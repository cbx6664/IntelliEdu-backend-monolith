package com.cbx.intelliedu.manager;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cbx.intelliedu.constant.AIConstant.MAX_TOKEN_LENGTH;

@Component
@Slf4j
public class AiManager {
    @Test
    public void test() {
        System.out.println("1");
    }

    @Resource
    private OpenAiClient openAiClient;

    // 定义 vip 线程池和普通线程池
    private final ExecutorService vipThreadPool;
    private final ExecutorService normalThreadPool;

    public AiManager() {
        ThreadFactory vipThreadFactory = new CustomThreadFactory("VIPPool");
        ThreadFactory normalThreadFactory = new CustomThreadFactory("NormalPool");
        // 创建 VIP 用户线程池，设置较小的核心线程数和队列
        this.vipThreadPool = new ThreadPoolExecutor(
                5, // 核心线程数
                10, // 最大线程数
                60L, // 空闲线程存活时间
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50), // 队列容量
                vipThreadFactory,
                new ThreadPoolExecutor.AbortPolicy() // 拒绝策略，直接拒绝任务
        );

        // 创建普通用户线程池，设置较大的核心线程数和队列
        this.normalThreadPool = new ThreadPoolExecutor(
                10, // 核心线程数
                20, // 最大线程数
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                normalThreadFactory,
                new ThreadPoolExecutor.DiscardPolicy() // 拒绝策略，丢弃任务
        );
    }


    /**
     * 通用请求
     *
     * @param messageList
     * @param temperature
     * @return
     */
    public String doRequest(List<Message> messageList, double temperature) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .maxTokens(MAX_TOKEN_LENGTH)
                .temperature(temperature)
                .messages(messageList)
                .build();

        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletionRequest).execute();

        return chatCompletionResponse.choices().get(0).message().content();
    }

    public String doRequest(String systemMessage, String userMessage, double temperature) {
        List<Message> messages = new ArrayList<>();
        SystemMessage sysMessage = SystemMessage.from(systemMessage);
        UserMessage uMessage = UserMessage.from(userMessage);
        messages.add(sysMessage);
        messages.add(uMessage);
        return doRequest(messages, temperature);
    }

    /**
     * 通用流式请求
     *
     * @param temperature
     * @param messages
     * @return
     */
    public ChatCompletionRequest generalStreamRequest(double temperature, List<Message> messages) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .stream(true)
                .maxTokens(MAX_TOKEN_LENGTH)
                .temperature(temperature)
                .messages(messages)
                .build();

        return chatCompletionRequest;
    }

    /**
     * 通用流式请求，优化消息传递
     *
     * @param systemMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public ChatCompletionRequest generalStreamRequest(String systemMessage, String userMessage, double temperature) {
        List<Message> messages = new ArrayList<>();
        SystemMessage sysMessage = SystemMessage.from(systemMessage);
        UserMessage uMessage = UserMessage.from(userMessage);
        messages.add(sysMessage);
        messages.add(uMessage);
        return generalStreamRequest(temperature, messages);
    }


    /**
     * 结束线程
     */
    public void shutdownThreadPools() {
        vipThreadPool.shutdown();
        normalThreadPool.shutdown();
        try {
            if (!vipThreadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                vipThreadPool.shutdownNow();
            }
            if (!normalThreadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                normalThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Error shutting down thread pools", e);
            vipThreadPool.shutdownNow();
            normalThreadPool.shutdownNow();
        }
    }

    public void executeChatCompletionWithIsolation(ChatCompletionRequest chatCompletionRequest, SseEmitter emitter, CompletableFuture<String> future, boolean isVip) {
        // 选择线程池
        ExecutorService threadPool = isVip ? vipThreadPool : normalThreadPool;

        // 提交任务到对应的线程池
        threadPool.submit(() -> {
            String threadName = Thread.currentThread().getName();
            log.info("Task started in thread: {}", threadName); // 输出线程信息

            // 如果是普通线程池，暂停 10 秒
            if (!isVip) {
                try {
                    log.info("Pausing task in thread: {} for 10 seconds", threadName);
                    Thread.sleep(10000); // 暂停 10 秒
                } catch (InterruptedException e) {
                    log.error("Interrupted while sleeping in thread: {}", threadName, e);
                    Thread.currentThread().interrupt(); // 恢复中断状态
                }
            }

            try {
                executeChatCompletion(chatCompletionRequest, emitter, future);
            } catch (Exception e) {
                log.error("error in executeChatCompletion with respective threadPool", e);
                emitter.completeWithError(e);
                future.completeExceptionally(e);
            }
            log.info("Task completed in thread: {}", threadName);
        });

    }

    public void executeChatCompletion(ChatCompletionRequest chatCompletionRequest, SseEmitter emitter, CompletableFuture<String> future) {
        StringBuilder contentBuilder = new StringBuilder();
        AtomicInteger flag = new AtomicInteger(0);

        openAiClient.chatCompletion(chatCompletionRequest)
                .onPartialResponse(response -> {
                    String message = response.choices().get(0).delta().content();


                    if (message != null) {
                        // openai 返回的字符中可能包括换行符 \n，而 SSE 协议中换行符有特殊含义，用于分隔消息的不同字段，若不处理会造成客户端解析出错
                        // 解决：将换行符替换为空格，注意不能直接删除所有空白字符，因为单词之间有空格
                        message = message.replaceAll("\\R", " ");

                        for (char c : message.toCharArray()) {
                            if (c == '{') {
                                flag.incrementAndGet();
                            }
                            if (flag.get() > 0) {
                                contentBuilder.append(c);
                            }
                            if (c == '}') {
                                flag.decrementAndGet();
                                if (flag.get() == 0) {
                                    try {
//                                        if(Thread.currentThread().getName()!="")
                                        emitter.send(contentBuilder.toString());
                                        contentBuilder.setLength(0);
                                    } catch (IOException e) {
                                        log.error("Error sending partial JSON object", e);
                                        emitter.completeWithError(e);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                })
                .onComplete(() -> {
                    future.complete(contentBuilder.toString());
                    emitter.complete();
                })
                .onError(throwable -> {
                    log.error("Error during chat completion", throwable);
                    future.completeExceptionally(throwable);
                    emitter.completeWithError(throwable);
                })
                .execute();

        future.whenComplete((response, throwable) -> {
            if (throwable != null) {
                log.error("Future completed with error", throwable);
                emitter.completeWithError(throwable);
            }
        });
    }


}
