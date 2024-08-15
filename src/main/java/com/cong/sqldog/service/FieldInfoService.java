package com.cong.sqldog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.model.dto.fieldinfo.*;
import com.cong.sqldog.model.entity.FieldInfo;
import com.cong.sqldog.model.vo.FieldInfoVO;

/**
* @author 香香
* @description 针对表【field_info(字段信息)】的数据库操作Service
* @createDate 2024-08-06 01:21:20
*/
public interface FieldInfoService extends IService<FieldInfo> {

    /**
     * 添加字段信息
     *
     * @param fieldInfoAddRequest 新增字段信息请求
     * @return long
     */
    long addFieldInfo(FieldInfoAddRequest fieldInfoAddRequest);

    /**
     * 删除字段信息
     *
     * @param deleteRequest 删除字段信息请求
     * @return boolean
     */
    boolean deleteFieldInfo(DeleteRequest deleteRequest);

    /**
     * 编辑字段信息（给用户使用）
     *
     * @param fieldInfoEditRequest 编辑字段信息请求
     * @return boolean
     */
    boolean editFieldInfo(FieldInfoEditRequest fieldInfoEditRequest);


    /**
     * 根据 id 获取字段信息（封装类）
     *
     * @param fieldInfo 字段信息
     * @return FieldInfoVO
     */
    FieldInfoVO getFieldInfoVO(FieldInfo fieldInfo);

    /**
     * 获取查询条件
     *
     * @param fieldInfoQueryRequest 查询字段信息请求
     * @return QueryWrapper<FieldInfo>
     */
    QueryWrapper<FieldInfo> getQueryWrapper(FieldInfoQueryRequest fieldInfoQueryRequest);

    /**
     * 分页获取字段信息封装
     *
     * @param fieldInfoPage 分页获取字段信息请求
     * @return Page<FieldInfoVO>
     */
    Page<FieldInfoVO> getFieldInfoVoPage(Page<FieldInfo> fieldInfoPage);

    /**
     * 更新字段信息（给管理员使用）
     *
     * @param fieldInfoUpdateRequest 更新字段信息请求
     * @return boolean
     */
    boolean updateFieldInfoByAdmin(FieldInfoUpdateRequest fieldInfoUpdateRequest);

    /**
     * 根据 id 查询字段信息
     * @param id id
     * @return  FieldInfoVO
     */
    FieldInfoVO getFieldInfoVoById(long id);

    /**
     * 分页获取新段信息列表
     * @param fieldInfoQueryRequest 查询字段信息请求
     * @return Page<FieldInfo>
     */
    Page<FieldInfo> listFieldInfoByPage(FieldInfoQueryRequest fieldInfoQueryRequest);

    /**
     * 分页获取字段信息列表
     * @param fieldInfoQueryRequest 查询字段信息请求
     * @return Page<FieldInfoVO>
     */
    Page<FieldInfoVO> listFieldInfoVoByPage(FieldInfoQueryRequest fieldInfoQueryRequest);

    /**
     * 分页获取我的字段信息列表
     * @param fieldInfoQueryRequest 查询字段信息请求
     * @return Page<FieldInfoVO>
     */
    Page<FieldInfoVO> listMyFieldInfoVOByPage(FieldInfoQueryRequest fieldInfoQueryRequest);


    /**
     * 根据 id 更改审批字段状态
     *
     * @param fieldInfoEditReviewStatusRequest 更改字段状态信息请求
     * @return Boolean
     */
    Boolean editReviewStatus(FieldInfoEditReviewStatusRequest fieldInfoEditReviewStatusRequest);
}
