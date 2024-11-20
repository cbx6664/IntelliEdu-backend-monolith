package com.cbx.intelliedu.scoring;


import com.cbx.intelliedu.model.entity.AnswerRecord;
import com.cbx.intelliedu.model.entity.Application;

import java.util.List;

/**
 * 评分策略
 */
public interface ScoringStrategy {

    /**
     * 执行评分
     *
     * @param answers
     * @param application
     * @return
     * @throws Exception
     */
    AnswerRecord doScore(List<String> answers, Application application) throws Exception;
}