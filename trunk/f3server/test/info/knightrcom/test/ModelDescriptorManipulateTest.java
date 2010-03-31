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
		String roles = "0~Administrator~系统管理员;1~SuperGameMaster~超级游戏管理员;2~GameMaster~游戏管理员;3~GroupUser~组用户;4~User~普通用户";
		String[] roleArray = roles.split(";");
		for (String role : roleArray) {
			GlobalConfig config = new GlobalConfig();
			config.setGlobalConfigId(UUID.randomUUID().toString());
			config.setNumber(role.split("~")[0]);
			config.setName(role.split("~")[1]);
			config.setValue(role.split("~")[2]);
			config.setType("PLAYER_ROLE");
			HibernateSessionFactory.getSession().save(config);
		}
	}

	/**
	 * @throws Exception
	 */
	public void testSystemParameters() throws Exception {
	    Map<String, String> allConfigParameters = new HashMap<String, String>();
        allConfigParameters.put("IP_CONFLICT_ENABLED", "false"); // IP互斥，即同IP不可以同时登录游戏平台
        allConfigParameters.put("SYSTEM_SCORE_RATE", "5"); // 系统抽水比率，整数，5代表5%
        allConfigParameters.put("PAGE_SIZE", "15"); // 每页显示的数据量
        allConfigParameters.put("SEARCH_PERIOD", "1"); // 搜索区间，周，1代表1周之内
        allConfigParameters.put("WAITING_QUEUE_RANDOM_ENABLE", "false"); // 等待队列中的人员是否要在游戏开始之前打乱次序以防止作弊
        allConfigParameters.put("WAITING_QUEUE_GROUP_QUANTITY", "1"); // 等待队列中，开始游戏所需要的最小组数，1代表能形成1组就开始游戏，3代表能形成3组就开始游戏，每组人数由具体游戏决定，如红五需要4个人，3组就是12个人，即3组才可以开始游戏
        allConfigParameters.put("PUPPET_LAUNCHER_URL", "http://localhost:8080/f3s/content/ccgc/CCGameClient.html"); // PUPPET启动路径
        allConfigParameters.put("DESKMATE_CROSS_IP_ENABLE", "false"); // 同IP是否可以同桌
        allConfigParameters.put("RED5_DEADLY7_EXTINCT8", "false"); // 是否启用七独八天功能
        allConfigParameters.put("MAX_ONLINE", "200"); // TODO This should be added for limiting the online player number 最大在线人数
        allConfigParameters.put("PUPPETS_HAPPY_PROHIBIT", "true"); // 是否允许PUPPET帐号自行娱乐
        allConfigParameters.put("IDLE_TIME", "90"); // 90秒间隔时间来检查空闲
        allConfigParameters.put("MAX_THREADS_IN_IDLE_FUTURE_EXECUTOR", "100"); // 用于IDLE测试的线程池大小
        allConfigParameters.put("SECURITY_CONFIGURATION", "<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>"); // 安全策略文件
	    for (String key : allConfigParameters.keySet()) {
	        GlobalConfig config = new GlobalConfig();
	        config.setGlobalConfigId(UUID.randomUUID().toString());
	        config.setName(key);
	        config.setType(GameConfigureConstant.SERVER_PARAM_NAME);
	        config.setValue(allConfigParameters.get(key));
	        HibernateSessionFactory.getSession().save(config);
	    }
	    allConfigParameters.clear();
        allConfigParameters.put("CUSTOM_POKER_RED5_1", "1V10,1V10,2V10,1VJ,1VJ,2VJ,1VQ,1VQ,2VQ,1VK,1VK,2VK,1VA,1VA,2VA~2V10,3V10,3V10,4V10,4V10,2VJ,3VJ,3VJ,4VJ,4VJ,2VQ,3VQ,3VQ,4VQ,4VQ,2VK,3VK,3VK,4VK,4VK,2VA,3VA,3VA,4VA,4VA,1V2,1V2,2V2,2V2,3V2,3V2,4V2,4V2,2V5,2V5,3V5,3V5,4V5,4V5,0VX,0VX,0VY,0VY,1V5,1V5");
        allConfigParameters.put("CUSTOM_POKER_RED5_2", "1V10,1V10,1VJ,1VJ,1VQ,1VQ,1VK,1VK,1VA,1VA,2V5,2V5,3V5,3V5,4V5~2V10,2V10,3V10,3V10,4V10,4V10,2VJ,2VJ,3VJ,3VJ,4VJ,4VJ,2VQ,2VQ,3VQ,3VQ,4VQ,4VQ,2VK,2VK,3VK,3VK,4VK,4VK,2VA,2VA,3VA,3VA,4VA,4VA,1V2,1V2,2V2,2V2,3V2,3V2,4V2,4V2,4V5,0VX,0VX,0VY,0VY,1V5,1V5");
        for (String key : allConfigParameters.keySet()) {
            GlobalConfig config = new GlobalConfig();
            config.setGlobalConfigId(UUID.randomUUID().toString());
            config.setName(key);
            config.setType("CUSTOM_POKER");
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
		assertTrue(bytes.toString("utf-8").length() > 0);
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateSessionFactory.closeSession();
		super.tearDown();
	}
}
