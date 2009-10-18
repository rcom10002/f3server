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
     * 充值(内部服务)
     * 
     * @param fromPlayer userId for recharging score
     * @param toPlayer userId for getting score
     * @param score recharege score
     * @return
     */
    static EntityInfo<RechargeRecord> INNER_SAVE_RECHARGE_RECORD(PlayerProfile fromPlayerProfile, PlayerProfile toPlayerProfile, double score) {
        // 设置玩家ID信息
        String fromPlayer = fromPlayerProfile.getUserId();
        String toPlayer = toPlayerProfile.getUserId();

        // 获取原始积分
        double fromCurScore = fromPlayerProfile.getCurrentScore() - score;
        double fromOrgScore = fromPlayerProfile.getCurrentScore();
        double toCurScore = toPlayerProfile.getCurrentScore() + score;
        double toOrgScore = toPlayerProfile.getCurrentScore();

        // 更新用户当前积分
        if ("GroupUser".equals(fromPlayerProfile.getRole())) {
            fromPlayerProfile.setCurrentScore(fromCurScore);
            fromPlayerProfile.setUpdateBy(fromPlayerProfile.getProfileId());
            fromPlayerProfile.setUpdateTime(new Date());
            HibernateSessionFactory.getSession().merge(fromPlayerProfile);
        }
        // 更新被充值玩家积分
        toPlayerProfile.setCurrentScore(toCurScore);
        toPlayerProfile.setUpdateBy(fromPlayerProfile.getProfileId());
        toPlayerProfile.setUpdateTime(new Date());
        HibernateSessionFactory.getSession().merge(toPlayerProfile);
        
        // 保存充值记录
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setRechargeId(UUID.randomUUID().toString());
        rechargeRecord.setFromPlayer(fromPlayer);
        if ("GroupUser".equals(fromPlayerProfile.getRole())) {
            // 组用户
            rechargeRecord.setFromOrgScore(fromOrgScore);
            rechargeRecord.setFromCurScore(fromCurScore);
        } else {
            // 系统管理员，超级游戏管理员，游戏管理员
            rechargeRecord.setFromOrgScore(-1d);
            rechargeRecord.setFromCurScore(-1d);
        }
        rechargeRecord.setScore(score);
        rechargeRecord.setToPlayer(toPlayer);
        rechargeRecord.setToOrgScore(toOrgScore);
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
        logInfo.setInfo("from user [" + fromPlayer + "] to user [" + toPlayer + "] add score [" + score + "] !" );
        logInfo.setType(LogType.SYSTEM_LOG.name());
        HibernateSessionFactory.getSession().save(logInfo);
        EntityInfo<RechargeRecord> info = new EntityInfo<RechargeRecord>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        return info;
    }

    /**
     * 充值(外部服务)
     * 
     * @param request
     * @param response
     * @return
     */
	public String SAVE_RECHARGE_RECORD(HttpServletRequest request, HttpServletResponse response) {
        String fromPlayer = request.getParameter("FROM_PLAYER");
        String score = request.getParameter("SCORE");
        String toPlayer = request.getParameter("TO_PLAYER");
        Criteria criteriaFrom = HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class);
        criteriaFrom.add(Expression.eq("userId", fromPlayer));
        PlayerProfile fromPlayerProfile = (PlayerProfile) criteriaFrom.uniqueResult();
        Criteria criteriaTo = HibernateSessionFactory.getSession().createCriteria(PlayerProfile.class);
        criteriaTo.add(Expression.eq("userId", toPlayer));
        PlayerProfile toPlayerProfile = (PlayerProfile) criteriaTo.uniqueResult();
        EntityInfo<RechargeRecord> info = INNER_SAVE_RECHARGE_RECORD(fromPlayerProfile, toPlayerProfile, Double.valueOf(score));
        return toXML(info, getAliasTypes());
    }
}
