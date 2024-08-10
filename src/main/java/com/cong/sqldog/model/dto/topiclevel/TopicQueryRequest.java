package com.cong.sqldog.model.dto.topiclevel;

import com.cong.sqldog.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 查询题目请求
 *
 * @author shing
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TopicQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 关卡中文名
     */
    private String title;

    /**
     * 关卡类别，custom 自定义、system 系统
     */
    private String type;

    /**
     * 搜索词
     */
    private String searchText;

    @Serial
    private static final long serialVersionUID = 1L;
}
