package com.cong.sqldog.controller;

import com.cong.sqldog.common.BaseResponse;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.common.ResultUtils;
import com.cong.sqldog.core.sqlgenerate.builder.TableSchemaBuilder;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import com.cong.sqldog.exception.BusinessException;
import com.cong.sqldog.generate.GeneratorFacade;
import com.cong.sqldog.model.dto.sql.GenerateBySqlRequest;
import com.cong.sqldog.model.dto.sql.SqlAnalysisRequest;
import com.cong.sqldog.model.vo.sql.GenerateVO;
import com.cong.sqldog.model.vo.sql.SqlAnalysisVO;
import com.cong.sqldog.service.SqlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SQL 控制器
 *
 * @author cong
 * @date 2024/08/09
 */
@RestController
@RequestMapping("/sql")
@Slf4j
//@Tag(name = "SQL 生成相关")
public class SqlController {

    @Resource
    private SqlService sqlService;

    /**
     * 根据表结构生成SQL
     *
     * @param tableSchema 表概要
     * @return BaseResponse<GenerateVO>
     */
    @PostMapping("/generate/schema")
    @Operation(summary = "根据表结构生成SQL")
    public BaseResponse<GenerateVO> generateBySchema(@RequestBody TableSchema tableSchema) {
        return ResultUtils.success(GeneratorFacade.generateAll(tableSchema));
    }

    /**
     * SQL 评分
     *
     * @param sqlAnalysisRequest SQL 分析请求
     * @return SqlAnalysisVO
     */
    @PostMapping("/score")
    @Operation(summary = "SQL 评分")
    public SqlAnalysisVO scoreBySql(@RequestBody SqlAnalysisRequest sqlAnalysisRequest) {
        return sqlService.getSqlScore(sqlAnalysisRequest);
    }

    /**
     * 根据 SQL 获取 schema
     *
     * @param sqlRequest 根据 SQL 生成请求体
     * @return BaseResponse<TableSchema>
     */
    @PostMapping("/get/schema/sql")
    public BaseResponse<TableSchema> getSchemaBySql(@RequestBody GenerateBySqlRequest sqlRequest) {
        if (sqlRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取 tableSchema
        return ResultUtils.success(TableSchemaBuilder.buildFromSql(sqlRequest.getSql()));
    }


}
