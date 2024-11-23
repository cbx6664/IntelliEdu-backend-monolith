package com.cbx.intelliedu.manager;

import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.*;
import lombok.extern.slf4j.Slf4j;
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
        ExecutorService threadPool = getThreadPool(isVip);

        threadPool.submit(() -> {
            String threadName = Thread.currentThread().getName();
            log.info("Task started in thread: {}", threadName);

            try {
                StringBuilder contentBuilder = new StringBuilder();
                AtomicInteger flag = new AtomicInteger(0);

                openAiClient.chatCompletion(chatCompletionRequest)
                        .onPartialResponse(response -> {
                            String message = response.choices().get(0).delta().content();

                            if (message != null) {
                                message = message.replaceAll("\\R", " ");
                                processMessage(contentBuilder, flag, message, emitter, isVip);
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
            } catch (Exception e) {
                log.error("Error in executeChatCompletion with respective threadPool", e);
                emitter.completeWithError(e);
                future.completeExceptionally(e);
            }
            log.info("Task completed in thread: {}", threadName);
        });
    }

    private ExecutorService getThreadPool(boolean isVip) {
        return isVip ? vipThreadPool : normalThreadPool;
    }

    private void processMessage(StringBuilder contentBuilder, AtomicInteger flag, String message, SseEmitter emitter, boolean isVip) {
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
                        if (!isVip) {
                            Thread.sleep(10000L);
                        }
                        emitter.send(contentBuilder.toString());
                        contentBuilder.setLength(0);
                    } catch (IOException e) {
                        log.error("Error sending partial JSON object", e);
                        emitter.completeWithError(e);
                        return;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}
