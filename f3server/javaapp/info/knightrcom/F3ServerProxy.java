package info.knightrcom;

import info.knightrcom.command.message.EchoMessage;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.LogInfo;
import info.knightrcom.model.global.GameStatus;
import info.knightrcom.model.global.Lobby;
import info.knightrcom.model.global.Platform;
import info.knightrcom.model.global.Room;
import info.knightrcom.util.EncryptionUtil;
import info.knightrcom.util.HandlerDispatcher;
import info.knightrcom.util.ModelUtil;
import info.knightrcom.util.SystemLogger;
import info.knightrcom.web.model.EntityInfo;
import info.knightrcom.web.service.F3SWebServiceResult;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mina.core.session.IoSession;
import org.hibernate.Query;
import org.hibernate.Transaction;

/**
 * 该类作为Web访问应用服务器的代理，具体的应用操作内容由代理完成
 */
public class F3ServerProxy {

    /**
     * 日志类型
     */
    public static enum LogType {
        CLIENT_ERROR, SYSTEM_ERROR, WEB_ERROR, SYSTEM_LOG
    }

    /**
     * 反馈处理状态
     */
    public static enum FeedbackStatus {
    	NEW_ARRIVAL, IN_PROGRESS, DONE 
    }

    /**
	 * 获取所有Socket会话
	 * 
	 * @return
	 */
	public static Collection<IoSession> getAllSession() {
		return F3Server.acceptor.getManagedSessions().values();
	}

	/**
	 * 发布消息
	 * 
	 * @param session
	 * @param echoMessage
	 */
	public static void sessionWrite(IoSession session, EchoMessage echoMessage) {
		session.write(EncryptionUtil.Base64Encode(HandlerDispatcher.respondMessage(echoMessage)));
	}

	/**
	 * 启动游戏服务器
	 */
	public static void startServer() {
		F3Server.startServer(null);
	}

	/**
	 * 停止游戏服务器
	 */
	public static void stopServer() {
		F3Server.shutdownServer();
	}

	/**
	 * 服务器运行状态
	 * 
	 * @return
	 */
	public static boolean isServerRunning() {
		return F3Server.isRunning();
	}

	/**
	 * 获取服务器运行信息
	 * 
	 * @return
	 */
	public static Object getServerStatus() {
		String[] titles = new String[] {
				"USE_SSL",
				"PORT",
				"SECURITY_CONFIGURATION",
				"MAX_CONNECTION_LIMIT",
				"RUNNING",
				"USER_ONLINE"};
		String[] contents = new String[] {
				String.valueOf(F3Server.USE_SSL), 
				String.valueOf(F3Server.PORT),
				String.valueOf(F3Server.SECURITY_CONFIGURATION), 
				String.valueOf(F3Server.MAX_CONNECTION_LIMIT),
				String.valueOf(F3Server.RUNNING), 
				String.valueOf(F3Server.RUNNING ? F3Server.acceptor.getManagedSessionCount() : 0) };
	    Query dbVars = HibernateSessionFactory.getSession().createSQLQuery("show variables");
	    // dbVars.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	    List<?> dbVarList = dbVars.list();
		String[] result = new String[titles.length + System.getProperties().entrySet().size() + dbVarList.size()];
		int i = 0;
		for (i = 0; i < titles.length; i++) {
		    result[i] = titles[i] + "~" + contents[i];
        }
		Iterator<Entry<Object, Object>> propItr = System.getProperties().entrySet().iterator();
	    while (propItr.hasNext()) {
	        Entry<Object, Object> entry = propItr.next();
	        result[i] = entry.getKey().toString().toUpperCase() + "~" + entry.getValue();
	        i++;
	    }
	    Iterator<?> varItr = dbVarList.iterator();
        while (varItr.hasNext()) {
            Object[] row = (Object[])varItr.next();
            result[i] = row[0].toString().toLowerCase() + "~" + row[1];
            i++;
        }
		return result;
	}
	
	/**
	 * 获取大厅状态信息
	 * 
	 * @return
	 */
	public static Object getLobbyStatus() {
		String[] result = new String[GameStatus.values().length];
		Platform platform = ModelUtil.getPlatform();
		Map<String, Lobby> lobbyMap = platform.getChildren();
		int i = 0;
		for (Object lobbyMapId : lobbyMap.entrySet()) {
            Lobby lobby = lobbyMap.get(lobbyMapId.toString().split("=")[0]);
            // 过滤未启动的大厅
            if (Boolean.parseBoolean(lobby.getDisabled())) continue;
            for (Object roomMapId : lobby.getChildren().entrySet()) {
    			Room room = lobby.getChildren().get(roomMapId.toString().split("=")[0]);
                // 过滤未启动的房间
                if (Boolean.parseBoolean(room.getDisabled())) continue;
    			String gameInfo = "";
    			for (GameStatus gameStatus : GameStatus.values()) {
    				gameInfo += room.getGameStatusNumber(gameStatus) + "~";
    			}
    			result[i] = room.getName() + "~" + gameInfo + room.getChildSize();
                i++;
    		}
        }
		return result;
	}

	/**
	 * 创建日志信息
	 * 
	 * @param caption
	 * @param message
	 * @param info
	 * @param type
	 * @return
	 */
	public static LogInfo createLogInfo(String caption, String message, String info, LogType type) {
	    return SystemLogger.createLog(caption, message, info, type);
	}

	/**
	 * 备份数据库
	 * <ol>
	 * <li>备份生产数据库</li>
	 * <li>清空生产数据库中所有业务数据</li>
	 * <li>将生产数据库中的MASTER表中的数据全部初始化</li>
	 * </ol>
	 * @param yyyyMM 起始日期的年月，用六位表
	 * @param fromDay 起始时间的日，用两位数表示
	 * @param toDay 结束时间的日，用两位数表示
	 * @return 成功或失败，失败时会在Tag中记录失败原因
	 */
	public static EntityInfo<Object> backupDatabase(String yyyyMM, String fromDay, String toDay) {
	    Transaction trans = null;
        // 生成数据库标识
        String dbMark1 = yyyyMM;
        String dbMark2 = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String databaseName = String.format("f3s_%1$s_F%2$s_T%3$s_%4$s", dbMark1, fromDay, toDay, dbMark2);
	    try {
	        // 创建数据库
	        HibernateSessionFactory.getSession().createSQLQuery("create database " + databaseName + " CHARACTER SET utf8 COLLATE utf8_general_ci").executeUpdate();
	        // 获取全部的表
	        List<?> resultList = HibernateSessionFactory.getSession().createSQLQuery("show tables").list();
	        // 创建表结构
            for (Object eachResult : resultList) {
                String sql4CreateTable = String.format("create table %1$s.%2$s like f3s.%2$s", databaseName, eachResult.toString());
                HibernateSessionFactory.getSession().createSQLQuery(sql4CreateTable).executeUpdate();
            }
            // 开启事务
            trans = HibernateSessionFactory.getSession().beginTransaction();
	        // 向备份数据库中导入全部数据
            for (Object eachResult : resultList) {
                String sql4ImportTable = String.format("insert into %1$s.%2$s select * from f3s.%2$s", databaseName, eachResult.toString());
                HibernateSessionFactory.getSession().createSQLQuery(sql4ImportTable).executeUpdate();
            }
	        // 删除生产数据库中的业务数据
            for (String eachResult : new String[] {"recharge_record", "player_score", "periodly_sum", "log_info", "game_record", "game_feedback"}) {
                String sql4DeleteTable = String.format("delete from %1$s", eachResult);
                HibernateSessionFactory.getSession().createSQLQuery(sql4DeleteTable).executeUpdate();
            }
            // 重新初始化MASTER表中的数据
            HibernateSessionFactory.getSession().createSQLQuery("update player_profile set current_score = init_limit").executeUpdate();
            // 创建空表SUCCESS代表数据备份成功
            HibernateSessionFactory.getSession().createSQLQuery("create table " + databaseName + ".success (success int)").executeUpdate();
            // 提交事务
            trans.commit();
            // 返回结果
	        EntityInfo<Object> info = new EntityInfo<Object>();
	        info.setResult(F3SWebServiceResult.SUCCESS);
            return info;
        } catch (Exception e) {
            // 回滚事务
            if (trans != null && trans.isActive()) {
                trans.rollback();
            }
            trans.rollback();
            // 异常发生时删除数据库
            HibernateSessionFactory.getSession().createSQLQuery("drop database " + databaseName).executeUpdate();
            EntityInfo<Object> info = new EntityInfo<Object>();
            info.setResult(F3SWebServiceResult.FAIL);
            info.setTag(e.getMessage());
            return info;
        } finally {
            HibernateSessionFactory.closeSession();
        }
	}
}
