package info.knightrcom.util;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.ModelHistory;
import info.knightrcom.data.metadata.ModelHistoryDAO;
import info.knightrcom.model.global.AbstractModel;
import info.knightrcom.model.global.Lobby;
import info.knightrcom.model.global.Platform;
import info.knightrcom.model.global.Player;
import info.knightrcom.model.global.Room;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.mina.core.session.IoSession;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.thoughtworks.xstream.XStream;

public class ModelUtil {

    private static Set<IoSession> sessions;
    private static Platform platform;
    private static String modelDesc;
    private static Map<String, Lobby> lobbys = Collections.synchronizedMap(new HashMap<String, Lobby>());
    private static Map<String, Room> rooms = Collections.synchronizedMap(new HashMap<String, Room>());

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
        ResourceBundle bundle = ResourceBundle.getBundle(ModelUtil.class.getPackage().getName() + ".model_defination");
        String platformConfig = (String) bundle.getString("PLATFORM");
        String[] lobbyConfigArray = ((String) bundle.getString("LOBBY")).split(";");
        String[] roomConfigArray = ((String) bundle.getString("ROOM")).split(";");
        int maxPlayerNumber = Integer.parseInt((String) bundle.getString("MAX_PLAYER_NUMBER"));

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
     * 
     * @return
     */
    public static Properties readProperties() {
    	Properties properties = new Properties();
    	try {
    		Query query = HibernateSessionFactory.getSession().createQuery("from ModelHistory order by createTime desc");
    		ModelHistory modelHistory = (ModelHistory)query.uniqueResult();
    		properties.loadFromXML(new ByteArrayInputStream(modelHistory.getContent().getBytes("utf-8")));
			return properties;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * @param properties
     */
    public static void saveProperties(Properties properties) {
    	try {
    		HibernateSessionFactory.getSession().beginTransaction();
    		
    		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			properties.storeToXML(outStream, null);
			
			ModelHistory modelHistory = new ModelHistory();
			modelHistory.setModelId(UUID.randomUUID().toString());
			modelHistory.setContent(outStream.toString("utf-8"));
			modelHistory.setOperator("");
			modelHistory.setCreateBy("SYSTEM");
			
			HibernateSessionFactory.getSession().save(modelHistory);
			
			HibernateSessionFactory.getSession().getTransaction().commit();
		} catch (Exception e) {
			HibernateSessionFactory.getSession().getTransaction().rollback();
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
