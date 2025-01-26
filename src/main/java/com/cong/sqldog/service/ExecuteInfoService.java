package com.cong.sqldog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.sqldog.infrastructure.common.DeleteRequest;
import com.cong.sqldog.infrastructure.common.ReviewRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoAddRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoEditRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoQueryRequest;
import com.cong.sqldog.model.dto.executeInfo.ExecuteInfoUpdateRequest;
import com.cong.sqldog.model.entity.ExecuteInfo;
import com.cong.sqldog.model.vo.ExecuteInfoVO;


/**
 * SQL执行记录服务
 *
 * @author tian
 */
public interface ExecuteInfoService extends IService<ExecuteInfo> {

    /**
     * 校验数据
     *
     * @param executeInfo 数据
     * @param add 对创建的数据进行校验
     */
    void validExecuteInfo(ExecuteInfo executeInfo, boolean add);

    /**
     * 获取查询条件
     *
     * @param executeInfoQueryRequest 查询条件
     * @return QueryWrapper
     */
    QueryWrapper<ExecuteInfo> getQueryWrapper(ExecuteInfoQueryRequest executeInfoQueryRequest);
    
    /**
     * 获取SQL执行记录封装
     *
     * @param executeInfo SQL执行记录实体
     * @return ExecuteInfoVO
     */
    ExecuteInfoVO getExecuteInfoVO(ExecuteInfo executeInfo);

    /**
     * 分页获取SQL执行记录封装
     *
     * @param executeInfoPage 分页数据
     * @return Page<ExecuteInfoVO>
     */
    Page<ExecuteInfoVO> getExecuteInfoVOPage(Page<ExecuteInfo> executeInfoPage);


    /**
     * 创建SQL执行记录
     *
     * @param executeInfoAddRequest SQL执行记录创建请求
     * @return SQL执行记录ID
     */
    long addExecuteInfo(ExecuteInfoAddRequest executeInfoAddRequest);

    /**
     * 删除SQL执行记录
     *
     * @param deleteRequest SQL执行记录删除请求
     * @return boolean 是否删除成功
     */
    Boolean deleteExecuteInfo(DeleteRequest deleteRequest);

    /**
     * 编辑SQL执行记录
     *
     * @param executeInfoEditRequest SQL执行记录编辑请求
     * @return boolean 是否编辑成功
     */
    Boolean editExecuteInfo(ExecuteInfoEditRequest executeInfoEditRequest);

    /**
     * 更新SQL执行记录
     *
     * @param executeInfoUpdateRequest SQL执行记录更新请求
     * @return boolean 是否更新成功
     */
    Boolean updateExecuteInfo(ExecuteInfoUpdateRequest executeInfoUpdateRequest);

    /**
     * 根据id获取SQL执行记录（封装类）
     * @param id SQL执行记录id
     * @return ExecuteInfo SQL执行记录
     */
    ExecuteInfoVO getExecuteInfoById(long id);

/**
     * 分页获取SQL执行记录列表（仅管理员可用）
     *
     * @param executeInfoQueryRequest SQL执行记录查询请求
     * @return Page<ExecuteInfo> SQL执行记录列表
     */
    Page<ExecuteInfo> listExecuteInfoByPage(ExecuteInfoQueryRequest executeInfoQueryRequest);


    Page<ExecuteInfoVO> listExecuteInfoVoByPage(ExecuteInfoQueryRequest executeInfoQueryRequest);

    /**
     * 分页获取当前登录用户创建的SQL执行记录列表(封装类)
     *
     * @param executeInfoQueryRequest SQL执行记录查询请求
     * @return Page<ExecuteInfoVO> SQL执行记录列表
     */
    Page<ExecuteInfoVO> listMyExecuteInfoVOByPage(ExecuteInfoQueryRequest executeInfoQueryRequest);

    /**
     * 审核SQL执行记录
     *
     * @param reviewRequest 审核请求
     * @return boolean 是否审核成功
     */
    Boolean reviewExecuteInfo(ReviewRequest reviewRequest);
}
