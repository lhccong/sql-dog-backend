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
public class TableInfoVO implements Serializable {

    private Long id;

    private String name;

    private String content;

    private Integer reviewStatus;

    private String reviewMessage;

    private Long userId;


    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     */
    public static TableInfo voToObj(TableInfoVO tableInfoVO) {
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
    public static TableInfoVO objToVo(TableInfo tableInfo) {
        if (tableInfo == null) {
            return null;
        }
        TableInfoVO tableInfoVO = new TableInfoVO();
        BeanUtils.copyProperties(tableInfo, tableInfoVO);
        return tableInfoVO;
    }
}
