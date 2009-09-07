package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.web.constant.GameConfigureConstant;
import info.knightrcom.web.model.EntityInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

@SuppressWarnings("unchecked")
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
        
    }
    
    /**
	 * 游戏大厅读取
	 * @param request
	 * @param response
	 * @return
	 */
	public String READ_GAME_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
    	// 设置页码
        int currentPage = 1;
        if (request.getParameter("CURRENT_PAGE").matches("[1-9]\\d*")) {
            currentPage = new Integer(request.getParameter("CURRENT_PAGE")).intValue();
        }
    	Properties config = getModelProperties();
        String[] lobbyConfigArray = config.getProperty("LOBBY").split(";");
        List<Map> lobbyList = new ArrayList<Map>();
        for (String lobbyConfig : lobbyConfigArray) {
        	Map map = readFromConfigString(lobbyConfig, null);
        	
        	// 获取房间数
        	String[] roomConfigArray = config.getProperty("ROOM").split(";");
            List<Map> roomList = new ArrayList<Map>();
            for (String roomConfig : roomConfigArray) {
            	Map room = readFromConfigString(roomConfig, null);
            	if (map.get(GameConfigureConstant.LOBBY_ID).equals(room.get(GameConfigureConstant.ROOM_PARENT))) {
            		roomList.add(room);
            	}
            }
        	map.put(GameConfigureConstant.LOBBY_ROOMCOUNT, roomList.size());
        	lobbyList.add(map);
        }
        EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        info.setEntity(lobbyList);
        info.getPagination().setPageSize(lobbyList.size());
        info.getPagination().setTotalRecord(lobbyList.size()*lobbyList.size());
        info.getPagination().setCurrentPage(currentPage);
        return toXML(info, getAliasTypes());
    }
	
	/**
	 * 游戏大厅保存
	 * @param request
	 * @param response
	 * @return
	 */
	public String UPDATE_GAME_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
		EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
	        String lobbyId = request.getParameter("LOBBY_ID");
	        String lobbyName = request.getParameter("LOBBY_NAME");
	        String lobbyDisplayIndex = request.getParameter("LOBBY_DISPLAYINDEX");
	        String lobbyStatus = request.getParameter("STATUS");
	        // 根据LOBBY—ID读取数据源
	        Properties config = getModelProperties();
	        
	        // 重构Property
	        Properties properties = new Properties();
	        properties.setProperty("PLATFORM", config.getProperty("PLATFORM"));
	        
	        // 移除旧值更新新值
	        String[] lobbyConfigArray = config.getProperty("LOBBY").split(";");
	        List<Map> lobbyList = new ArrayList<Map>();
	        for (String lobbyConfig : lobbyConfigArray) {
	        	Map lobby = saveFromConfigString(lobbyConfig, null);
	        	if (lobby.get(GameConfigureConstant.LOBBY_ID).equals(lobbyId)) {
	        		if (lobby.containsKey(GameConfigureConstant.LOBBY_NAME)) {
		        		lobby.remove(GameConfigureConstant.LOBBY_NAME);
		        		lobby.put(GameConfigureConstant.LOBBY_NAME, lobbyName);
	        		}
	        		if (lobby.containsKey(GameConfigureConstant.LOBBY_DISPLAYINDEX)) {
		        		lobby.remove(GameConfigureConstant.LOBBY_DISPLAYINDEX);
		        		lobby.put(GameConfigureConstant.LOBBY_DISPLAYINDEX, lobbyDisplayIndex);
	        		}
	        		if (lobby.containsKey(GameConfigureConstant.LOBBY_STATUS)) {
		        		lobby.remove(GameConfigureConstant.LOBBY_STATUS);
		        		lobby.put(GameConfigureConstant.LOBBY_STATUS, lobbyStatus);
	        		}
	        	} 
	        	lobbyList.add(lobby);
	        }
	        // 更新后的新值重构成configString
	        properties.setProperty("LOBBY", createFromList(lobbyList));
	        properties.setProperty("ROOM", config.getProperty("ROOM"));
	        properties.setProperty("MAX_PLAYER_NUMBER", config.getProperty("MAX_PLAYER_NUMBER"));
	        
	        // 数据更新至DB
	    	// 获取原始记录
	    	Criteria criteria = HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class);
	    	criteria.add(Expression.eq("name", GameConfigureConstant.GLOBAL_CONFIG_NAME));
	    	GlobalConfig globalConfig = (GlobalConfig) criteria.uniqueResult();
			// 更新记录
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			properties.storeToXML(outStream, null);
			globalConfig.setValue(outStream.toString("utf-8"));
			globalConfig.setUpdateTime(new Date());
			HibernateSessionFactory.getSession().save(globalConfig);
			info.setResult(F3SWebServiceResult.SUCCESS);
        } catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
        return toXML(info, getAliasTypes());
    }
	
	/**
	 * 游戏大厅删除
	 * @param request
	 * @param response
	 * @return
	 */
	public String DELETE_GAME_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
        String lobbyId = request.getParameter("LOBBY_ID");
        // 根据LOBBY—ID读取数据源
        Properties config = getModelProperties();
        
        // 重构Property
        Properties properties = new Properties();
        properties.setProperty("PLATFORM", config.getProperty("PLATFORM"));
        
        // 移除旧值更新新值
        String[] lobbyConfigArray = config.getProperty("LOBBY").split(";");
        List<Map> lobbyList = new ArrayList<Map>();
        for (String lobbyConfig : lobbyConfigArray) {
        	Map lobby = saveFromConfigString(lobbyConfig, null);
        	// 被删除的不重新写入
        	if (lobby.get(GameConfigureConstant.LOBBY_ID).equals(lobbyId)) {
        		continue;
        	} 
        	lobbyList.add(lobby);
        }
        // 更新后的新值重构成configString
        properties.setProperty("LOBBY", createFromList(lobbyList));
        properties.setProperty("ROOM", config.getProperty("ROOM"));
        properties.setProperty("MAX_PLAYER_NUMBER", config.getProperty("MAX_PLAYER_NUMBER"));
        
        // 数据更新至DB
        try {
        	// 获取原始记录
        	Criteria criteria = HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class);
        	criteria.add(Expression.eq("name", GameConfigureConstant.GLOBAL_CONFIG_NAME));
        	GlobalConfig globalConfig = (GlobalConfig) criteria.uniqueResult();
    		// 更新记录
    		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    		properties.storeToXML(outStream, null);
    		globalConfig.setValue(outStream.toString("utf-8"));
    		globalConfig.setUpdateTime(new Date());
			HibernateSessionFactory.getSession().save(globalConfig);
        } catch (Exception e) {
			e.printStackTrace();
		}
        return READ_GAME_CONFIGURE(request, response);
    }

	/**
	 * 房间读取
	 * @param request
	 * @param response
	 * @return
	 */
	public String READ_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
    	// 游戏类型
    	String gameType = request.getParameter("GAME_TYPE");
    	// 设置页码
        int currentPage = 1;
        if (request.getParameter("CURRENT_PAGE").matches("[1-9]\\d*")) {
            currentPage = new Integer(request.getParameter("CURRENT_PAGE")).intValue();
        }
    	Properties config = getModelProperties();
        String[] roomConfigArray = config.getProperty("ROOM").split(";");
        List<Map> roomList = new ArrayList<Map>();
        for (String roomConfig : roomConfigArray) {
        	Map room = readFromConfigString(roomConfig, null);
        	switch (Integer.valueOf(gameType)) {
			case 0:
				// 红五
				if (GameConfigureConstant.GAME_TYPE_NAME_POKER.equals(room.get(GameConfigureConstant.ROOM_PARENT)) 
						&& room.get(GameConfigureConstant.ROOM_ID).toString().length() >= GameConfigureConstant.GAME_TYPE_VALUE_RED5.length()
	        			&& GameConfigureConstant.GAME_TYPE_VALUE_RED5.equals(room.get(GameConfigureConstant.ROOM_ID).toString().substring(0,GameConfigureConstant.GAME_TYPE_VALUE_RED5.length()))) {
	        		roomList.add(room);
	        	}
				break;
			case 1:
				// 斗地主
				if (GameConfigureConstant.GAME_TYPE_NAME_POKER.equals(room.get(GameConfigureConstant.ROOM_PARENT)) 
						&& room.get(GameConfigureConstant.ROOM_ID).toString().length() >= GameConfigureConstant.GAME_TYPE_VALUE_FIGHTLANDLORD.length()
	        			&& GameConfigureConstant.GAME_TYPE_VALUE_FIGHTLANDLORD.equals(room.get(GameConfigureConstant.ROOM_ID).toString().substring(0,GameConfigureConstant.GAME_TYPE_VALUE_FIGHTLANDLORD.length()))) {
	        		roomList.add(room);
	        	}
				break;
			case 2:
				// 麻将[穷胡]
				if (GameConfigureConstant.GAME_TYPE_NAME_MAHJONG.equals(room.get(GameConfigureConstant.ROOM_PARENT)) 
						&& room.get(GameConfigureConstant.ROOM_ID).toString().length() >= GameConfigureConstant.GAME_TYPE_VALUE_MAHJONG_QIONG.length()
	        			&& GameConfigureConstant.GAME_TYPE_VALUE_MAHJONG_QIONG.equals(room.get(GameConfigureConstant.ROOM_ID).toString().substring(0,GameConfigureConstant.GAME_TYPE_VALUE_MAHJONG_QIONG.length()))) {
	        		roomList.add(room);
	        	}
				break;
			case 3:
				// 麻将[推倒]
				if (GameConfigureConstant.GAME_TYPE_NAME_MAHJONG.equals(room.get(GameConfigureConstant.ROOM_PARENT)) 
						&& room.get(GameConfigureConstant.ROOM_ID).toString().length() >= GameConfigureConstant.GAME_TYPE_VALUE_MAHJONG_PUSHDOWN.length()
	        			&& GameConfigureConstant.GAME_TYPE_VALUE_MAHJONG_PUSHDOWN.equals(room.get(GameConfigureConstant.ROOM_ID).toString().substring(0,GameConfigureConstant.GAME_TYPE_VALUE_MAHJONG_PUSHDOWN.length()))) {
	        		roomList.add(room);
	        	}
				break;
			default:
				break;
			}
        }
        EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        info.setEntity(roomList);
        info.getPagination().setPageSize(roomList.size());
        info.getPagination().setTotalRecord(roomList.size()*roomList.size());
        info.getPagination().setCurrentPage(currentPage);
        return toXML(info, getAliasTypes());
    }

	// 红五
	public String CREATE_RED5_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
		EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
        	createGameRoomConfigure(GameConfigureConstant.GAME_TYPE_NAME_POKER, GameConfigureConstant.GAME_TYPE_VALUE_RED5, request);
        	info.setResult(F3SWebServiceResult.SUCCESS);
        } catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
        return toXML(info, getAliasTypes());
    }
	
	public String UPDATE_RED5_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
		EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
        	updateGameRoomConfigure(GameConfigureConstant.GAME_TYPE_NAME_POKER, request);
        	info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
    	return toXML(info, getAliasTypes());
    }
	
	public String DELETE_RED5_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
		EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
	        deleteGameRoomConfigure(GameConfigureConstant.GAME_TYPE_NAME_POKER, request);
	        info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
    	return toXML(info, getAliasTypes());
    }

	// 斗地主
	public String CREATE_FIGHT_LANDLORD_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
		EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
			createGameRoomConfigure(GameConfigureConstant.GAME_TYPE_NAME_POKER, GameConfigureConstant.GAME_TYPE_VALUE_FIGHTLANDLORD, request);
			info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
    	return toXML(info, getAliasTypes());
    }
	
    public String UPDATE_FIGHT_LANDLORD_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
    		updateGameRoomConfigure(GameConfigureConstant.GAME_TYPE_NAME_POKER, request);
    		info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
    	return toXML(info, getAliasTypes());
    }
    
    public String DELETE_FIGHT_LANDLORD_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
	    	deleteGameRoomConfigure(GameConfigureConstant.GAME_TYPE_NAME_POKER, request);
	    	info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
    	return toXML(info, getAliasTypes());
    }

    // 推倒
    public String CREATE_PUSHDOWN_WIN_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
			createGameRoomConfigure(GameConfigureConstant.GAME_TYPE_NAME_MAHJONG, GameConfigureConstant.GAME_TYPE_VALUE_MAHJONG_PUSHDOWN, request);
			info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
    	return toXML(info, getAliasTypes());
    }
    
    public String UPDATE_PUSHDOWN_WIN_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
    		updateGameRoomConfigure(GameConfigureConstant.GAME_TYPE_NAME_MAHJONG, request);
    		info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
    	return toXML(info, getAliasTypes());
    }
    
    public String DELETE_PUSHDOWN_WIN_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
    		deleteGameRoomConfigure(GameConfigureConstant.GAME_TYPE_NAME_MAHJONG, request);
    		info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
    	return toXML(info, getAliasTypes());
    }

    // 穷胡
    public String CREATE_QIONG_WIN_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
			createGameRoomConfigure(GameConfigureConstant.GAME_TYPE_NAME_MAHJONG, GameConfigureConstant.GAME_TYPE_VALUE_MAHJONG_QIONG, request);
			info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
    	return toXML(info, getAliasTypes());
    }
    
    public String UPDATE_QIONG_WIN_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
	    	updateGameRoomConfigure(GameConfigureConstant.GAME_TYPE_NAME_MAHJONG, request);
			info.setResult(F3SWebServiceResult.SUCCESS);
    	} catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
    	return toXML(info, getAliasTypes());
    }
    
    public String DELETE_QIONG_WIN_ROOM_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
    		deleteGameRoomConfigure(GameConfigureConstant.GAME_TYPE_NAME_MAHJONG, request);
			info.setResult(F3SWebServiceResult.SUCCESS);
		} catch (Exception e) {
	    	e.printStackTrace();
	    	info.setResult(F3SWebServiceResult.FAIL);
	    }
		return toXML(info, getAliasTypes());
    }
    
    /**
     * 通用四种游戏的参数设置
     * @param lobbyId
     * @param request
     */
    private void deleteGameRoomConfigure(String lobbyId, HttpServletRequest request) {
		String gameId = request.getParameter("GAME_ID");
		// 根据LOBBY—ID读取数据源
        Properties config = getModelProperties();
        // 重构Property
        Properties properties = new Properties();
        properties.setProperty("PLATFORM", config.getProperty("PLATFORM"));
        properties.setProperty("LOBBY", config.getProperty("LOBBY"));
        String[] roomConfigArray = config.getProperty("ROOM").split(";");
        List<Map> roomList = new ArrayList<Map>();
        for (String roomConfig : roomConfigArray) {
        	Map room = saveFromConfigString(roomConfig, null);
        	// 被删除的房间不加载
			if (lobbyId.equals(room.get(GameConfigureConstant.ROOM_PARENT)) 
        			&& gameId.equals(room.get(GameConfigureConstant.ROOM_ID))) {
	        	continue;
			}
			roomList.add(room);
        }
        // 更新后的新值重构成configString
        properties.setProperty("ROOM", createFromList(roomList));
        
        properties.setProperty("MAX_PLAYER_NUMBER", config.getProperty("MAX_PLAYER_NUMBER"));
        
        // 数据更新至DB
        try {
        	// 获取原始记录
        	Criteria criteria = HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class);
        	criteria.add(Expression.eq("name", GameConfigureConstant.GLOBAL_CONFIG_NAME));
    		GlobalConfig globalconfig = (GlobalConfig)criteria.uniqueResult();
    		// 更新记录
    		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    		properties.storeToXML(outStream, null);
			globalconfig.setValue(outStream.toString("utf-8"));
			globalconfig.setUpdateTime(new Date());
			HibernateSessionFactory.getSession().save(globalconfig);
        } catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    
    /**
     * 通用四种游戏的参数设置
     * @param lobbyId
     * @param request
     */
	private void updateGameRoomConfigure(String lobbyId, HttpServletRequest request) {
        String gameId = request.getParameter("GAME_ID");
        String gameName = request.getParameter("GAME_NAME");
        String displayIndex = request.getParameter("DISPLAY_INDEX");
        String roundMark = request.getParameter("ROUND_MARK");
        String lowLevelMark = request.getParameter("LOW_LEVEL_MARK");
        String midLevelMark = request.getParameter("MID_LEVEL_MARK");
        String highLevelMark = request.getParameter("HIGH_LEVEL_MARK");
        String pointMark = request.getParameter("POINT_MARK");
        String minMarks = request.getParameter("MIN_MARKS");
        String status = request.getParameter("STATUS");
        // 根据LOBBY—ID读取数据源
        Properties config = getModelProperties();
        
        // 重构Property
        Properties properties = new Properties();
        properties.setProperty("PLATFORM", config.getProperty("PLATFORM"));
        properties.setProperty("LOBBY", config.getProperty("LOBBY"));
        // 移除旧值更新新值
        String[] roomConfigArray = config.getProperty("ROOM").split(";");
        List<Map> roomList = new ArrayList<Map>();
        for (String roomConfig : roomConfigArray) {
        	Map room = saveFromConfigString(roomConfig, null);
        	// 根据游戏类型及游戏ID各自设置其游戏参数
			if (lobbyId.equals(room.get(GameConfigureConstant.ROOM_PARENT)) 
        			&& gameId.equals(room.get(GameConfigureConstant.ROOM_ID))) {
        		if (room.containsKey(GameConfigureConstant.ROOM_NAME)) {
	        		room.remove(GameConfigureConstant.ROOM_NAME);
	        		room.put(GameConfigureConstant.ROOM_NAME, gameName);
        		}
        		if (room.containsKey(GameConfigureConstant.ROOM_DISPLAY_INDEX)) {
	        		room.remove(GameConfigureConstant.ROOM_DISPLAY_INDEX);
	        		room.put(GameConfigureConstant.ROOM_DISPLAY_INDEX, displayIndex);
        		}
        		if (room.containsKey(GameConfigureConstant.ROOM_ROUND_MARK)) {
	        		room.remove(GameConfigureConstant.ROOM_ROUND_MARK);
	        		room.put(GameConfigureConstant.ROOM_ROUND_MARK, roundMark);
        		}
        		// 如果是扑克类添加三种等级设置参数
		        if (room.containsKey(GameConfigureConstant.ROOM_LOW_LEVEL_MARK) && GameConfigureConstant.GAME_TYPE_NAME_POKER.equals(lobbyId)) {
		        	room.remove(GameConfigureConstant.ROOM_LOW_LEVEL_MARK);
	        		room.put(GameConfigureConstant.ROOM_LOW_LEVEL_MARK, lowLevelMark);
		        }
		        if (room.containsKey(GameConfigureConstant.ROOM_MID_LEVEL_MARK) && GameConfigureConstant.GAME_TYPE_NAME_POKER.equals(lobbyId)) {
		        	room.remove(GameConfigureConstant.ROOM_MID_LEVEL_MARK);
	        		room.put(GameConfigureConstant.ROOM_MID_LEVEL_MARK, midLevelMark);
		        }
		        if (room.containsKey(GameConfigureConstant.ROOM_HIGH_LEVEL_MARK) && GameConfigureConstant.GAME_TYPE_NAME_POKER.equals(lobbyId)) {
		        	room.remove(GameConfigureConstant.ROOM_HIGH_LEVEL_MARK);
	        		room.put(GameConfigureConstant.ROOM_HIGH_LEVEL_MARK, highLevelMark);
		        }
		        
        		// 如果是麻将类添加番分参数
		        if (room.containsKey(GameConfigureConstant.ROOM_POINT_MARK) && GameConfigureConstant.GAME_TYPE_NAME_MAHJONG.equals(lobbyId)) {
		        	room.remove(GameConfigureConstant.ROOM_POINT_MARK);
	        		room.put(GameConfigureConstant.ROOM_POINT_MARK, pointMark);
		        }
        		if (room.containsKey(GameConfigureConstant.ROOM_MIN_MARKS)) {
	        		room.remove(GameConfigureConstant.ROOM_MIN_MARKS);
	        		room.put(GameConfigureConstant.ROOM_MIN_MARKS, minMarks);
        		}
        		
        		// 启用禁用状态
        		if (room.containsKey(GameConfigureConstant.ROOM_STATUS)) {
	        		room.remove(GameConfigureConstant.ROOM_STATUS);
	        		room.put(GameConfigureConstant.ROOM_STATUS, status);
        		}
        	}
        	roomList.add(room);
        }
        // 更新后的新值重构成configString
        properties.setProperty("ROOM", createFromList(roomList));
        
        properties.setProperty("MAX_PLAYER_NUMBER", config.getProperty("MAX_PLAYER_NUMBER"));
        
        // 数据更新至DB
        try {
        	// 获取原始记录
        	Criteria criteria = HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class);
        	criteria.add(Expression.eq("name", GameConfigureConstant.GLOBAL_CONFIG_NAME));
    		GlobalConfig globalconfig = (GlobalConfig)criteria.uniqueResult();
    		// 更新记录
    		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    		properties.storeToXML(outStream, null);
			globalconfig.setValue(outStream.toString("utf-8"));
			globalconfig.setUpdateTime(new Date());
			HibernateSessionFactory.getSession().save(globalconfig);
        } catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	/**
     * 通用四种游戏的参数设置
     * @param lobbyId
     * @param prefixGameId
     * @param request
	 * @throws IOException 
     */
	private void createGameRoomConfigure(String lobbyId, String prefixGameId, HttpServletRequest request) throws IOException {
        String gameId = request.getParameter("GAME_ID");
        String gameName = request.getParameter("GAME_NAME");
        String pointMark = request.getParameter("POINT_MARK");
        String roundMark = request.getParameter("ROUND_MARK");
        String lowLevelMark = request.getParameter("LOW_LEVEL_MARK");
        String midLevelMark = request.getParameter("MID_LEVEL_MARK");
        String highLevelMark = request.getParameter("HIGH_LEVEL_MARK");
        String minMarks = request.getParameter("MIN_MARKS");
        String status = request.getParameter("STATUS");
        // add flag
        boolean bool = true;
        // 根据LOBBY—ID读取数据源
        Properties config = getModelProperties();
        
        // 重构Property
        Properties properties = new Properties();
        properties.setProperty("PLATFORM", config.getProperty("PLATFORM"));
        properties.setProperty("LOBBY", config.getProperty("LOBBY"));
        // 移除旧值更新新值
        String[] roomConfigArray = config.getProperty("ROOM").split(";");
        List<Map> roomList = new ArrayList<Map>();
        for (String roomConfig : roomConfigArray) {
        	Map room = saveFromConfigString(roomConfig, null);
        	// 根据游戏类型及游戏ID各自设置其游戏参数
			if (lobbyId.equals(room.get(GameConfigureConstant.ROOM_PARENT)) 
        			&& gameId.equals(room.get(GameConfigureConstant.ROOM_ID))) {
				// id 相同，禁止新增
				return;
        	}
			if (bool && lobbyId.equals(room.get(GameConfigureConstant.ROOM_PARENT)) && room.get(GameConfigureConstant.ROOM_ID).toString().indexOf(prefixGameId) >= 0){
				Map newRoom = new LinkedHashMap();
		        newRoom.put(GameConfigureConstant.ROOM_PARENT, lobbyId);
		        newRoom.put(GameConfigureConstant.ROOM_ID, prefixGameId + gameId);
		        newRoom.put(GameConfigureConstant.ROOM_NAME, gameName);
		        newRoom.put(GameConfigureConstant.ROOM_DISPLAY_INDEX, String.valueOf(getMaxDisplayIndex(lobbyId)));
		        newRoom.put(GameConfigureConstant.ROOM_ROUND_MARK, roundMark);
		        // 如果是扑克类添加三种等级设置参数
		        if (GameConfigureConstant.GAME_TYPE_NAME_POKER.equals(lobbyId)) {
		        	newRoom.put(GameConfigureConstant.ROOM_LOW_LEVEL_MARK, lowLevelMark);
		        	newRoom.put(GameConfigureConstant.ROOM_MID_LEVEL_MARK, midLevelMark);
		        	newRoom.put(GameConfigureConstant.ROOM_HIGH_LEVEL_MARK, highLevelMark);
		        }
		        
		        // 如果是麻将类添加番分参数
		        if (GameConfigureConstant.GAME_TYPE_NAME_MAHJONG.equals(lobbyId)) {
		        	newRoom.put(GameConfigureConstant.ROOM_POINT_MARK, pointMark);
		        }
		        newRoom.put(GameConfigureConstant.ROOM_MIN_MARKS, minMarks);
		        
		        newRoom.put(GameConfigureConstant.ROOM_STATUS, status);
		        roomList.add(newRoom);
		        bool = !bool;
			}
        	roomList.add(room);
        }
        
        // 更新后的新值重构成configString
        properties.setProperty("ROOM", createFromList(roomList));
        
        properties.setProperty("MAX_PLAYER_NUMBER", config.getProperty("MAX_PLAYER_NUMBER"));
        
        // 数据更新至DB
    	// 获取原始记录
    	Criteria criteria = HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class);
    	criteria.add(Expression.eq("name", GameConfigureConstant.GLOBAL_CONFIG_NAME));
		GlobalConfig globalconfig = (GlobalConfig)criteria.uniqueResult();
		// 更新记录
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		properties.storeToXML(outStream, null);
		globalconfig.setValue(outStream.toString("utf-8"));
		globalconfig.setUpdateTime(new Date());
		HibernateSessionFactory.getSession().save(globalconfig);
    }
    
    
    
	/**
	 * Read Status
     * ConfigString -> Map
     * @param configString
     * @param exclusion
     * @return
     */
	private Map readFromConfigString(String configString, String exclusion) {
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
     * Save Status
     * ConfigString -> Map
     * @param configString
     * @param exclusion
     * @return
     */
	private Map saveFromConfigString(String configString, String exclusion) {
        try {
            Map bean = new LinkedHashMap();
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
//        		return result.substring(0, result.length() - 1);
        		result = result.replaceAll(";$", "");
        	}
        	return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	/**
	 * 获取最大房间位置号
	 * @param lobbyId
	 * @return
	 */
	private int getMaxDisplayIndex(String lobbyId) {
    	Properties config = getModelProperties();
        String[] roomConfigArray = config.getProperty("ROOM").split(";");
        int maxDisplayIndex = 0;
        for (String roomConfig : roomConfigArray) {
        	Map room = readFromConfigString(roomConfig, null);
        	if (lobbyId.equals(room.get(GameConfigureConstant.ROOM_PARENT)))  {
        		int displayIndex = Integer.parseInt((String) room.get(GameConfigureConstant.ROOM_DISPLAY_INDEX));
        		if (displayIndex > maxDisplayIndex) {
        			maxDisplayIndex = displayIndex;
        		}
        	}
        }
        if (maxDisplayIndex != 0 ) {
        	maxDisplayIndex++;
        }
        return maxDisplayIndex;
    }

	private Properties getModelProperties() {
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
}
