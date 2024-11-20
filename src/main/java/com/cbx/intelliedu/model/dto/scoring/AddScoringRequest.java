package com.cbx.intelliedu.model.dto.scoring;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AddScoringRequest implements Serializable {
    private static final long serialVersionUID = 6504624271615045534L;

    /**
     * Application ID
     */
    private Long appId;

    /**
     * Result name
     */
    private String resultName;

    /**
     * Result Detail
     */
    private String resultDetail;

    /**
     * Result Image URL
     */
    private String resultImageUrl;

    /**
     * Score Threshold For This Result, Intended For Grade-Type Applications
     */
    private Integer resultThreshold;

    /**
     * Result Attribute Array (JSON), Intended For Evaluation-Type Applications
     */
    private List<String> resultAttributes;
}
