package info.knightrcom.test;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.util.ModelUtil;
import info.knightrcom.web.constant.GameConfigureConstant;

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
	    HibernateSessionFactory.getSession().createSQLQuery("delete from global_config where name='#'".replace("#", GameConfigureConstant.GLOBAL_CONFIG_NAME)).executeUpdate();
	    HibernateSessionFactory.getSession().flush();
	    HibernateSessionFactory.getSession().close();
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
	 * Test properties file's writing ability. about server param
	 * 
	 * @throws Exception
	 */
	public void testSaveServerParamProperties() throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName().replaceFirst("\\.\\w+$", "") + ".test_server_param_model_defination");
		Properties properties = new Properties();
		Enumeration<String> keyEnum = bundle.getKeys();
		while (keyEnum.hasMoreElements()) {
			String key = keyEnum.nextElement();
			String value = bundle.getString(key);
			properties.put(key, value);
		}
		ModelUtil.saveProperties(properties, GameConfigureConstant.SERVER_PARAM_NAME);
	}
	
	/**
	 * Test properties file's writing ability. about report template manage
	 * 
	 * @throws Exception
	 */
	public void testSaveReportTemplateProperties() throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName().replaceFirst("\\.\\w+$", "") + ".test_report_template_model_defination");
		Properties properties = new Properties();
		Enumeration<String> keyEnum = bundle.getKeys();
		while (keyEnum.hasMoreElements()) {
			String key = keyEnum.nextElement();
			String value = bundle.getString(key);
			properties.put(key, value);
		}
		ModelUtil.saveProperties(properties, GameConfigureConstant.REPORT_TEMPLATE_MANAGE);
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
	
	/**
	 * Test properties file's reading ability. about server parameter
	 * 
	 * @throws Exception
	 */
	public void testReadServerParamProperties() throws Exception {
	    Properties properties = ModelUtil.readProperties(GameConfigureConstant.SERVER_PARAM_NAME);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		properties.storeToXML(bytes, null);
		System.out.println(bytes.toString("utf-8"));
	}
	
	/**
	 * Test properties file's reading ability. about report template
	 * 
	 * @throws Exception
	 */
	public void testReadReportTemplateProperties() throws Exception {
	    Properties properties = ModelUtil.readProperties(GameConfigureConstant.REPORT_TEMPLATE_MANAGE);
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		properties.storeToXML(bytes, null);
		System.out.println(bytes.toString("utf-8"));
	}
}
