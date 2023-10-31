package com.example.avuehelper.dao;

import java.io.Serializable;

import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * 可视化配置表
 * @TableName blade_visual_config
 */
@Data
public class VisualConfigDao implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 可视化表主键
     */
    private Long visualId;

    /**
     * 配置json
     */
    private String detail;

    /**
     * 组件json
     */
    private String component;

    /**
     * 大屏的变量
     */
    private JSONObject variable;

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
        VisualConfigDao other = (VisualConfigDao) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getVisualId() == null ? other.getVisualId() == null : this.getVisualId().equals(other.getVisualId()))
            && (this.getDetail() == null ? other.getDetail() == null : this.getDetail().equals(other.getDetail()))
            && (this.getComponent() == null ? other.getComponent() == null : this.getComponent().equals(other.getComponent()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getVisualId() == null) ? 0 : getVisualId().hashCode());
        result = prime * result + ((getDetail() == null) ? 0 : getDetail().hashCode());
        result = prime * result + ((getComponent() == null) ? 0 : getComponent().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", visualId=").append(visualId);
        sb.append(", detail=").append(detail);
        sb.append(", component=").append(component);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}