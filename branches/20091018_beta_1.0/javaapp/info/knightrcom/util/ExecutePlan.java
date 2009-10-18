package info.knightrcom.util;

import info.knightrcom.web.model.EntityInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 执行计划
 */
public abstract class ExecutePlan {

	private Map<String, Object> params = new HashMap<String, Object>();

	/**
	 * @param entity
	 * @return
	 */
	protected EntityInfo<Object> newEntityInfo(final Object entity) {
        return new EntityInfo<Object>() {
        	{
        		setEntity(entity);
        	}
        };
	}

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
			result = result == null ? beforeTryPart() : result;
			result = result == null ? tryPart() : result;
			result = result == null ? afterTryPart() : result;
		} catch (Exception e) {
			result = result == null ? exceptionPart() : result;
		} finally {
			finallyPart(result);
		}
		return result;
	}
}
