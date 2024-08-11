package com.cong.sqldog.model.dto.executeInfo;

import com.cong.sqldog.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 查询SQL执行记录请求
 *
 * @author tian
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExecuteInfoQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String sqlContent;

    private String sqlAnalyzeResult;

    private Integer reviewStatus;

    private String reviewMessage;


    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 创建用户 id
     */
    private Long userId;

    @Serial
    private static final long serialVersionUID = 1L;
}