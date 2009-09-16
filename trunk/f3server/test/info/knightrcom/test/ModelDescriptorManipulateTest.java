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

	    // 把配置文件内容保存到数据库中
    	try {
    		HibernateSessionFactory.getSession().beginTransaction();
    		
    		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			properties.storeToXML(outStream, null);
			
			GlobalConfig config = new GlobalConfig();
			config.setGlobalConfigId(UUID.randomUUID().toString());
			config.setName(GameConfigureConstant.GLOBAL_CONFIG_NAME);
			config.setValue(outStream.toString("utf-8"));
			
			HibernateSessionFactory.getSession().save(config);
			
			HibernateSessionFactory.getSession().getTransaction().commit();
		} catch (Exception e) {
			HibernateSessionFactory.getSession().getTransaction().rollback();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @throws Exception
	 */
	public void testInitRoles() throws Exception {
    	try {
    		HibernateSessionFactory.getSession().beginTransaction();
    		HibernateSessionFactory.getSession().createSQLQuery("DELETE FROM global_config WHERE type = 'PLAYER_ROLE'").executeUpdate();
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
			HibernateSessionFactory.getSession().getTransaction().commit();
		} catch (Exception e) {
			HibernateSessionFactory.getSession().getTransaction().rollback();
			throw new RuntimeException(e);
		}
	}

	/**
	 * @throws Exception
	 */
	public void testSystemParameters() throws Exception {
        try {
            HibernateSessionFactory.getSession().beginTransaction();
    	    HibernateSessionFactory.getSession().createSQLQuery("DELETE FROM global_config WHERE type = '" + GameConfigureConstant.SERVER_PARAM_NAME + "'").executeUpdate();
    	    Map<String, String> allConfigParameters = new HashMap<String, String>();
            allConfigParameters.put("IP_CONFLICT_ENABLED", "false");
            allConfigParameters.put("SYSTEM_SCORE_RATE", "5");
            allConfigParameters.put("RANDOMIZE_WAITING_QUEUE", "false");
            allConfigParameters.put("RANDOMIZE_WAITING_QUEUE", "10");
    	    for (String key : allConfigParameters.keySet()) {
    	        GlobalConfig config = new GlobalConfig();
    	        config.setGlobalConfigId(UUID.randomUUID().toString());
    	        config.setName(key);
    	        config.setType(GameConfigureConstant.SERVER_PARAM_NAME);
    	        config.setValue(allConfigParameters.get(key));
    	        HibernateSessionFactory.getSession().save(config);
    	    }
            HibernateSessionFactory.getSession().getTransaction().commit();
        } catch (Exception e) {
            HibernateSessionFactory.getSession().getTransaction().rollback();
            throw new RuntimeException(e);
        }
	}

	/**
	 * Test properties file's reading ability.
	 * 
	 * @throws Exception
	 */
	public void testReadProperties() throws Exception {
    	Properties properties = new Properties();
    	try {
    		Query query = HibernateSessionFactory.getSession().createQuery("from GlobalConfig where name = '" + GameConfigureConstant.GLOBAL_CONFIG_NAME + "' order by createTime desc");
    		GlobalConfig config = (GlobalConfig)query.uniqueResult();
    		properties.loadFromXML(new ByteArrayInputStream(config.getValue().getBytes("utf-8")));
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			properties.storeToXML(bytes, null);
			System.out.println(bytes.toString("utf-8"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
