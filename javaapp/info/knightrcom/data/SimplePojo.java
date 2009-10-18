package info.knightrcom.data;

import java.util.Date;

public interface SimplePojo {

    public Date getCreateTime();

    public void setCreateTime(Date createTime);

    public String getCreateBy();

    public void setCreateBy(String createBy);

    public Date getUpdateTime();

    public void setUpdateTime(Date updateTime);

    public String getUpdateBy();

    public void setUpdateBy(String updateBy);
}
