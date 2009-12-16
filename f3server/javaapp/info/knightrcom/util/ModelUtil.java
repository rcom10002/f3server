package info.knightrcom.util;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.data.metadata.GlobalConfigDAO;
import info.knightrcom.model.global.AbstractModel;
import info.knightrcom.model.global.Lobby;
import info.knightrcom.model.global.Platform;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.global.Room;
import info.knightrcom.web.constant.GameConfigureConstant;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

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
    private static Map<String, String> systemParameters = new HashMap<String, String>();
    private static Map<String, String> customPokersParameters = new HashMap<String, String>(); // TODO This variable should be moved to another TBD place!!!

    /**
     * 
     */
    public static void resetModels() {
    	sessions = null;
    	modelDesc = null;
    	platform = null;
    	lobbys = Collections.synchronizedMap(new HashMap<String, Lobby>());
    	rooms = Collections.synchronizedMap(new HashMap<String, Room>());
    	systemParameters = new HashMap<String, String>();
    	customPokersParameters = new HashMap<String, String>();
        load();
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

    static {
        load();
    }

    /**
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void load() {
        // 获取配置信息
        Properties config = readProperties();
        String platformConfig = config.getProperty("PLATFORM");
        String[] lobbyConfigArray = config.getProperty("LOBBY").split(";");
        String[] roomConfigArray = config.getProperty("ROOM").split(";");
        int maxPlayerNumber = Integer.parseInt(config.getProperty("MAX_PLAYER_NUMBER"));

        // 创建模型
        platform = ModelUtil.createFromConfigString(Platform.class, platformConfig);
        for (String lobbyConfig : lobbyConfigArray) {
            Lobby lobby = ModelUtil.createFromConfigString(Lobby.class, lobbyConfig);
            if (lobby.getDisabled() != null && "false".equals(lobby.getDisabled().toLowerCase())) {
                lobby.setParentId(platform.getId());
                lobby.setParent(platform);
                platform.addChild(lobby.getId(), lobby);
            } else {
                continue;
            }
            lobbys.put(lobby.getId(), lobby);
        }
        for (String roomConfig : roomConfigArray) {
            Room room = ModelUtil.createFromConfigString(Room.class, roomConfig, "parent");
            if (room != null && "false".equals(room.getDisabled().toLowerCase())) {
                String parentId = roomConfig.replaceAll("parent=([^,]+).*", "$1");
                if (lobbys.get(parentId) == null) {
                    // 大厅被禁用时
                    continue;
                }
                room.setParentId(parentId);
                room.setParent(platform.getChild(parentId));
                room.setChildLimit(maxPlayerNumber);
                platform.getChild(parentId).addChild(room.getId(), room);
            } else {
                continue;
            }
            rooms.put(room.getId(), room);
        }

        // 创建模型XML
        XStream stream = new XStream();
        stream.alias("platform", Platform.class);
        stream.alias("lobby", Lobby.class);
        stream.alias("room", Room.class);
        stream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
        modelDesc = "<e4x-data>#</e4x-data>".replace("#", stream.toXML(platform));

        // 初始化系统参数
        for (GlobalConfig eachParameter : new GlobalConfigDAO().findByType(GameConfigureConstant.SERVER_PARAM_NAME)) {
            systemParameters.put(eachParameter.getName(), eachParameter.getValue());
        }

        // 初始化系统参数
        for (GlobalConfig eachParameter : new GlobalConfigDAO().findByType("CUSTOM_POKER")) {
            customPokersParameters.put(eachParameter.getName(), eachParameter.getValue());
        }
    }

    public static final String getModelDesc() {
        return modelDesc;
    }

    /**
     * @param key
     * @param defaultValue
     * 
     * @return the systemParameters
     */
    public static String getSystemParameter(String key, Object defaultValue) {
        return getSystemParameter(key) == null ? defaultValue.toString() : getSystemParameter(key);
    }

    /**
     * @param key
     * 
     * @return the systemParameters
     */
    public static String getSystemParameter(String key) {
        return systemParameters.get(key);
    }

    /**
     * @param prefix
     * 
     * @return the systemParameters
     */
    public static List<String> getSystemParameters(String prefix) {
        List<String> results = new ArrayList<String>();
        for (Entry<String, String> entry : customPokersParameters.entrySet()) {
            if (entry.getKey().indexOf(prefix) == 0) {
                results.add(entry.getValue());
            }
        }
        return results;
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
    public static synchronized Player getPlayer(IoSession session) {
        return (Player)session.getAttribute(Player.ATTR_NAME);
    }

    /**
     * @param session
     * @return
     */
    public static synchronized void setPlayer(IoSession session, Player player) {
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

    /**
     * TODO This class my be used in the future
     *
     */
    public static class PuppetPool {

        private static final Map<String, Player> puppets = Collections.synchronizedSortedMap(new TreeMap<String, Player>());

        /**
         * @param puppetId
         * @return
         */
        public static Player getPuppet(String puppetId) {
            return puppets.get(puppetId);
        }

        /**
         * @param playerId
         * @param puppet
         */
        public static void addPuppet(String playerId, Player puppet) {
            puppets.put(playerId, puppet);
        }

        /**
         * @param playerId
         */
        public static void removePuppet(String playerId) {
            puppets.remove(playerId);
        }

    }

}
