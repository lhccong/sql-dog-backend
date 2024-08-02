package com.cong.sqldog.core.sqlgenerate.model.dto;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Java 对象生成封装类
 *
 * @author cong
 * @date 2024/08/02
 */
@Data
@Accessors(chain = true)
public class JavaObjectGenerateDTO {

    /**
     * 类名
     */
    private String className;

    /**
     * 对象名
     */
    private String objectName;

    /**
     * 列信息列表
     */
    private List<FieldObjectDTO> fieldList;

    /**
     * 列信息
     */
    @Data
    @Accessors(chain = true)
    public static class FieldObjectDTO {
        /**
         * set 方法名
         */
        private String setMethod;

        /**
         * 值
         */
        private String value;
    }

}
