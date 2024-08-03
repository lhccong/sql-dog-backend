package com.cong.sqldog.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.model.dto.tableInfo.TableInfoAddRequest;
import com.cong.sqldog.model.dto.tableInfo.TableInfoEditRequest;
import com.cong.sqldog.model.dto.tableInfo.TableInfoQueryRequest;
import com.cong.sqldog.model.dto.tableInfo.TableInfoUpdateRequest;
import com.cong.sqldog.model.entity.TableInfo;
import com.cong.sqldog.model.vo.TableInfoVO;

/**
 * 表信息服务
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
public interface TableInfoService extends IService<TableInfo> {

    /**
     * 校验数据
     *
     * @param tableInfo 数据
     * @param add       对创建的数据进行校验
     */
    void validTableInfo(TableInfo tableInfo, boolean add);

    /**
     * 获取查询条件
     *
     * @param tableInfoQueryRequest 查询条件
     * @return QueryWrapper
     */
    QueryWrapper<TableInfo> getQueryWrapper(TableInfoQueryRequest tableInfoQueryRequest);

    /**
     * 获取表信息封装
     *
     * @param tableInfo 表信息实体
     * @return TableInfoVO
     */
    TableInfoVO getTableInfoVO(TableInfo tableInfo);

    /**
     * 分页获取表信息封装
     *
     * @param tableInfoPage 分页数据
     * @return Page<TableInfoVO>
     */
    Page<TableInfoVO> getTableInfoVoPage(Page<TableInfo> tableInfoPage);


    long addTableInfo(TableInfoAddRequest tableInfoAddRequest);

    boolean deleteTableInfo(DeleteRequest deleteRequest);

    boolean editTableInfo(TableInfoEditRequest tableInfoEditRequest);

    boolean updateTableInfo(TableInfoUpdateRequest updateRequest);


}
