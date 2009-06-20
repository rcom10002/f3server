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
 * PeriodlySum entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see info.knightrcom.data.metadata.PeriodlySum
 * @author MyEclipse Persistence Tools
 */

public class PeriodlySumDAO extends BaseHibernateDAO {
    private static final Log log = LogFactory.getLog(PeriodlySumDAO.class);

    // property constants
    public static final String PROFILE_ID = "profileId";

    public static final String NUMBER = "number";

    public static final String SCORE = "score";

    public static final String SYSTEM_SCORE = "systemScore";

    public static final String STATUS = "status";

    public static final String CREATE_BY = "createBy";

    public static final String UPDATE_BY = "updateBy";

    public void save(PeriodlySum transientInstance) {
        log.debug("saving PeriodlySum instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }

    public void delete(PeriodlySum persistentInstance) {
        log.debug("deleting PeriodlySum instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    public PeriodlySum findById(java.lang.String id) {
        log.debug("getting PeriodlySum instance with id: " + id);
        try {
            PeriodlySum instance = (PeriodlySum) getSession().get("info.knightrcom.data.metadata.PeriodlySum", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    public List<PeriodlySum> findByExample(PeriodlySum instance) {
        log.debug("finding PeriodlySum instance by example");
        try {
            List<PeriodlySum> results = (List<PeriodlySum>) getSession().createCriteria("info.knightrcom.data.metadata.PeriodlySum").add(create(instance)).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }

    public List findByProperty(String propertyName, Object value) {
        log.debug("finding PeriodlySum instance with property: " + propertyName + ", value: " + value);
        try {
            String queryString = "from PeriodlySum as model where model." + propertyName + "= ?";
            Query queryObject = getSession().createQuery(queryString);
            queryObject.setParameter(0, value);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find by property name failed", re);
            throw re;
        }
    }

    public List<PeriodlySum> findByProfileId(Object profileId) {
        return findByProperty(PROFILE_ID, profileId);
    }

    public List<PeriodlySum> findByNumber(Object number) {
        return findByProperty(NUMBER, number);
    }

    public List<PeriodlySum> findByScore(Object score) {
        return findByProperty(SCORE, score);
    }

    public List<PeriodlySum> findBySystemScore(Object systemScore) {
        return findByProperty(SYSTEM_SCORE, systemScore);
    }

    public List<PeriodlySum> findByStatus(Object status) {
        return findByProperty(STATUS, status);
    }

    public List<PeriodlySum> findByCreateBy(Object createBy) {
        return findByProperty(CREATE_BY, createBy);
    }

    public List<PeriodlySum> findByUpdateBy(Object updateBy) {
        return findByProperty(UPDATE_BY, updateBy);
    }

    public List findAll() {
        log.debug("finding all PeriodlySum instances");
        try {
            String queryString = "from PeriodlySum";
            Query queryObject = getSession().createQuery(queryString);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
            throw re;
        }
    }

    public PeriodlySum merge(PeriodlySum detachedInstance) {
        log.debug("merging PeriodlySum instance");
        try {
            PeriodlySum result = (PeriodlySum) getSession().merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(PeriodlySum instance) {
        log.debug("attaching dirty PeriodlySum instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(PeriodlySum instance) {
        log.debug("attaching clean PeriodlySum instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}
