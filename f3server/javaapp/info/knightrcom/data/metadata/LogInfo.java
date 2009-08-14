package info.knightrcom.data.metadata;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * LogInfo entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "log_info", catalog = "f3s")
public class LogInfo extends info.knightrcom.data.SimplePojoImpl implements java.io.Serializable {

    // Fields

    private String logId;

    private String number;

    private String caption;

    private String keyCause1;

    private String keyCause2;

    private String keyCause3;

    private String info;

    private String type;

    private Short status;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    // Constructors

    /** default constructor */
    public LogInfo() {
    }

    /** minimal constructor */
    public LogInfo(String logId, String caption, String info, String type) {
        this.logId = logId;
        this.caption = caption;
        this.info = info;
        this.type = type;
    }

    /** full constructor */
    public LogInfo(String logId, String number, String caption, String keyCause1, String keyCause2, String keyCause3, String info, String type, Short status, Date createTime, String createBy, Date updateTime, String updateBy) {
        this.logId = logId;
        this.number = number;
        this.caption = caption;
        this.keyCause1 = keyCause1;
        this.keyCause2 = keyCause2;
        this.keyCause3 = keyCause3;
        this.info = info;
        this.type = type;
        this.status = status;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
    }

    // Property accessors
    @Id
    @Column(name = "LOG_ID", unique = true, nullable = false, length = 100)
    public String getLogId() {
        return this.logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    @Column(name = "NUMBER", length = 100)
    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Column(name = "CAPTION", nullable = false, length = 100)
    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Column(name = "KEY_CAUSE1", length = 100)
    public String getKeyCause1() {
        return this.keyCause1;
    }

    public void setKeyCause1(String keyCause1) {
        this.keyCause1 = keyCause1;
    }

    @Column(name = "KEY_CAUSE2", length = 100)
    public String getKeyCause2() {
        return this.keyCause2;
    }

    public void setKeyCause2(String keyCause2) {
        this.keyCause2 = keyCause2;
    }

    @Column(name = "KEY_CAUSE3", length = 100)
    public String getKeyCause3() {
        return this.keyCause3;
    }

    public void setKeyCause3(String keyCause3) {
        this.keyCause3 = keyCause3;
    }

    @Column(name = "INFO", nullable = false, length = 65535)
    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Column(name = "TYPE", nullable = false, length = 100)
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "STATUS")
    public Short getStatus() {
        return this.status;
    }

    public void setStatus(Short status) {
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
