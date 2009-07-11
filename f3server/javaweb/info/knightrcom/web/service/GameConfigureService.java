package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.util.ModelUtil;
import info.knightrcom.web.constant.GameConfigureConstant;
import info.knightrcom.web.model.EntityInfo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

public class GameConfigureService extends F3SWebService<List<Map>> {

    @Override
    public Class<?>[] getAliasTypes() {
    	return new Class<?>[] {List.class};
    }

    @Override
    public String getNamedQuery() {
    	 return "GLOBAL_CONFIG";
    }

    @Override
    public String getNamedQueryForCount() {
    	return "GLOBAL_CONFIG_COUNT";
    }

    @Override
    public ResultTransformer getResultTransformer() {
    	return Transformers.ALIAS_TO_ENTITY_MAP;
    }

    @Override
    public void processQuerySetting(Query query, HttpServletRequest request) {
        // TODO Auto-generated method stub
        
    }
    
    @SuppressWarnings("unchecked")
	public String READ_GAME_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
    	// 设置页码
        int currentPage = 1;
        if (request.getParameter("CURRENT_PAGE").matches("[1-9]\\d*")) {
            currentPage = new Integer(request.getParameter("CURRENT_PAGE")).intValue();
        }
    	Properties config = ModelUtil.readProperties();
        String[] lobbyConfigArray = config.getProperty("LOBBY").split(";");
        List<Map> lobbyList = new ArrayList<Map>();
        for (String lobbyConfig : lobbyConfigArray) {
        	Map lobby = createFromConfigString(lobbyConfig, null);
        	lobbyList.add(lobby);
        }
        EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        info.setEntity(lobbyList);
        info.getPagination().setTotalRecord(lobbyList.size()*lobbyList.size());
        info.getPagination().setPageSize(lobbyList.size());
        info.getPagination().setCurrentPage(currentPage);
        return toXML(info, getAliasTypes());
    }
    
    @SuppressWarnings("unchecked")
	public String UPDATE_GAME_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
        String lobbyId = request.getParameter("LOBBY_ID");
        String lobbyName = request.getParameter("LOBBY_NAME");
        String lobbyDisplayIndex = request.getParameter("LOBBY_DISPLAYINDEX");
        String lobbyRoomCount = request.getParameter("LOBBY_ROOMCOUNT");
        // 根据LOBBY—ID读取数据源
        Properties config = ModelUtil.readProperties();
        
        // 重构Property
        Properties properties = new Properties();
        properties.setProperty("PLATFORM", config.getProperty("PLATFORM"));
        
        // 移除旧值更新新值
        String[] lobbyConfigArray = config.getProperty("LOBBY").split(";");
        List<Map> lobbyList = new ArrayList<Map>();
        for (String lobbyConfig : lobbyConfigArray) {
        	Map lobby = createFromConfigString(lobbyConfig, null);
        	if (lobby.get(GameConfigureConstant.LOBBY_ID).equals(lobbyId)) {
        		if (lobby.containsKey(GameConfigureConstant.LOBBY_NAME)) {
	        		lobby.remove(GameConfigureConstant.LOBBY_NAME);
	        		lobby.put(GameConfigureConstant.LOBBY_NAME, lobbyName);
        		}
        		if (lobby.containsKey(GameConfigureConstant.LOBBY_DISPLAYINDEX)) {
	        		lobby.remove(GameConfigureConstant.LOBBY_DISPLAYINDEX);
	        		lobby.put(GameConfigureConstant.LOBBY_DISPLAYINDEX, lobbyDisplayIndex);
        		}
        		if (lobby.containsKey(GameConfigureConstant.LOBBY_ROOMCOUNT)) {
	        		lobby.remove(GameConfigureConstant.LOBBY_ROOMCOUNT);
	        		lobby.put(GameConfigureConstant.LOBBY_ROOMCOUNT, lobbyRoomCount);
        		}
        	} 
        	lobbyList.add(lobby);
        }
        // 更新后的新值重构成configString
        properties.setProperty("LOBBY", createFromList(lobbyList));
        properties.setProperty("ROOM", config.getProperty("ROOM"));
        properties.setProperty("MAX_PLAYER_NUMBER", config.getProperty("MAX_PLAYER_NUMBER"));
        
        // 数据更新至DB
        try {
        	// 删除原始记录
        	Criteria criteria = HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class);
    		HibernateSessionFactory.getSession().delete(criteria.list().get(0));
    		// 插入新记录
    		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    		properties.storeToXML(outStream, null);
			GlobalConfig globalconfig = new GlobalConfig();
			globalconfig.setGlobalConfigId(UUID.randomUUID().toString());
			globalconfig.setValue(outStream.toString("utf-8"));
			HibernateSessionFactory.getSession().save(globalconfig);
        } catch (Exception e) {
			e.printStackTrace();
		}
        return READ_GAME_CONFIGURE(request, response);
    }

    public String UPDATE_RED5_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    public String UPDATE_FIGHT_LANDLORD_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    public String UPDATE_PUSHDOWN_WIN_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    public String UPDATE_QIONG_WIN_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
    /**
     * ConfigString -> Map
     * @param configString
     * @param exclusion
     * @return
     */
    @SuppressWarnings("unchecked")
	private Map createFromConfigString(String configString, String exclusion) {
        try {
            Map bean = new HashMap();
            String[] configProperties = configString.split(",");
            for (String currentConfig : configProperties) {
                String propName = currentConfig.split("=")[0];
                String propValue = currentConfig.split("=")[1];
                if (exclusion != null && exclusion.equals(propName)) {
                    continue;
                }
                bean.put(propName, propValue);
            }
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * LIST -> ConfigString
     * @param list
     * @return
     */
    @SuppressWarnings("unchecked")
	private String createFromList(List<Map> list) {
        try {
        	StringBuffer strbuf = new StringBuffer();
        	for (Map map : list) {
        		
        		Set keySet = map.keySet();
        		Iterator items = keySet.iterator();
        		while (items.hasNext()) {
        			String key = (String) items.next();
        			String val = (String) map.get(key);
        			strbuf.append(key);
        			strbuf.append("=");
        			strbuf.append(val);
        			if (items.hasNext()) {
        				strbuf.append(",");
        			}
        		}
        		strbuf.append(";");
        	}
        	String result = strbuf.toString();
        	if (result != null && result.length() > 0) {
        		return result.substring(0, result.length() - 1);
        	}
        	return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
