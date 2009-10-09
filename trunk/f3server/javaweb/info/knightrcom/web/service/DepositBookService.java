package info.knightrcom.web.service;

import info.knightrcom.F3ServerProxy.LogType;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.LogInfo;
import info.knightrcom.data.metadata.PlayerProfile;
import info.knightrcom.data.metadata.RechargeRecord;
import info.knightrcom.web.model.EntityInfo;

import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

public class DepositBookService extends F3SWebService<RechargeRecord> {

	@Override
	public Class<?>[] getAliasTypes() {
		return new Class<?>[] {RechargeRecord.class, Object[].class, BigInteger.class};
	}

	@Override
	public String getNamedQuery() {
		return null;
	}

	@Override
	public String getNamedQueryForCount() {
		return null;
	}

	@Override
	public ResultTransformer getResultTransformer() {
		return Transformers.ALIAS_TO_ENTITY_MAP;
	}

	@Override
	public void processQuerySetting(Query query, HttpServletRequest request) {
		query.setString(0, request.getParameter("CURRENT_USER_ID"));
	}

    /**
     * 我的充值记录
     * 
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("unchecked")
	public String GET_MY_RECHARGE_RECORD(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<RechargeRecord> info = new EntityInfo<RechargeRecord>();
    	Query query = HibernateSessionFactory.getSession().getNamedQuery("MY_RECHARGE_RECORD");
    	processQuerySetting(query, request);
    	query.setResultTransformer(getResultTransformer());
    	info.setEntityList(query.list());
        return toXML(info, getAliasTypes());
    }

    /**
     * 玩家的充值记录
     * 
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("unchecked")
	public String GET_PLAYER_RECHARGE_RECORD(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<RechargeRecord> info = new EntityInfo<RechargeRecord>();
    	Query query = HibernateSessionFactory.getSession().getNamedQuery("PLAYER_RECHARGE_RECORD");
    	processQuerySetting(query, request);
    	query.setResultTransformer(getResultTransformer());
    	info.setEntityList(query.list());
        return toXML(info, getAliasTypes());
    }

    /**
     * 充值
     * 
     * @param request
     * @param response
     * @return
     */
	public String SAVE_RECHARGE_RECORD(HttpServletRequest request, HttpServletResponse response) {
    	EntityInfo<RechargeRecord> info = new EntityInfo<RechargeRecord>();
        String fromPlayer = request.getParameter("FROM_PLAYER");
        String fromOrgScore = request.getParameter("FROM_ORG_SCORE");
        String score = request.getParameter("SCORE");
        int fromCurScore = Integer.valueOf(fromOrgScore) - Integer.valueOf(score);
        String toPlayer = request.getParameter("TO_PLAYER");
        String toOrgScore = request.getParameter("TO_ORG_SCORE");
        int toCurScore = Integer.valueOf(toOrgScore) + Integer.valueOf(score);
        
    	// 获取原始积分
    	Criteria criteriaFrom = HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class);
    	criteriaFrom.add(Expression.eq("userId", fromPlayer));
    	PlayerProfile fromPlayerProfile = (PlayerProfile) criteriaFrom.uniqueResult();

    	Criteria criteriaTo = HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class);
    	criteriaTo.add(Expression.eq("userId", toPlayer));
    	PlayerProfile toPlayerProfile = (PlayerProfile) criteriaTo.uniqueResult();
		// 更新用户当前积分
    	if (fromPlayerProfile != null) {
    		fromPlayerProfile.setCurrentScore(fromCurScore);
    		fromPlayerProfile.setUpdateBy(fromPlayerProfile.getProfileId());
    		fromPlayerProfile.setUpdateTime(new Date());
    	}
		HibernateSessionFactory.getSession().save(fromPlayerProfile);
		
		if (toPlayerProfile != null) {
			toPlayerProfile.setCurrentScore(toCurScore);
			toPlayerProfile.setUpdateBy(fromPlayerProfile.getProfileId());
			toPlayerProfile.setUpdateTime(new Date());
    	}
		HibernateSessionFactory.getSession().save(toPlayerProfile);
		
		// 保存充值记录
		RechargeRecord rechargeRecord = new RechargeRecord();
		rechargeRecord.setRechargeId(UUID.randomUUID().toString());
		rechargeRecord.setFromPlayer(fromPlayer);
		rechargeRecord.setFromOrgScore(Integer.parseInt(fromOrgScore));
		rechargeRecord.setFromCurScore(fromCurScore);
		rechargeRecord.setScore(Integer.parseInt(score));
		rechargeRecord.setToPlayer(toPlayer);
		rechargeRecord.setToOrgScore(Integer.parseInt(toOrgScore));
		rechargeRecord.setToCurScore(toCurScore);
		rechargeRecord.setCreateBy(fromPlayerProfile.getProfileId());
		rechargeRecord.setCreateTime(new Date());
		rechargeRecord.setUpdateBy(fromPlayerProfile.getProfileId());
		rechargeRecord.setUpdateTime(new Date());
		HibernateSessionFactory.getSession().save(rechargeRecord);
		
		// 保存日志
		LogInfo logInfo = new LogInfo();
        logInfo.setLogId(UUID.randomUUID().toString());
        logInfo.setCaption("DepositBookService Successfully");
        logInfo.setKeyCause1(fromPlayer);
        logInfo.setKeyCause2(toPlayer);
        logInfo.setKeyCause3(String.valueOf(score));
        logInfo.setInfo("from user [" + fromPlayer + "] to user [" + toPlayer + "] add score is [" + score + "] !" );
        logInfo.setType(LogType.SYSTEM_LOG.name());
        HibernateSessionFactory.getSession().save(logInfo);
		info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, getAliasTypes());
    }
}
