package info.knightrcom.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 执行计划
 */
public abstract class ExecutePlan {

	private Map<String, Object> params = new HashMap<String, Object>();

	/**
	 * @param key
	 * @return
	 */
	public Object getParams(String key) {
		return params.get(key);
	}

	/**
	 * @param key
	 * @param value
	 */
	public void setParams(String key, Object value) {
		this.params.put(key, value);
	}

	/**
	 * @return
	 */
	public Object beforeTryPart() throws Exception {
		return null;
	}

	/**
	 * @return
	 */
	public abstract Object tryPart() throws Exception;

	/**
	 * @return
	 */
	public Object afterTryPart() throws Exception {
		return null;
	}

	/**
	 * @return
	 */
	public abstract Object exceptionPart();

	/**
	 * @param executeResult
	 */
	public void finallyPart(Object executeResult) {
	}

	/**
	 * @return
	 */
	public Object execute() {
		Object result = null;
		try {
			result = beforeTryPart();
			result = tryPart();
			result = afterTryPart();
		} catch (Exception e) {
			result = exceptionPart();
		} finally {
			finallyPart(result);
		}
		return result;
	}
}
