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
 * @see info.knightrcom.data.metadata.ModelHistory
 * @author MyEclipse Persistence Tools
 */

public class ModelHistoryDAO extends BaseHibernateDAO {
    private static final Log log = LogFactory.getLog(ModelHistoryDAO.class);

    // property constants
    public static final String OPERATOR = "operator";

    public static final String CONTENT = "content";

    public static final String CREATE_BY = "createBy";

    public static final String UPDATE_BY = "updateBy";

    public void save(ModelHistory transientInstance) {
        log.debug("saving RechargeHistory instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }

    public void delete(ModelHistory persistentInstance) {
        log.debug("deleting RechargeHistory instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    public ModelHistory findById(java.lang.String id) {
        log.debug("getting RechargeHistory instance with id: " + id);
        try {
            ModelHistory instance = (ModelHistory) getSession().get("info.knightrcom.data.metadata.RechargeHistory", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    public List<ModelHistory> findByExample(ModelHistory instance) {
        log.debug("finding RechargeHistory instance by example");
        try {
            List<ModelHistory> results = (List<ModelHistory>) getSession().createCriteria("info.knightrcom.data.metadata.RechargeHistory").add(create(instance)).list();
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

    public List<ModelHistory> findByOperator(Object operator) {
        return findByProperty(OPERATOR, operator);
    }

    public List<ModelHistory> findByContent(Object score) {
        return findByProperty(CONTENT, score);
    }

    public List<ModelHistory> findByCreateBy(Object createBy) {
        return findByProperty(CREATE_BY, createBy);
    }

    public List<ModelHistory> findByUpdateBy(Object updateBy) {
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

    public ModelHistory merge(ModelHistory detachedInstance) {
        log.debug("merging RechargeHistory instance");
        try {
            ModelHistory result = (ModelHistory) getSession().merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(ModelHistory instance) {
        log.debug("attaching dirty RechargeHistory instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(ModelHistory instance) {
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
