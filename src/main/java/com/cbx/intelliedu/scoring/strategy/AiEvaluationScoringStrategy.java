package com.cbx.intelliedu.scoring.strategy;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.cbx.intelliedu.manager.AiManager;
import com.cbx.intelliedu.model.dto.question.QuestionContent;
import com.cbx.intelliedu.model.dto.scoring.AiDoScoreRequest;
import com.cbx.intelliedu.model.entity.AnswerRecord;
import com.cbx.intelliedu.model.entity.Application;
import com.cbx.intelliedu.model.entity.Question;
import com.cbx.intelliedu.model.vo.QuestionVo;
import com.cbx.intelliedu.scoring.ScoringStrategy;
import com.cbx.intelliedu.scoring.annotation.ScoringStrategyConfig;
import com.cbx.intelliedu.service.QuestionService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.cbx.intelliedu.constant.AIConstant.AI_EVALUATION_SCORING_SYSTEM_MESSAGE;
import static com.cbx.intelliedu.constant.AIConstant.AI_SCORING_LOCK;


/**
 * AI 测评类应用评分策略
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AiEvaluationScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 本地缓存
     */
    private final Cache<String, String> answerCacheMap =
            Caffeine.newBuilder().initialCapacity(1024)
                    // 缓存 5 分钟移除
                    .expireAfterAccess(5L, TimeUnit.MINUTES)
                    .build();


    private String getAiEvaluationScoringUserMessage(Application application, List<QuestionContent> questionContentList, List<String> answerList) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append("Application name: ").append(application.getAppName()).append("\n");
        userMessage.append("Application description: ").append(application.getDescription()).append("\n");
        List<AiDoScoreRequest> aiDoScoreRequestList = new ArrayList<>();
        for (int i = 0; i < questionContentList.size(); i++) {
            AiDoScoreRequest aiDoScoreRequest = new AiDoScoreRequest();
            aiDoScoreRequest.setTitle(questionContentList.get(i).getTitle());
            aiDoScoreRequest.setUserAnswer(answerList.get(i));
            aiDoScoreRequestList.add(aiDoScoreRequest);
        }
        userMessage.append("List of questions and user answers: ").append(JSONUtil.toJsonStr(aiDoScoreRequestList));
        return userMessage.toString();
    }


    @Override
    public AnswerRecord doScore(List<String> answerList, Application application) throws Exception {
        Long appId = application.getId();
        String jsonAnswerList = JSONUtil.toJsonStr(answerList);
        String cacheKey = buildCacheKey(appId, jsonAnswerList);
        String resultCache = answerCacheMap.getIfPresent(cacheKey);
        // 缓存命中
        if (StringUtils.isNotBlank(resultCache)) {
            AnswerRecord answerRecord = JSONUtil.toBean(resultCache, AnswerRecord.class);
            answerRecord.setAppId(appId);
            answerRecord.setAppType(application.getType());
            answerRecord.setStrategy(application.getStrategy());
            answerRecord.setAnswers(answerList);
            return answerRecord;
        }

        // 定义锁
        RLock lock = redissonClient.getLock(AI_SCORING_LOCK + cacheKey);

        try {
            // 竞争锁
            boolean tryLock = lock.tryLock(3, 15, TimeUnit.SECONDS);
            // 没抢到锁，强行返回
            if (!tryLock) {
                return null;
            }
            // 1. 根据 appId 查询到对应题目
            Question question = questionService.getQuestionByAppId(appId);
            QuestionVo questionVo = QuestionVo.objToVo(question);
            List<QuestionContent> questionContent = questionVo.getQuestions();
            // 2. 调用 AI 获取结果
            // 封装 Prompt
            String userMessage = getAiEvaluationScoringUserMessage(application, questionContent, answerList);
            // AI 生成
            String result = aiManager.doRequest(AI_EVALUATION_SCORING_SYSTEM_MESSAGE, userMessage, 1);
            // 结果处理
            int start = result.indexOf("{");
            int end = result.lastIndexOf("}");
            String json = result.substring(start, end + 1);

            // 缓存结果
            answerCacheMap.put(cacheKey, json);

            // 3. 构造返回值，填充答案对象的属性
            AnswerRecord answerRecord = JSONUtil.toBean(json, AnswerRecord.class);
            answerRecord.setAppId(appId);
            answerRecord.setAppType(application.getType());
            answerRecord.setStrategy(application.getStrategy());
            answerRecord.setAnswers(answerList);
            return answerRecord;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (lock != null && lock.isLocked()) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    private String buildCacheKey(Long appId, String answerList) {
        return MD5.create().digestHex(appId + ":" + answerList);
    }


}
