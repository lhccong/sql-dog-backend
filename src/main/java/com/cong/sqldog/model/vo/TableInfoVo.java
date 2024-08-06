package com.cong.sqldog.model.vo;

import com.cong.sqldog.model.entity.TableInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 表信息视图
 *
 * @author <a href="https://github.com/lhccong">聪</a>
 */
@Data
public class TableInfoVo implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 名字
     */
    private String name;

    /**
     * 内容
     */
    private String content;

    /**
     * 审核状态
     */
    private Integer reviewStatus;

    /**
     * 评论消息
     */
    private String reviewMessage;

    /**
     * 用户 ID
     */
    private Long userId;


    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     */
    public static TableInfo voToObj(TableInfoVo tableInfoVO) {
        if (tableInfoVO == null) {
            return null;
        }
        TableInfo tableInfo = new TableInfo();
        BeanUtils.copyProperties(tableInfoVO, tableInfo);
        return tableInfo;
    }

    /**
     * 对象转封装类
     */
    public static TableInfoVo objToVo(TableInfo tableInfo) {
        if (tableInfo == null) {
            return null;
        }
        TableInfoVo tableInfoVO = new TableInfoVo();
        BeanUtils.copyProperties(tableInfo, tableInfoVO);
        return tableInfoVO;
    }
}
