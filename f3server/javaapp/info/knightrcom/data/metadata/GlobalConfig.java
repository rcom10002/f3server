package info.knightrcom.data.metadata;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * GlobalConfig entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "global_config", catalog = "f3s")
public class GlobalConfig extends info.knightrcom.data.SimplePojoImpl implements java.io.Serializable {

    // Fields

    private String globalConfigId;

    private String number;

    private String name;

    private String value;

    private String type;

    private String status;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    // Constructors

    /** default constructor */
    public GlobalConfig() {
    }

    /** minimal constructor */
    public GlobalConfig(String globalConfigId, String name, String value) {
        this.globalConfigId = globalConfigId;
        this.name = name;
        this.value = value;
    }

    /** full constructor */
    public GlobalConfig(String globalConfigId, String number, String name, String value, String type, String status, Date createTime, String createBy, Date updateTime, String updateBy) {
        this.globalConfigId = globalConfigId;
        this.number = number;
        this.name = name;
        this.value = value;
        this.type = type;
        this.status = status;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
    }

    // Property accessors
    @Id
    @Column(name = "GLOBAL_CONFIG_ID", unique = true, nullable = false, length = 100)
    public String getGlobalConfigId() {
        return this.globalConfigId;
    }

    public void setGlobalConfigId(String globalConfigId) {
        this.globalConfigId = globalConfigId;
    }

    @Column(name = "NUMBER", length = 100)
    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Column(name = "NAME", nullable = false, length = 100)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "VALUE", nullable = false, length = 65535)
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Column(name = "TYPE", length = 100)
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "STATUS", length = 100)
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TIME", length = 19)
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "CREATE_BY", length = 100)
    public String getCreateBy() {
        return this.createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_TIME", length = 19)
    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "UPDATE_BY", length = 100)
    public String getUpdateBy() {
        return this.updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

}
