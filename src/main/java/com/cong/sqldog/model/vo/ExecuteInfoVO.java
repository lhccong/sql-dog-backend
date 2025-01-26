package com.cong.sqldog.model.vo;

import com.cong.sqldog.interfaces.vo.user.UserVO;
import com.cong.sqldog.model.entity.ExecuteInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * SQL执行记录视图
 *
 * @author tian
 */
@Data
public class ExecuteInfoVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 执行 SQL 的内容
     */
    private String sqlContent;

    /**
     * SQL 分析结果
     */
    private String sqlAnalyzeResult;

    /**
     * 状态（0-待审核, 1-通过, 2-拒绝）
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 创建用户 id
     */
    private Long userId;


    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param executeInfoVO SQL 执行记录封装类
     * @return SQL执行记录对象
     */
    public static ExecuteInfo voToObj(ExecuteInfoVO executeInfoVO) {
        if (executeInfoVO == null) {
            return null;
        }
        ExecuteInfo executeInfo = new ExecuteInfo();
        BeanUtils.copyProperties(executeInfoVO, executeInfo);
        return executeInfo;
    }

    /**
     * 对象转封装类
     *
     * @param executeInfo SQL 执行记录对象
     * @return SQL 执行记录封装类
     */
    public static ExecuteInfoVO objToVo(ExecuteInfo executeInfo) {
        if (executeInfo == null) {
            return null;
        }
        ExecuteInfoVO executeInfoVO = new ExecuteInfoVO();
        BeanUtils.copyProperties(executeInfo, executeInfoVO);
        return executeInfoVO;
    }
}
