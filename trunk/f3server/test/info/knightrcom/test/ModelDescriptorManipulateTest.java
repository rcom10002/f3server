package info.knightrcom.test;

import info.knightrcom.util.ModelUtil;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import junit.framework.TestCase;

public class ModelDescriptorManipulateTest extends TestCase {

	/**
	 * Test properties file's writing ability.
	 * 
	 * @throws Exception
	 */
	public void testSaveProperties() throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName().replaceFirst("\\.\\w+$", "") + ".test_model_defination");
		Properties properties = new Properties();
		Enumeration<String> keyEnum = bundle.getKeys();
		while (keyEnum.hasMoreElements()) {
			String key = keyEnum.nextElement();
			String value = bundle.getString(key);
			properties.put(key, value);
		}

		ModelUtil.saveProperties(properties);
	}

	/**
	 * Test properties file's reading ability.
	 * 
	 * @throws Exception
	 */
	public void testReadProperties() throws Exception {
	    Properties properties = ModelUtil.readProperties();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		properties.storeToXML(bytes, null);
		System.out.println(bytes.toString("utf-8"));
	}
}
