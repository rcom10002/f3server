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
	 * @param values
	 */
	private void validate(String ... values) {
		for (String value : values) {
			if (value == null || value.indexOf('#') > -1 || value.indexOf('~') > -1) {
				throw new RuntimeException("# is not allowed here");
			}
		}
	}

	/**
	 * @return
	 */
	public int count() {
		return container.length() - container.replace("#", "").length();
	}
}
