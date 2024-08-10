package com.cong.sqldog.model.vo.sql;

import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * 生成 VO
 *
 * @author cong
 * @date 2024/08/09
 */
@Data
@Accessors(chain = true)
public class GenerateVO {

    /**
     * 表架构
     */
    private TableSchema tableSchema;

    /**
     * 创建 SQL
     */
    private String createSql;

    /**
     * 数据列表
     */
    private List<Map<String, Object>> dataList;

    /**
     * 插入 SQL
     */
    private String insertSql;

    /**
     * 数据 json
     */
    private String dataJson;

    /**
     * Java 实体代码
     */
    private String javaEntityCode;

    /**
     * Java 对象代码
     */
    private String javaObjectCode;

    /**
     * TypeScript 类型代码
     */
    private String typescriptTypeCode;

}
