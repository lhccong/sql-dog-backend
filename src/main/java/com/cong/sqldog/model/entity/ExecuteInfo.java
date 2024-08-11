package com.cong.sqldog.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * SQL执行记录
 * TableName: execute_info
 */
@TableName(value ="execute_info")
@Data
public class ExecuteInfo implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 执行 SQL 的内容
     */
    @TableField(value = "sqlContent")
    private String sqlContent;

    /**
     * SQL 分析结果
     */
    @TableField(value = "sqlAnalyzeResult")
    private String sqlAnalyzeResult;

    /**
     * 状态（0-待审核, 1-通过, 2-拒绝）
     */
    @TableField(value = "reviewStatus")
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    @TableField(value = "reviewMessage")
    private String reviewMessage;

    /**
     * 创建用户 id
     */
    @TableField(value = "userId",fill = FieldFill.INSERT)
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    private Integer isDelete;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ExecuteInfo other = (ExecuteInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSqlContent() == null ? other.getSqlContent() == null : this.getSqlContent().equals(other.getSqlContent()))
            && (this.getSqlAnalyzeResult() == null ? other.getSqlAnalyzeResult() == null : this.getSqlAnalyzeResult().equals(other.getSqlAnalyzeResult()))
            && (this.getReviewStatus() == null ? other.getReviewStatus() == null : this.getReviewStatus().equals(other.getReviewStatus()))
            && (this.getReviewMessage() == null ? other.getReviewMessage() == null : this.getReviewMessage().equals(other.getReviewMessage()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSqlContent() == null) ? 0 : getSqlContent().hashCode());
        result = prime * result + ((getSqlAnalyzeResult() == null) ? 0 : getSqlAnalyzeResult().hashCode());
        result = prime * result + ((getReviewStatus() == null) ? 0 : getReviewStatus().hashCode());
        result = prime * result + ((getReviewMessage() == null) ? 0 : getReviewMessage().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sqlContent=").append(sqlContent);
        sb.append(", sqlAnalyzeResult=").append(sqlAnalyzeResult);
        sb.append(", reviewStatus=").append(reviewStatus);
        sb.append(", reviewMessage=").append(reviewMessage);
        sb.append(", userId=").append(userId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}