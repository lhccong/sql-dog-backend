package com.cong.sqldog.controller;

import com.cong.sqldog.common.BaseResponse;
import com.cong.sqldog.common.ResultUtils;
import com.cong.sqldog.core.sqlgenerate.schema.TableSchema;
import com.cong.sqldog.generate.GeneratorFacade;
import com.cong.sqldog.model.vo.sql.GenerateVO;
import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping("/generate/schema")
    @Operation(summary = "根据表结构生成SQL")
    public BaseResponse<GenerateVO> generateBySchema(@RequestBody TableSchema tableSchema) {
        return ResultUtils.success(GeneratorFacade.generateAll(tableSchema));
    }
}
