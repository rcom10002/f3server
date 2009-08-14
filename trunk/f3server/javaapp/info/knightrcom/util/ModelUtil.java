package info.knightrcom.util;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.model.global.AbstractModel;
import info.knightrcom.model.global.Lobby;
import info.knightrcom.model.global.Platform;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.global.Room;
import info.knightrcom.web.constant.GameConfigureConstant;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.mina.core.session.IoSession;
import org.hibernate.Query;

import com.thoughtworks.xstream.XStream;

public class ModelUtil {

    private static Set<IoSession> sessions;
    private static String modelDesc;
    private static Platform platform;
    private static Map<String, Lobby> lobbys = Collections.synchronizedMap(new HashMap<String, Lobby>());
    private static Map<String, Room> rooms = Collections.synchronizedMap(new HashMap<String, Room>());

    public static void resetModels() {
    	sessions = null;
    	modelDesc = null;
    	platform = null;
    	lobbys = Collections.synchronizedMap(new HashMap<String, Lobby>());
    	rooms = Collections.synchronizedMap(new HashMap<String, Room>());
    }

    /**
     * @return
     */
    public static Set<IoSession> getSessions() {
        return sessions;
    }

    /**
     * @param sessionSet
     */
    public static void setSessions(Set<IoSession> sessionSet) {
        sessions = sessionSet;
    }

    /**
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Platform createPlatform() throws FileNotFoundException, IOException {
        // 获取匹配信息
        // MOD 2009/07/02 BEGIN
//        ResourceBundle bundle = ResourceBundle.getBundle(ModelUtil.class.getPackage().getName() + ".model_defination");
//        String platformConfig = (String) bundle.getString("PLATFORM");
//        String[] lobbyConfigArray = ((String) bundle.getString("LOBBY")).split(";");
//        String[] roomConfigArray = ((String) bundle.getString("ROOM")).split(";");
//        int maxPlayerNumber = Integer.parseInt((String) bundle.getString("MAX_PLAYER_NUMBER"));

        Properties config = readProperties();
        String platformConfig = config.getProperty("PLATFORM");
        String[] lobbyConfigArray = config.getProperty("LOBBY").split(";");
        String[] roomConfigArray = config.getProperty("ROOM").split(";");
        int maxPlayerNumber = Integer.parseInt(config.getProperty("MAX_PLAYER_NUMBER"));
        // MOD 2009/07/02 END

        // 创建模型
        platform = ModelUtil.createFromConfigString(Platform.class, platformConfig);
        for (String lobbyConfig : lobbyConfigArray) {
            Lobby lobby = ModelUtil.createFromConfigString(Lobby.class, lobbyConfig);
            lobby.setParentId(platform.getId());
            lobby.setParent(platform);
            platform.addChild(lobby.getId(), lobby);
            lobbys.put(lobby.getId(), lobby);
        }
        for (String roomConfig : roomConfigArray) {
            Room room = ModelUtil.createFromConfigString(Room.class, roomConfig, "parent");
            String parentId = roomConfig.replaceAll("parent=([^,]+).*", "$1");
            room.setParentId(parentId);
            room.setParent(platform.getChild(parentId));
            room.setChildLimit(maxPlayerNumber);
            platform.getChild(parentId).addChild(room.getId(), room);
            rooms.put(room.getId(), room);
        }

        // 创建模型XML
        XStream stream = new XStream();
        stream.alias("platform", Platform.class);
        stream.alias("lobby", Lobby.class);
        stream.alias("room", Room.class);
        stream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
        modelDesc = "<e4x-data>#</e4x-data>".replace("#", stream.toXML(ModelUtil.getPlatform()));
        return platform;
    }

    public static final String getModelDesc() {
        return modelDesc;
    }

    /**
     * @return
     */
    public static final Platform getPlatform() {
        return platform;
    }

    /**
     * @param lobbyId
     * @return
     */
    public static Lobby getLobby(String lobbyId) {
        return lobbys.get(lobbyId);
    }

    /**
     * @param roomId
     * @return
     */
    public static Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    /**
     * @param session
     * @return
     */
    public static Player getPlayer(IoSession session) {
        return (Player)session.getAttribute(Player.ATTR_NAME);
    }

    /**
     * @param session
     * @return
     */
    public static void setPlayer(IoSession session, Player player) {
        session.setAttribute(Player.ATTR_NAME, player);
        player.setIosession(session);
    }

    /**
     * 从数据库中读取配置文件内容
     * 
     * @return Properties类型对象
     */
    private static Properties readProperties() {
    	Properties properties = new Properties();
    	try {
    		Query query = HibernateSessionFactory.getSession().createQuery("from GlobalConfig where name = '" + GameConfigureConstant.GLOBAL_CONFIG_NAME + "' order by createTime desc");
    		GlobalConfig config = (GlobalConfig)query.uniqueResult();
    		properties.loadFromXML(new ByteArrayInputStream(config.getValue().getBytes("utf-8")));
			return properties;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * @param <T>
     * @param modelClass
     * @param configString
     * @return
     */
    private static <T extends AbstractModel<?, ?>> T createFromConfigString(Class<T> modelClass, String configString) {
        return createFromConfigString(modelClass, configString, null);
    }

    /**
     * @param <T>
     * @param modelClass
     * @param configString
     * @param exclusion
     * @return
     */
    private static <T extends AbstractModel<?, ?>> T createFromConfigString(Class<T> modelClass, String configString, String exclusion) {
        T bean;
        try {
            bean = modelClass.newInstance();
            String[] configProperties = configString.split(",");
            for (String currentConfig : configProperties) {
                String propName = currentConfig.split("=")[0];
                String propValue = currentConfig.split("=")[1];
                if (exclusion != null && exclusion.equals(propName)) {
                    continue;
                }
                PropertyUtils.setProperty(bean, propName, propValue);
            }
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
