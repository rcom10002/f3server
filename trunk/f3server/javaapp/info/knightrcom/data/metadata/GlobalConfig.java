package info.knightrcom.data.metadata;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * GlobalConfig entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "global_config")
public class GlobalConfig extends info.knightrcom.data.SimplePojoImpl implements java.io.Serializable {

	// Fields
	private static final long serialVersionUID = -5491682659448268349L;
	
	private String globalConfigId;
	private String number;
	private String name;
	private String displayName;
	private String value;
	private String desc0;
	private String desc1;
	private String desc2;
	private String desc3;
	private String desc4;
	private String desc5;
	private String desc6;
	private String desc7;
	private String desc8;
	private String desc9;
	private Integer displayIndex;
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
	public GlobalConfig(String globalConfigId, String number, String name,
			String displayName, String value, String desc0, String desc1,
			String desc2, String desc3, String desc4, String desc5,
			String desc6, String desc7, String desc8, String desc9,
			Integer displayIndex, String type, String status, Date createTime,
			String createBy, Date updateTime, String updateBy) {
		this.globalConfigId = globalConfigId;
		this.number = number;
		this.name = name;
		this.displayName = displayName;
		this.value = value;
		this.desc0 = desc0;
		this.desc1 = desc1;
		this.desc2 = desc2;
		this.desc3 = desc3;
		this.desc4 = desc4;
		this.desc5 = desc5;
		this.desc6 = desc6;
		this.desc7 = desc7;
		this.desc8 = desc8;
		this.desc9 = desc9;
		this.displayIndex = displayIndex;
		this.type = type;
		this.status = status;
		this.createTime = createTime;
		this.createBy = createBy;
		this.updateTime = updateTime;
		this.updateBy = updateBy;
	}

	// Property accessors
	@Id
	@Column(name = "GLOBAL_CONFIG_ID", unique = true, nullable = false, insertable = true, updatable = true, length = 100)
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

	@Column(name = "NAME", unique = false, nullable = false, insertable = true, updatable = true, length = 100)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DISPLAY_NAME", length = 100)
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(name = "VALUE", unique = false, nullable = false, insertable = true, updatable = true, length = 65535)
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(name = "DESC0", length = 100)
	public String getDesc0() {
		return this.desc0;
	}

	public void setDesc0(String desc0) {
		this.desc0 = desc0;
	}

	@Column(name = "DESC1", length = 100)
	public String getDesc1() {
		return this.desc1;
	}

	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}

	@Column(name = "DESC2", length = 100)
	public String getDesc2() {
		return this.desc2;
	}

	public void setDesc2(String desc2) {
		this.desc2 = desc2;
	}

	@Column(name = "DESC3", length = 100)
	public String getDesc3() {
		return this.desc3;
	}

	public void setDesc3(String desc3) {
		this.desc3 = desc3;
	}

	@Column(name = "DESC4", length = 100)
	public String getDesc4() {
		return this.desc4;
	}

	public void setDesc4(String desc4) {
		this.desc4 = desc4;
	}

	@Column(name = "DESC5", length = 100)
	public String getDesc5() {
		return this.desc5;
	}

	public void setDesc5(String desc5) {
		this.desc5 = desc5;
	}

	@Column(name = "DESC6", length = 100)
	public String getDesc6() {
		return this.desc6;
	}

	public void setDesc6(String desc6) {
		this.desc6 = desc6;
	}

	@Column(name = "DESC7", length = 100)
	public String getDesc7() {
		return this.desc7;
	}

	public void setDesc7(String desc7) {
		this.desc7 = desc7;
	}

	@Column(name = "DESC8", length = 100)
	public String getDesc8() {
		return this.desc8;
	}

	public void setDesc8(String desc8) {
		this.desc8 = desc8;
	}

	@Column(name = "DESC9", length = 100)
	public String getDesc9() {
		return this.desc9;
	}

	public void setDesc9(String desc9) {
		this.desc9 = desc9;
	}

	@Column(name = "DISPLAY_INDEX", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getDisplayIndex() {
		return this.displayIndex;
	}

	public void setDisplayIndex(Integer displayIndex) {
		this.displayIndex = displayIndex;
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