package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.util.ModelUtil;
import info.knightrcom.web.model.EntityInfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.criterion.MatchMode;
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
        // 取得PUPPET用户名、密码和游戏类型 TODO 需要排除已经登录的玩家
        List<PlayerProfile> resultList = HibernateSessionFactory.getSession()
                .createCriteria(PlayerProfile.class)
                .add(Restrictions.like(PlayerProfileDAO.STATUS, "puppet~" + gameType, MatchMode.START))
                .setMaxResults(maxResultsSize).list();
        EntityInfo<PlayerProfile> entity = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
        entity.setTag(targetPuppetLauncherURL);
        entity.setEntityList(resultList);
        return toXML(entity, new Class[]{List.class, PlayerProfile.class});
    }

    public static void main(String[] args) {
        System.out.println(new PuppetConsoleService().RETRIEVE_PUPPET_INFO(null, null));
    }
}
