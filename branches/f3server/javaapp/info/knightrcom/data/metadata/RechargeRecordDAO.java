package info.knightrcom.data.metadata;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;

/**
 * A data access object (DAO) providing persistence and search support for
 * RechargeRecord entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see info.knightrcom.data.metadata.RechargeRecord
 * @author MyEclipse Persistence Tools
 */

public class RechargeRecordDAO extends BaseHibernateDAO {
    private static final Log log = LogFactory.getLog(RechargeRecordDAO.class);

    // property constants
    public static final String FROM_PLAYER = "fromPlayer";

    public static final String FROM_ORG_SCORE = "fromOrgScore";

    public static final String FROM_CUR_SCORE = "fromCurScore";

    public static final String TO_PLAYER = "toPlayer";

    public static final String TO_ORG_SCORE = "toOrgScore";

    public static final String TO_CUR_SCORE = "toCurScore";

    public static final String MEMO = "memo";

    public static final String STATUS = "status";

    public static final String CREATE_BY = "createBy";

    public static final String UPDATE_BY = "updateBy";

    public void save(RechargeRecord transientInstance) {
        log.debug("saving RechargeRecord instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }

    public void delete(RechargeRecord persistentInstance) {
        log.debug("deleting RechargeRecord instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    public RechargeRecord findById(java.lang.String id) {
        log.debug("getting RechargeRecord instance with id: " + id);
        try {
            RechargeRecord instance = (RechargeRecord) getSession().get("info.knightrcom.data.metadata.RechargeRecord", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
	public List<RechargeRecord> findByExample(RechargeRecord instance) {
        log.debug("finding RechargeRecord instance by example");
        try {
            List<RechargeRecord> results = (List<RechargeRecord>) getSession().createCriteria("info.knightrcom.data.metadata.RechargeRecord").add(create(instance)).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }

    @SuppressWarnings("unchecked")
	public List<RechargeRecord> findByProperty(String propertyName, Object value) {
        log.debug("finding RechargeRecord instance with property: " + propertyName + ", value: " + value);
        try {
            String queryString = "from RechargeRecord as model where model." + propertyName + "= ?";
            Query queryObject = getSession().createQuery(queryString);
            queryObject.setParameter(0, value);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find by property name failed", re);
            throw re;
        }
    }

    public List<RechargeRecord> findByFromPlayer(Object fromPlayer) {
        return findByProperty(FROM_PLAYER, fromPlayer);
    }

    public List<RechargeRecord> findByFromOrgScore(Object fromOrgScore) {
        return findByProperty(FROM_ORG_SCORE, fromOrgScore);
    }

    public List<RechargeRecord> findByFromCurScore(Object fromCurScore) {
        return findByProperty(FROM_CUR_SCORE, fromCurScore);
    }

    public List<RechargeRecord> findByToPlayer(Object toPlayer) {
        return findByProperty(TO_PLAYER, toPlayer);
    }

    public List<RechargeRecord> findByToOrgScore(Object toOrgScore) {
        return findByProperty(TO_ORG_SCORE, toOrgScore);
    }

    public List<RechargeRecord> findByToCurScore(Object toCurScore) {
        return findByProperty(TO_CUR_SCORE, toCurScore);
    }

    public List<RechargeRecord> findByMemo(Object memo) {
        return findByProperty(MEMO, memo);
    }

    public List<RechargeRecord> findByStatus(Object status) {
        return findByProperty(STATUS, status);
    }

    public List<RechargeRecord> findByCreateBy(Object createBy) {
        return findByProperty(CREATE_BY, createBy);
    }

    public List<RechargeRecord> findByUpdateBy(Object updateBy) {
        return findByProperty(UPDATE_BY, updateBy);
    }

    @SuppressWarnings("unchecked")
	public List<RechargeRecord> findAll() {
        log.debug("finding all RechargeRecord instances");
        try {
            String queryString = "from RechargeRecord";
            Query queryObject = getSession().createQuery(queryString);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
            throw re;
        }
    }

    public RechargeRecord merge(RechargeRecord detachedInstance) {
        log.debug("merging RechargeRecord instance");
        try {
            RechargeRecord result = (RechargeRecord) getSession().merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(RechargeRecord instance) {
        log.debug("attaching dirty RechargeRecord instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(RechargeRecord instance) {
        log.debug("attaching clean RechargeRecord instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}
