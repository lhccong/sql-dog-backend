package com.cong.sqldog.model.vo;

import com.cong.sqldog.model.entity.FieldInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @Author 香香
 * @Date 2024-08-06 01:56
 **/
@Data
public class FieldInfoVO {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段信息（json）
     */
    private String content;

    /**
     * 状态（0-待审核, 1-通过, 2-拒绝）
     */
    private Integer reviewStatus;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建对象信息
     */
    private UserVO userVO;



    /**
     * 封装类转对象
     */
    public static FieldInfo voToObj(FieldInfoVO fieldInfoVO) {
        if (fieldInfoVO == null) {
            return null;
        }
        FieldInfo fieldInfo = new FieldInfo();
        BeanUtils.copyProperties(fieldInfoVO, fieldInfo);
        return fieldInfo;
    }

    /**
     * 对象转封装类
     */
    public static FieldInfoVO objToVo(FieldInfo fieldInfo, UserVO userVO) {
        if (fieldInfo == null) {
            return null;
        }
        FieldInfoVO fieldInfoVO = new FieldInfoVO();
        BeanUtils.copyProperties(fieldInfo, fieldInfoVO);
        fieldInfoVO.setUserVO(userVO);
        return fieldInfoVO;
    }

    /**
     * 对象转封装类
     */
    public static FieldInfoVO objToVo(FieldInfo fieldInfo) {
        if (fieldInfo == null) {
            return null;
        }
        FieldInfoVO fieldInfoVO = new FieldInfoVO();
        BeanUtils.copyProperties(fieldInfo, fieldInfoVO);
        return fieldInfoVO;
    }

}
