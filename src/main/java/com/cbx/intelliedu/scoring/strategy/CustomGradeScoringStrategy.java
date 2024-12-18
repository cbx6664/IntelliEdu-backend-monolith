package com.cbx.intelliedu.scoring.strategy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cbx.intelliedu.model.dto.question.QuestionContent;
import com.cbx.intelliedu.model.entity.AnswerRecord;
import com.cbx.intelliedu.model.entity.Application;
import com.cbx.intelliedu.model.entity.Question;
import com.cbx.intelliedu.model.entity.Scoring;
import com.cbx.intelliedu.model.vo.QuestionVo;
import com.cbx.intelliedu.scoring.ScoringStrategy;
import com.cbx.intelliedu.scoring.annotation.ScoringStrategyConfig;
import com.cbx.intelliedu.service.QuestionService;
import com.cbx.intelliedu.service.ScoringService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 自定义打分类应用评分策略
 */
@ScoringStrategyConfig(appType = 0, scoringStrategy = 0)
public class CustomGradeScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringService scoringService;

    @Override
    public AnswerRecord doScore(List<String> answerList, Application application) throws Exception {
        Long appId = application.getId();
        // 1. 根据 id 查询到题目和题目结果信息（按分数降序排序）
        Question question = questionService.getQuestionByAppId(appId);

        List<Scoring> scoringList = scoringService.list(
                Wrappers.lambdaQuery(Scoring.class)
                        .eq(Scoring::getAppId, appId)
                        .orderByDesc(Scoring::getResultThreshold)
        );

        // 2. 统计用户的总得分
        int totalScore = 0;
        QuestionVo questionVo = QuestionVo.objToVo(question);
        List<QuestionContent> questionContent = questionVo.getQuestions();

        for (int i = 0; i < questionContent.size(); i++) {
            QuestionContent questionContentDTO = questionContent.get(i);
            String answer = answerList.get(i);  // 从 answerList 中拿到对应题目的答案

            // 遍历题目中的选项
            for (QuestionContent.Option option : questionContentDTO.getOptions()) {
                // 如果答案和选项的 key 匹配
                if (option.getKey().equals(answer)) {
                    int score = Optional.of(option.getGrade()).orElse(0);
                    totalScore += score;
                    break;  // 跳出循环，避免重复统计
                }
            }
        }

        // 3. 遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果
        Scoring maxScoringResult = scoringList.get(0);
        for (Scoring scoringResult : scoringList) {
            if (totalScore >= scoringResult.getResultThreshold()) {
                maxScoringResult = scoringResult;
                break;
            }
        }

        // 4. 构造返回值，填充答案对象的属性
        AnswerRecord answerRecord = new AnswerRecord();
        answerRecord.setAppId(appId);
        answerRecord.setAppType(application.getType());
        answerRecord.setStrategy(application.getStrategy());
        answerRecord.setAnswers(answerList);
        answerRecord.setResultId(maxScoringResult.getId());
        answerRecord.setResultName(maxScoringResult.getResultName());
        answerRecord.setResultDetail(maxScoringResult.getResultDetail());
        answerRecord.setResultImageUrl(maxScoringResult.getResultImageUrl());
        answerRecord.setResultGrade(totalScore);
        return answerRecord;
    }
}
