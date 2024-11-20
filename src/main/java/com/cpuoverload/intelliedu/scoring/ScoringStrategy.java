package com.cpuoverload.intelliedu.scoring;


import com.cpuoverload.intelliedu.model.entity.AnswerRecord;
import com.cpuoverload.intelliedu.model.entity.Application;

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