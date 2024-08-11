package com.cong.sqldog.generate;

import com.cong.sqldog.model.entity.ExecuteInfo;

/**
 * 代码生成器
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
public class CodeGenerator {

    /**
     * 用法：追加process(数据类.class, "数据别名");
     */
    public static void main(String[] args) {
        // 代码生成处理器
        new GenerateProcessor()
                // 生成项目路径
                .packageName("com.cong.sqldog")
                // 排除字段策略
                .exclusionStrategy("serialVersionUID", "isDelete","updateTime","createTime")
                // 继续追加process(数据类.class, "数据别名")
                .process(ExecuteInfo.class, "SQL执行记录");

    }
}
