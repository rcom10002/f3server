package info.knightrcom.util;


/**
 * 
 * 键与值中均不可以出现“#”和“~”
 * 
 * #key~value#key~value#key~value ...
 * 
 */
public class SystemParameter {

	private String container = "";

	/**
	 * 根据键值返回value
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		validate(key);
		String pattern = "^.*#key~([^#]*).*$".replace("key", key);
		return container.replaceFirst(pattern, "$1");
	}

	/**
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		validate(key, value);
		if (container.indexOf("#key~".replace("key", key)) > -1) {
			container = container.replaceFirst("#key~([^#]*)".replace("key", key), ""); 
		}
		container += "#key~value".replace("key", key).replace("value", value);
	}

	/**
	 * @return
	 */
	public String[] keys() {
	    container = container.replaceFirst("^#", "");
	    container = container.replaceAll("~[^#]*", "");
	    return container.split("#");
	}

	/**
     * @return
     */
    public int count() {
        return container.length() - container.replace("#", "").length();
    }

	/**
	 * key 与 value 只允许使用数字和字母，并且不能为null或空串
	 * 
	 * @param values
	 */
	private void validate(String ... values) {
		for (String value : values) {
			if (value == null || value.length() == 0 || value.indexOf('_') > -1 || !value.matches("^\\w*$")) {
				throw new RuntimeException("Illegal mark is not allowed here");
			}
		}
	}//Pattern
}
