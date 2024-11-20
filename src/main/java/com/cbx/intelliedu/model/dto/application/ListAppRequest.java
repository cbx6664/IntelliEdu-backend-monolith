package com.cbx.intelliedu.model.dto.application;

import com.cbx.intelliedu.common.dto.TableRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ListAppRequest extends TableRequest implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * Application Name
     */
    private String appName;

    /**
     * Application Type (0 - Grade, 1 - Evaluation)
     */
    private Integer type;

    /**
     * Scoring Strategy (0 - Custom, 1 - AI)
     */
    private Integer strategy;

    /**
     * Creator User ID
     */
    private Long userId;

    /**
     * Audit Status: 0 - Pending, 1 - Approved, 2 - Rejected
     */
    private Integer auditStatus;

    /**
     * Auditor User ID
     */
    private Long auditorId;

    private static final long serialVersionUID = 1L;
}