package com.cong.sqldog.core.sqlgenerate.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * JSON 构建器
 *
 * @author cong
 * @date 2024/08/01
 */
@Slf4j
public class JsonBuilder {
    JsonBuilder(){}
    /**
     * 构造数据 json
     * e.g. {"id": 1}
     *
     * @param dataList 数据列表
     * @return 生成的 json 数组字符串
     */
    public static String buildJson(List<Map<String, Object>> dataList) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(dataList);
    }
}
