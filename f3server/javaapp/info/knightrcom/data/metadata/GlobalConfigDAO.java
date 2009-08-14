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
 * GlobalConfig entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see info.knightrcom.data.metadata.GlobalConfig
 * @author MyEclipse Persistence Tools
 */

public class GlobalConfigDAO extends BaseHibernateDAO {
    private static final Log log = LogFactory.getLog(GlobalConfigDAO.class);

    // property constants
    public static final String NUMBER = "number";

    public static final String NAME = "name";

    public static final String VALUE = "value";

    public static final String TYPE = "type";

    public static final String STATUS = "status";

    public static final String CREATE_BY = "createBy";

    public static final String UPDATE_BY = "updateBy";

    public void save(GlobalConfig transientInstance) {
        log.debug("saving GlobalConfig instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }

    public void delete(GlobalConfig persistentInstance) {
        log.debug("deleting GlobalConfig instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    public GlobalConfig findById(java.lang.String id) {
        log.debug("getting GlobalConfig instance with id: " + id);
        try {
            GlobalConfig instance = (GlobalConfig) getSession().get("info.knightrcom.data.metadata.GlobalConfig", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    public List<GlobalConfig> findByExample(GlobalConfig instance) {
        log.debug("finding GlobalConfig instance by example");
        try {
            List<GlobalConfig> results = (List<GlobalConfig>) getSession().createCriteria("info.knightrcom.data.metadata.GlobalConfig").add(create(instance)).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }

    public List findByProperty(String propertyName, Object value) {
        log.debug("finding GlobalConfig instance with property: " + propertyName + ", value: " + value);
        try {
            String queryString = "from GlobalConfig as model where model." + propertyName + "= ?";
            Query queryObject = getSession().createQuery(queryString);
            queryObject.setParameter(0, value);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find by property name failed", re);
            throw re;
        }
    }

    public List<GlobalConfig> findByNumber(Object number) {
        return findByProperty(NUMBER, number);
    }

    public List<GlobalConfig> findByName(Object name) {
        return findByProperty(NAME, name);
    }

    public List<GlobalConfig> findByValue(Object value) {
        return findByProperty(VALUE, value);
    }

    public List<GlobalConfig> findByType(Object type) {
        return findByProperty(TYPE, type);
    }

    public List<GlobalConfig> findByStatus(Object status) {
        return findByProperty(STATUS, status);
    }

    public List<GlobalConfig> findByCreateBy(Object createBy) {
        return findByProperty(CREATE_BY, createBy);
    }

    public List<GlobalConfig> findByUpdateBy(Object updateBy) {
        return findByProperty(UPDATE_BY, updateBy);
    }

    public List findAll() {
        log.debug("finding all GlobalConfig instances");
        try {
            String queryString = "from GlobalConfig";
            Query queryObject = getSession().createQuery(queryString);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
            throw re;
        }
    }

    public GlobalConfig merge(GlobalConfig detachedInstance) {
        log.debug("merging GlobalConfig instance");
        try {
            GlobalConfig result = (GlobalConfig) getSession().merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(GlobalConfig instance) {
        log.debug("attaching dirty GlobalConfig instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(GlobalConfig instance) {
        log.debug("attaching clean GlobalConfig instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}
