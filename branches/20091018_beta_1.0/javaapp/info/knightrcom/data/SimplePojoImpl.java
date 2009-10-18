package info.knightrcom.data;

import java.util.Date;

/**
 * 
 */
public abstract class SimplePojoImpl implements SimplePojo {

    protected SimplePojoImpl() {
        this.setCreateBy("SYSTEM");
        this.setCreateTime(new Date());
        this.setUpdateBy("SYSTEM");
        this.setUpdateTime(new Date());
    }
}
