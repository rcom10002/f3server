package info.knightrcom.util;

import info.knightrcom.data.metadata.GlobalConfigDAO;

import java.math.BigDecimal;

/**
 * 读取系统参数
 */
public class SystemParamUtil {

	public boolean readBoolean(String key) {
		String value = new GlobalConfigDAO().findByName(key).get(0).getValue();
		return Boolean.getBoolean(value.toLowerCase());
	}

	public String readString(String key) {
		return new GlobalConfigDAO().findByName(key).get(0).getValue();
	}

	public int readInt(String key) {
		String value = new GlobalConfigDAO().findByName(key).get(0).getValue();
		return new BigDecimal(value).intValue();
	}
	
	public double readFloat(String key) {
		String value = new GlobalConfigDAO().findByName(key).get(0).getValue();
		return new BigDecimal(value).doubleValue();
	}
}
