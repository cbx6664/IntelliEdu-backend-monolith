package com.cbx.intelliedu.model.dto.answerrecord;

import com.cbx.intelliedu.common.dto.TableRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ListMyAnswerRequest extends TableRequest implements Serializable {

    private static final long serialVersionUID = -3092260871645428664L;

}
