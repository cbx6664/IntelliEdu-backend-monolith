package com.cpuoverload.intelliedu.model.dto.application;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateAppRequest implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * Application Name
     */
    private String appName;

    /**
     * Application Description
     */
    private String description;

    /**
     * Application Image URL
     */
    private String imageUrl;

    private static final long serialVersionUID = 1L;
}
