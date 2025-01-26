package com.cong.sqldog.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.cong.sqldog.common.BaseResponse;
import com.cong.sqldog.common.ErrorCode;
import com.cong.sqldog.common.ResultUtils;
import com.cong.sqldog.core.sqlgenerate.builder.TableSchemaBuilder;
import com.cong.sqldog.core.sqlgenerate.model.dto.GenerateByAutoRequest;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema.Field;
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
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @PostMapping("/get/schema/auto")
    public BaseResponse<TableSchema> getSchemaByAuto(@RequestBody GenerateByAutoRequest autoRequest) {
        if (autoRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(TableSchemaBuilder.buildFromAuto(autoRequest.getContent()));
    }

    /**
     * SQL 评分
     *
     * @param sqlAnalysisRequest SQL 分析请求
     * @return SqlAnalysisVO
     */
    @PostMapping("/score")
    @Operation(summary = "SQL 评分")
    public BaseResponse<SqlAnalysisVO> scoreBySql(@RequestBody SqlAnalysisRequest sqlAnalysisRequest) {
        return ResultUtils.success(sqlService.getSqlScore(sqlAnalysisRequest));
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

    /**
     * 下载模拟数据 Excel
     *
     * @param response
     */
    @PostMapping("/download/data/excel")
    public void downloadDataExcel(@RequestBody GenerateVO generateVO, HttpServletResponse response) {
        TableSchema tableSchema = generateVO.getTableSchema();
        String tableName = tableSchema.getTableName();
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里 URLEncoder.encode 可以防止中文乱码
            String fileName = URLEncoder.encode(tableName + "表数据", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            // 设置表头
            List<List<String>> headList = new ArrayList<>();
            for (Field field : tableSchema.getFieldList()) {
                List<String> head = Collections.singletonList(field.getFieldName());
                headList.add(head);
            }
            List<String> fieldNameList = tableSchema.getFieldList().stream()
                    .map(Field::getFieldName).toList();
            // 设置数据
            List<List<Object>> dataList = new ArrayList<>();
            for (Map<String, Object> data : generateVO.getDataList()) {
                List<Object> dataRow = fieldNameList.stream().map(data::get).toList();
                dataList.add(dataRow);
            }
            // 这里需要设置不关闭流
            EasyExcelFactory.write(response.getOutputStream())
                    .autoCloseStream(Boolean.FALSE)
                    .head(headList)
                    .sheet(tableName + "表")
                    .doWrite(dataList);
        } catch (Exception e) {
            // 重置 response
            response.reset();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        }
    }


}
