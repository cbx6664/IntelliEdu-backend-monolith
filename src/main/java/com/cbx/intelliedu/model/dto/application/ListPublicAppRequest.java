package com.cbx.intelliedu.model.dto.application;

import com.cbx.intelliedu.common.dto.TableRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ListPublicAppRequest extends TableRequest implements Serializable {
    /**
     * Application Name
     */
    private String appName;

    private static final long serialVersionUID = 5654129428599598117L;
}
