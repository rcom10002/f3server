package info.knightrcom.data.metadata;

import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Query;
import static org.hibernate.criterion.Example.create;

/**
 * A data access object (DAO) providing persistence and search support for
 * RechargeHistory entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see info.knightrcom.data.metadata.RechargeHistory
 * @author MyEclipse Persistence Tools
 */

public class RechargeHistoryDAO extends BaseHibernateDAO {
    private static final Log log = LogFactory.getLog(RechargeHistoryDAO.class);

    // property constants
    public static final String PROFILE_ID = "profileId";

    public static final String OPERATOR = "operator";

    public static final String SCORE = "score";

    public static final String CREATE_BY = "createBy";

    public static final String UPDATE_BY = "updateBy";

    public void save(RechargeHistory transientInstance) {
        log.debug("saving RechargeHistory instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }

    public void delete(RechargeHistory persistentInstance) {
        log.debug("deleting RechargeHistory instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    public RechargeHistory findById(java.lang.String id) {
        log.debug("getting RechargeHistory instance with id: " + id);
        try {
            RechargeHistory instance = (RechargeHistory) getSession().get("info.knightrcom.data.metadata.RechargeHistory", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    public List<RechargeHistory> findByExample(RechargeHistory instance) {
        log.debug("finding RechargeHistory instance by example");
        try {
            List<RechargeHistory> results = (List<RechargeHistory>) getSession().createCriteria("info.knightrcom.data.metadata.RechargeHistory").add(create(instance)).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }

    public List findByProperty(String propertyName, Object value) {
        log.debug("finding RechargeHistory instance with property: " + propertyName + ", value: " + value);
        try {
            String queryString = "from RechargeHistory as model where model." + propertyName + "= ?";
            Query queryObject = getSession().createQuery(queryString);
            queryObject.setParameter(0, value);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find by property name failed", re);
            throw re;
        }
    }

    public List<RechargeHistory> findByProfileId(Object profileId) {
        return findByProperty(PROFILE_ID, profileId);
    }

    public List<RechargeHistory> findByOperator(Object operator) {
        return findByProperty(OPERATOR, operator);
    }

    public List<RechargeHistory> findByScore(Object score) {
        return findByProperty(SCORE, score);
    }

    public List<RechargeHistory> findByCreateBy(Object createBy) {
        return findByProperty(CREATE_BY, createBy);
    }

    public List<RechargeHistory> findByUpdateBy(Object updateBy) {
        return findByProperty(UPDATE_BY, updateBy);
    }

    public List findAll() {
        log.debug("finding all RechargeHistory instances");
        try {
            String queryString = "from RechargeHistory";
            Query queryObject = getSession().createQuery(queryString);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
            throw re;
        }
    }

    public RechargeHistory merge(RechargeHistory detachedInstance) {
        log.debug("merging RechargeHistory instance");
        try {
            RechargeHistory result = (RechargeHistory) getSession().merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(RechargeHistory instance) {
        log.debug("attaching dirty RechargeHistory instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(RechargeHistory instance) {
        log.debug("attaching clean RechargeHistory instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}
