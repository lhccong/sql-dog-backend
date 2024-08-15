package com.cong.sqldog.core.sqlgenerate.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class GenerateByAutoRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 内容
     */
    private String content;
}