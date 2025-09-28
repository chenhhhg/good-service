package bupt.goodservice.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 中国行政区划表-市级
 *
 * @TableName regional_divisions
 */
@Data
public class RegionalDivisions implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 地域标识/行政区划代码
     */
    private String regionalCode;
    /**
     * 地域名称
     */
    private String regionalName;
    /**
     * 所属地市名称
     */
    private String cityName;
    /**
     * 所属省份名称
     */
    private String provinceName;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 更新时间
     */
    private Date updatedTime;

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
        RegionalDivisions other = (RegionalDivisions) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getRegionalCode() == null ? other.getRegionalCode() == null : this.getRegionalCode().equals(other.getRegionalCode()))
                && (this.getRegionalName() == null ? other.getRegionalName() == null : this.getRegionalName().equals(other.getRegionalName()))
                && (this.getCityName() == null ? other.getCityName() == null : this.getCityName().equals(other.getCityName()))
                && (this.getProvinceName() == null ? other.getProvinceName() == null : this.getProvinceName().equals(other.getProvinceName()))
                && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()))
                && (this.getUpdatedTime() == null ? other.getUpdatedTime() == null : this.getUpdatedTime().equals(other.getUpdatedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRegionalCode() == null) ? 0 : getRegionalCode().hashCode());
        result = prime * result + ((getRegionalName() == null) ? 0 : getRegionalName().hashCode());
        result = prime * result + ((getCityName() == null) ? 0 : getCityName().hashCode());
        result = prime * result + ((getProvinceName() == null) ? 0 : getProvinceName().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        result = prime * result + ((getUpdatedTime() == null) ? 0 : getUpdatedTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", regionalCode=").append(regionalCode);
        sb.append(", regionalName=").append(regionalName);
        sb.append(", cityName=").append(cityName);
        sb.append(", provinceName=").append(provinceName);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}