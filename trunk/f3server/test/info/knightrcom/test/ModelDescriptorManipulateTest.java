package info.knightrcom.test;

import info.knightrcom.data.HibernateSessionFactory;

import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import junit.framework.TestCase;

public class ModelDescriptorManipulateTest extends TestCase {

	protected void setUp() throws Exception {
		HibernateSessionFactory.init();
		HibernateSessionFactory.getSession().beginTransaction();
		super.setUp();
	}

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
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		properties.storeToXML(bytes, null);
		String result = bytes.toString("utf-8");
		System.out.println(result);
	}

	/**
	 * Test properties file's reading ability.
	 * 
	 * @throws Exception
	 */
	public void testReadProperties() throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName().replaceFirst("\\.\\w+$", "") + ".test_model_defination");
		Properties properties = new Properties();
		Enumeration<String> keyEnum = bundle.getKeys();
		while (keyEnum.hasMoreElements()) {
			String key = keyEnum.nextElement();
			String value = bundle.getString(key);
			properties.put(key, value);
		}
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		properties.storeToXML(bytes, null);
		String result = bytes.toString("utf-8");
		System.out.println(result);
	}

	protected void tearDown() throws Exception {
		HibernateSessionFactory.getSession().getTransaction().commit();
		HibernateSessionFactory.closeSession();
		super.tearDown();
	}
}
