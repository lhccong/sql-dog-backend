package com.cong.sqldog.core.sqlgenerate.generator;


import com.cong.sqldog.core.sqlgenerate.schema.TableSchema.Field;

import java.util.List;

/**
 * 数据生成器
 *
 * @author cong
 * @date 2024/08/01
 */
public interface DataGenerator {

    /**
     * 生成
     *
     * @param field 字段信息
     * @param rowNum 行数
     * @return 生成的数据列表
     */
    List<Object> doGenerate(Field field, int rowNum);

}
