package info.knightrcom.web.service;

import info.knightrcom.F3ServerProxy;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.model.global.Player;
import info.knightrcom.util.ModelUtil;
import info.knightrcom.web.model.EntityInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mina.core.session.IoSession;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class PuppetConsoleService extends F3SWebServiceAdaptor<PlayerProfile> {

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public String RETRIEVE_PUPPET_INFO(HttpServletRequest request,
            HttpServletResponse response) {
        // 游戏类型
        String gameType = request.getParameter("GAME_TYPE");
        // 取得的最大结果数
        int maxResultsSize = new Integer(request.getParameter("MAX_RESULTS_SIZE"));
        // 取得URL
        final String targetPuppetLauncherURL = ModelUtil.getSystemParameter("PUPPET_LAUNCHER_URL");
        // 取得已经登录的玩家
        List loginUserIds = new ArrayList(); 
        Collection<IoSession> sessions = F3ServerProxy.getAllSession();
        Iterator<IoSession> itr = sessions.iterator();
        while (itr.hasNext()) {
            Player player = (Player)itr.next().getAttribute(Player.ATTR_NAME);
            if (player != null) {
            	loginUserIds.add(player.getId());
            }
        }
        // 取得PUPPET用户名、密码和游戏类型
        Criteria criteria = HibernateSessionFactory.getSession()
                .createCriteria(PlayerProfile.class)
                .add(Restrictions.like(PlayerProfileDAO.STATUS, "puppet~" + gameType, MatchMode.START))
                .setMaxResults(maxResultsSize)
                .addOrder(Order.desc("currentScore"))
                .addOrder(Order.desc("createTime"));
        // 排除已经登录的玩家
        if (loginUserIds.size() > 0) {
        	criteria.add(Restrictions.not(Restrictions.in("userId", loginUserIds)));
        }
        List resultList = criteria.list();
        EntityInfo<PlayerProfile> entity = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
        entity.setTag(targetPuppetLauncherURL);
        entity.setEntityList(resultList);
        return toXML(entity, new Class[]{List.class, PlayerProfile.class});
    }
    
    @SuppressWarnings("unchecked")
	public String LIST_PUPPET_INFO(HttpServletRequest request,
            HttpServletResponse response) {
    	// 游戏类型
        String gameType = request.getParameter("GAME_TYPE");
    	Collection<IoSession> sessions = F3ServerProxy.getAllSession();
    	List resultList = new ArrayList();
        Iterator<IoSession> itr = sessions.iterator();
        while (itr.hasNext()) {
            Player player = (Player)itr.next().getAttribute(Player.ATTR_NAME);
            if (player != null) {
            	List<PlayerProfile> playerProfileList = HibernateSessionFactory.getSession()
                .createCriteria(PlayerProfile.class)
                .add(Restrictions.like(PlayerProfileDAO.STATUS, "puppet~" + gameType, MatchMode.START))
                .add(Restrictions.eq("userId", player.getId())).list();
            	if (playerProfileList != null && playerProfileList.size() > 0) {
                	PlayerProfile playerProfile = playerProfileList.get(0);
                	Map map = new HashMap();
                    map.put("pupuetname", playerProfile.getName());
                    map.put("currentscore", playerProfile.getCurrentScore());
                    map.put("currentstatus", player.getCurrentStatus());
                    Date lastPlayTime = new Date();
                    lastPlayTime.setTime(player.getLastPlayTime());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    map.put("lastgametime", sdf.format(lastPlayTime));
                    map.put("starttime", sdf.format(playerProfile.getCreateTime()));
                    map.put("runingtime", (new Date().getTime() - player.getLastPlayTime())/1000);
                    resultList.add(map);
            	}
            }
        }
    	EntityInfo<PlayerProfile> entity = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
        entity.setTag(resultList);
    	return toXML(entity, new Class[]{List.class, Player.class});
    }
    
    public static void main(String[] args) {
        System.out.println(new PuppetConsoleService().RETRIEVE_PUPPET_INFO(null, null));
    }
}
