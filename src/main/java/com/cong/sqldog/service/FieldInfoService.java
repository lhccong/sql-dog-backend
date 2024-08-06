package com.cong.sqldog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cong.sqldog.common.BaseResponse;
import com.cong.sqldog.common.DeleteRequest;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoAddRequest;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoEditRequest;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoQueryRequest;
import com.cong.sqldog.model.dto.fieldinfo.FieldInfoUpdateRequest;
import com.cong.sqldog.model.entity.FieldInfo;
import com.cong.sqldog.model.vo.FieldInfoVO;
import com.cong.sqldog.model.vo.UserVO;

/**
* @author 香香
* @description 针对表【field_info(字段信息)】的数据库操作Service
* @createDate 2024-08-06 01:21:20
*/
public interface FieldInfoService extends IService<FieldInfo> {

    /**
     * 添加字段信息
     *
     * @param fieldInfoAddRequest
     * @return
     */
    long addFieldInfo(FieldInfoAddRequest fieldInfoAddRequest);

    /**
     * 删除字段信息
     *
     * @param deleteRequest
     * @return
     */
    boolean deleteFieldInfo(DeleteRequest deleteRequest);

    /**
     * 编辑字段信息（给用户使用）
     *
     * @param fieldInfoEditRequest
     * @return
     */
    boolean editFieldInfo(FieldInfoEditRequest fieldInfoEditRequest);

    /**
     * 根据 id 获取字段信息（封装类）
     *
     * @param fieldInfo
     * @param userVo
     * @return
     */
    FieldInfoVO getFieldInfoVO(FieldInfo fieldInfo, UserVO userVo);

    /**
     * 获取查询条件
     *
     * @param fieldInfoQueryRequest
     * @return
     */
    QueryWrapper<FieldInfo> getQueryWrapper(FieldInfoQueryRequest fieldInfoQueryRequest);

    /**
     * 分页获取字段信息封装
     *
     * @param fieldInfoPage
     * @return
     */
    Page<FieldInfoVO> getFieldInfoVoPage(Page<FieldInfo> fieldInfoPage);

    /**
     * 更新字段信息（给管理员使用）
     *
     * @param fieldInfoUpdateRequest
     * @return
     */
    boolean editFieldInfoByAdmin(FieldInfoUpdateRequest fieldInfoUpdateRequest);
}
