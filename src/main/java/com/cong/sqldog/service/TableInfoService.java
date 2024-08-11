package com.cong.sqldog.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.common.ReviewRequest;
import com.cong.sqldog.model.dto.tableinfo.TableInfoAddRequest;
import com.cong.sqldog.model.dto.tableinfo.TableInfoEditRequest;
import com.cong.sqldog.model.dto.tableinfo.TableInfoQueryRequest;
import com.cong.sqldog.model.dto.tableinfo.TableInfoUpdateRequest;
import com.cong.sqldog.model.entity.TableInfo;
import com.cong.sqldog.model.vo.TableInfoVo;

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
     * 分页获取表信息
     */
    Page<TableInfo> listTableInfoByPage( TableInfoQueryRequest tableInfoQueryRequest);

    /**
     * 获取表信息封装
     *
     * @param tableInfo 表信息实体
     * @return TableInfoVO
     */
    TableInfoVo getTableInfoVo(TableInfo tableInfo);

    /**
     * 分页获取表信息封装
     *
     * @param tableInfoPage 分页数据
     * @return Page<TableInfoVO>
     */
    Page<TableInfoVo> getTableInfoVoPage(Page<TableInfo> tableInfoPage);

    /**
     * 新增表信息
     */
    long addTableInfo(TableInfoAddRequest tableInfoAddRequest);

    /**
     * 删除表信息
     */
    boolean deleteTableInfo(DeleteRequest deleteRequest);

    /**
     * 编辑表信息
     */
    boolean editTableInfo(TableInfoEditRequest tableInfoEditRequest);

    /**
     * 更新表信息
     */
    boolean updateTableInfo(TableInfoUpdateRequest updateRequest);

    /**
     * 根据 id 获取表信息（封装类）
     */
    TableInfoVo getTableInfoVoById(long id);

    /**
     * 分页获取表信息列表（封装类）
     */
    Page<TableInfoVo> listTableInfoVoByPage(TableInfoQueryRequest tableInfoQueryRequest);

    /**
     * 分页获取我的表信息列表（封装类）
     */
    Page<TableInfoVo> listMyTableInfoVoByPage(TableInfoQueryRequest tableInfoQueryRequest);

    /**
     * 表信息状态审核
     */
    Boolean doTableInfoReview( ReviewRequest reviewRequest);
}
