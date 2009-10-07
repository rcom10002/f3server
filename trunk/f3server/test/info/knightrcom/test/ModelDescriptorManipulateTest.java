package info.knightrcom.test;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.web.constant.GameConfigureConstant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.UUID;

import junit.framework.TestCase;

import org.hibernate.Query;

public class ModelDescriptorManipulateTest extends TestCase {

	public ModelDescriptorManipulateTest() {
		HibernateSessionFactory.getSession().createSQLQuery("delete from global_config").executeUpdate();
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
	    // 把配置文件内容保存到数据库中
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		properties.storeToXML(outStream, null);
		GlobalConfig config = new GlobalConfig();
		config.setGlobalConfigId(UUID.randomUUID().toString());
		config.setName(GameConfigureConstant.GLOBAL_CONFIG_NAME);
		config.setValue(outStream.toString("utf-8"));
		HibernateSessionFactory.getSession().save(config);
	}
	
	/**
	 * @throws Exception
	 */
	public void testInitRoles() throws Exception {
		String roles = "Administrator~系统管理员;SuperGameMaster~超级游戏管理员;GameMaster~游戏管理员;GroupUser~组用户;User~普通用户";
		String[] roleArray = roles.split(";");
		for (String role : roleArray) {
			GlobalConfig config = new GlobalConfig();
			config.setGlobalConfigId(UUID.randomUUID().toString());
			config.setName(role.split("~")[0]);
			config.setValue(role.split("~")[1]);
			config.setType("PLAYER_ROLE");
			HibernateSessionFactory.getSession().save(config);
		}
	}

	/**
	 * @throws Exception
	 */
	public void testSystemParameters() throws Exception {
	    Map<String, String> allConfigParameters = new HashMap<String, String>();
        allConfigParameters.put("IP_CONFLICT_ENABLED", "false");
        allConfigParameters.put("SYSTEM_SCORE_RATE", "5");
        allConfigParameters.put("PAGE_SIZE", "15");
        allConfigParameters.put("WAITING_QUEUE_RANDOM_ENABLE", "false");
        allConfigParameters.put("WAITING_QUEUE_GROUP_QUANTITY", "1");
	    for (String key : allConfigParameters.keySet()) {
	        GlobalConfig config = new GlobalConfig();
	        config.setGlobalConfigId(UUID.randomUUID().toString());
	        config.setName(key);
	        config.setType(GameConfigureConstant.SERVER_PARAM_NAME);
	        config.setValue(allConfigParameters.get(key));
	        HibernateSessionFactory.getSession().save(config);
	    }
	}

	/**
	 * Test properties file's reading ability.
	 * 
	 * @throws Exception
	 */
	public void testReadProperties() throws Exception {
		HibernateSessionFactory.closeSession();
    	Properties properties = new Properties();
		Query query = HibernateSessionFactory.getSession().createQuery("from GlobalConfig where name = '" + GameConfigureConstant.GLOBAL_CONFIG_NAME + "' order by createTime desc");
		GlobalConfig config = (GlobalConfig)query.uniqueResult();
		properties.loadFromXML(new ByteArrayInputStream(config.getValue().getBytes("utf-8")));
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		properties.storeToXML(bytes, null);
		System.out.println(bytes.toString("utf-8"));
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateSessionFactory.closeSession();
		super.tearDown();
	}
}
