package com.cong.sqldog.core.sqlgenerate.builder.sql;


import cn.hutool.core.map.WeakConcurrentMap;
import com.cong.sqldog.infrastructure.common.ErrorCode;
import com.cong.sqldog.infrastructure.exception.BusinessException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQL 方言工厂
 * 工厂 + 单例模式，降低开销
 */
public class SQLDialectFactory {

    /**
     * className => 方言实例映射
     */
    private static final Map<String, SQLDialect> DIALECT_POOL = new ConcurrentHashMap<>();

    private static final WeakConcurrentMap<String, Object> lockMap = new WeakConcurrentMap<>();

    private SQLDialectFactory() {
    }

    /**
     * 获取方言实例
     *
     * @param className 类名
     * @return {@link SQLDialect }
     */
    public static SQLDialect getDialect(String className) {
        SQLDialect dialect = DIALECT_POOL.get(className);
        if (null == dialect) {
            Object lock;
            synchronized (lockMap) {
                lock = lockMap.computeIfAbsent(className, key -> new Object());
            }
            synchronized (lock) {
                dialect = DIALECT_POOL.computeIfAbsent(className,
                        key -> {
                            try {
                                return (SQLDialect) Class.forName(className).getDeclaredConstructor().newInstance();
                            } catch (Exception e) {
                                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                            }
                        });
            }
        }
        return dialect;
    }
}
