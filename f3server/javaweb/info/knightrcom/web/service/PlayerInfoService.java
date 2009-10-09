package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.PlayerProfileDAO;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.util.EncryptionUtil;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.model.EntityInfo;
import info.knightrcom.web.model.entity.TodayInfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

public class PlayerInfoService extends F3SWebService<PlayerScore> {

    public void processQuerySetting(Query query, HttpServletRequest request) {
        query.setString(0, request.getParameter("CURRENT_PROFILE_ID"));
        query.setTimestamp(1, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(2, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setInteger(3, new Integer(request.getParameter("SHOW_CONDITION")));
        query.setInteger(4, new Integer(request.getParameter("SHOW_CONDITION")));
        query.setString(5, request.getParameter("GAME_TYPE"));
        query.setString(6, request.getParameter("GAME_TYPE"));
    }

    public String getNamedQuery() {
        return "SCORE_INFO";
    }

    public String getNamedQueryForCount() {
        return "SCORE_INFO_COUNT";
    }

    public ResultTransformer getResultTransformer() {
        return Transformers.aliasToBean(TodayInfo.class);
    }

    @Override
    public Class<?>[] getAliasTypes() {
        return new Class<?>[] {PlayerScore.class, TodayInfo.class};
    }

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String VIEW_CURRENT_PLAYER_INFO(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String profileId = request.getParameter("CURRENT_PROFILE_ID");
    	final PlayerProfile myProfile = new PlayerProfileDAO().findById(profileId);
    	Query query = HibernateSessionFactory.getSession().getNamedQuery("VIEW_CURRENT_PLAYER_INFO");
    	query.setParameter(0, profileId);
    	query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
    	final List<?> historyRechargeList = query.list();
    	EntityInfo<PlayerScore> info = createEntityInfo(null, F3SWebServiceResult.SUCCESS);
    	info.setTag(new Object() {
    		@SuppressWarnings("unused")
			PlayerProfile profile = myProfile;
    		@SuppressWarnings("unused")
    		List<?> historyRecharges = historyRechargeList;
    	});
    	HibernateSessionFactory.closeSession();
//    	<?xml version="1.0" encoding="utf-8"?>
//    	<EntityInfo>
//    	  <pagination>
//    	    <log class="org.apache.commons.logging.impl.Log4JLogger">
//    	      <name>info.knightrcom.web.model.Pagination</name>
//    	    </log>
//    	    <totalRecord>-1</totalRecord>
//    	    <currentPage>-1</currentPage>
//    	    <pageSize>15</pageSize>
//    	    <totalPage>0</totalPage>
//    	  </pagination>
//    	  <result>SUCCESS</result>
//    	  <tag class="info.knightrcom.web.service.PlayerInfoService$1">
//    	    <profile>
//    	      <profileId>5a3bbbc9-afff-46a9-84e0-134cd58aa4e7</profileId>
//    	      <name>user1</name>
//    	      <userId>user1</userId>
//    	      <password>s9qne0wEqVUbh4HQMZH+CY8yXmc=</password>
//    	      <currentScore>400</currentScore>
//    	      <initLimit>500</initLimit>
//    	      <level>0</level>
//    	      <rlsPath>user1</rlsPath>
//    	      <role>GroupUser</role>
//    	      <status>1</status>
//    	      <createTime class="sql-timestamp">2009-09-16 23:37:30.0</createTime>
//    	      <createBy>SYSTEM</createBy>
//    	      <updateTime class="sql-timestamp">2009-09-16 23:37:30.0</updateTime>
//    	      <updateBy>SYSTEM</updateBy>
//    	    </profile>
//    	    <historyRecharges>
//    	      <map>
//    	        <toCurScore>1500</toCurScore>
//    	        <fromPlayer>2e1221b4-9dd8-4e8f-baa5-8db6ac730e59</fromPlayer>
//    	        <toOrgScore>500</toOrgScore>
//    	      </map>
//    	      <map>
//    	        <toCurScore>2000</toCurScore>
//    	        <fromPlayer>2e1221b4-9dd8-4e8f-baa5-8db6ac730e59</fromPlayer>
//    	        <toOrgScore>1500</toOrgScore>
//    	      </map>
//    	    </historyRecharges>
//    	    <outer-class>
//    	      <log class="org.apache.commons.logging.impl.Log4JLogger">
//    	        <name>info.knightrcom.web.service.PlayerInfoService</name>
//    	      </log>
//    	    </outer-class>
//    	  </tag>
//    	</EntityInfo>
    	return toXML(info, PlayerProfile.class);
	}

    /**
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String CHANGE_PASSWORD(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	String profileId = request.getParameter("CURRENT_PROFILE_ID");
    	String orgPassword = request.getParameter("OLD_PWD");
    	String newPassword = request.getParameter("OLD_PWD");
    	PlayerProfile myProfile = new PlayerProfileDAO().findById(profileId);
    	if (!myProfile.getPassword().equals(EncryptionUtil.encryptSHA(orgPassword))) {
    		return toXML(createEntityInfo(null, F3SWebServiceResult.FAIL), PlayerScore.class);
    	}
    	myProfile.setPassword(EncryptionUtil.encryptSHA(newPassword));
    	HibernateSessionFactory.getSession().save(myProfile);
    	HibernateSessionFactory.closeSession();
//    	<?xml version="1.0" encoding="utf-8"?>
//    	<EntityInfo>
//    	  <pagination>
//    	    <log class="org.apache.commons.logging.impl.Log4JLogger">
//    	      <name>info.knightrcom.web.model.Pagination</name>
//    	    </log>
//    	    <totalRecord>-1</totalRecord>
//    	    <currentPage>-1</currentPage>
//    	    <pageSize>15</pageSize>
//    	    <totalPage>0</totalPage>
//    	  </pagination>
//    	  <result>SUCCESS</result>
//    	</EntityInfo>
		return toXML(createEntityInfo(null, F3SWebServiceResult.SUCCESS), PlayerScore.class);
	}

}
