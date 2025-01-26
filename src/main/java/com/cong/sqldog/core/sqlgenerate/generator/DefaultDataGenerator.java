package com.cong.sqldog.core.sqlgenerate.generator;

import cn.hutool.core.date.DateUtil;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import com.cong.sqldog.infrastructure.utils.IdWorkerUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 默认数据生成器
 *
 * @author cong
 * @date 2024/08/01
 */
public class DefaultDataGenerator implements DataGenerator {
    @Override
    public List<Object> doGenerate(TableSchema.Field field, int rowNum) {
        String mockParams = field.getMockParams();
        List<Object> list = new ArrayList<>(rowNum);
        //如果模拟参数有值采用递增
        if (field.isPrimaryKey()) {
            getMockPrimaryKeyData(rowNum, mockParams, list);
            return list;
        }
        // 使用默认值
        String defaultValue = field.getDefaultValue();
        // 特殊逻辑，日期要伪造数据
        if ("CURRENT_TIMESTAMP".equals(defaultValue)) {
            defaultValue = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        }
        if (StringUtils.isNotBlank(defaultValue)) {
            for (int i = 0; i < rowNum; i++) {
                list.add(defaultValue);
            }
        }
        return list;
    }

    private static void getMockPrimaryKeyData(int rowNum, String mockParams, List<Object> list) {
        if (StringUtils.isNotBlank(mockParams)) {
            int initValue = 1;
            for (int i = 0; i < rowNum; i++) {
                list.add(String.valueOf(initValue + i));
            }
        } else {
            IdWorkerUtil idWorkerUtil = new IdWorkerUtil();
            for (int i = 0; i < rowNum; i++) {
                list.add(String.valueOf(idWorkerUtil.nextId()));
            }
        }
    }
}
