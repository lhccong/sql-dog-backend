package com.cong.sqldog.core.sqlgenerate.generator;


import com.cong.sqldog.core.sqlgenerate.model.enums.MockTypeEnum;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * 数据生成器工厂
 * <p>
 * <p>
 * 工厂 + 单例模式，降低开销
 *
 * @author cong
 * @date 2024/08/01
 */
public class DataGeneratorFactory {
    private static final Map<MockTypeEnum, DataGenerator> mockTypeDataGeneratorMap;

    static {
        //模拟类型 => 生成器映射
        mockTypeDataGeneratorMap = new EnumMap<>(MockTypeEnum.class);
        mockTypeDataGeneratorMap.put(MockTypeEnum.NONE, new DefaultDataGenerator());
        mockTypeDataGeneratorMap.put(MockTypeEnum.FIXED, new FixedDataGenerator());
        mockTypeDataGeneratorMap.put(MockTypeEnum.RANDOM, new RandomDataGenerator());
        mockTypeDataGeneratorMap.put(MockTypeEnum.RULE, new RuleDataGenerator());
        mockTypeDataGeneratorMap.put(MockTypeEnum.INCREASE, new IncreaseDataGenerator());
    }

    private DataGeneratorFactory() {
    }

    /**
     * 获取实例
     *
     * @param mockTypeEnum mock 类型枚举
     * @return {@link DataGenerator }
     */
    public static DataGenerator getGenerator(MockTypeEnum mockTypeEnum) {
        mockTypeEnum = Optional.ofNullable(mockTypeEnum).orElse(MockTypeEnum.NONE);
        return mockTypeDataGeneratorMap.get(mockTypeEnum);
    }
}
