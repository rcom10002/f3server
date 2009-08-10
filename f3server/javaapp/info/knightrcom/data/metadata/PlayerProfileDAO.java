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
 * PlayerProfile entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see info.knightrcom.data.metadata.PlayerProfile
 * @author MyEclipse Persistence Tools
 */

public class PlayerProfileDAO extends BaseHibernateDAO {
    private static final Log log = LogFactory.getLog(PlayerProfileDAO.class);

    // property constants
    public static final String NUMBER = "number";

    public static final String NAME = "name";

    public static final String USER_ID = "userId";

    public static final String PASSWORD = "password";

    public static final String CURRENT_SCORE = "currentScore";

    public static final String INIT_LIMIT = "initLimit";

    public static final String LEVEL = "level";

    public static final String RLS_PATH = "rlsPath";

    public static final String STATUS = "status";

    public static final String CREATE_BY = "createBy";

    public static final String UPDATE_BY = "updateBy";

    public static final String ROLE = "role";

    public void save(PlayerProfile transientInstance) {
        log.debug("saving PlayerProfile instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }

    public void delete(PlayerProfile persistentInstance) {
        log.debug("deleting PlayerProfile instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    public PlayerProfile findById(java.lang.String id) {
        log.debug("getting PlayerProfile instance with id: " + id);
        try {
            PlayerProfile instance = (PlayerProfile) getSession().get("info.knightrcom.data.metadata.PlayerProfile", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    public List<PlayerProfile> findByExample(PlayerProfile instance) {
        log.debug("finding PlayerProfile instance by example");
        try {
            List<PlayerProfile> results = (List<PlayerProfile>) getSession().createCriteria("info.knightrcom.data.metadata.PlayerProfile").add(create(instance)).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }

    public List findByProperty(String propertyName, Object value) {
        log.debug("finding PlayerProfile instance with property: " + propertyName + ", value: " + value);
        try {
            String queryString = "from PlayerProfile as model where model." + propertyName + "= ?";
            Query queryObject = getSession().createQuery(queryString);
            queryObject.setParameter(0, value);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find by property name failed", re);
            throw re;
        }
    }

    public List<PlayerProfile> findByNumber(Object number) {
        return findByProperty(NUMBER, number);
    }

    public List<PlayerProfile> findByName(Object name) {
        return findByProperty(NAME, name);
    }

    public List<PlayerProfile> findByUserId(Object userId) {
        return findByProperty(USER_ID, userId);
    }

    public List<PlayerProfile> findByPassword(Object password) {
        return findByProperty(PASSWORD, password);
    }

    public List<PlayerProfile> findByCurrentScore(Object currentScore) {
        return findByProperty(CURRENT_SCORE, currentScore);
    }

    public List<PlayerProfile> findByInitLimit(Object initLimit) {
        return findByProperty(INIT_LIMIT, initLimit);
    }

    public List<PlayerProfile> findByLevel(Object level) {
        return findByProperty(LEVEL, level);
    }

    public List<PlayerProfile> findByRlsPath(Object rlsPath) {
        return findByProperty(RLS_PATH, rlsPath);
    }

    public List<PlayerProfile> findByStatus(Object status) {
        return findByProperty(STATUS, status);
    }

    public List<PlayerProfile> findByCreateBy(Object createBy) {
        return findByProperty(CREATE_BY, createBy);
    }

    public List<PlayerProfile> findByUpdateBy(Object updateBy) {
        return findByProperty(UPDATE_BY, updateBy);
    }

    public List<PlayerProfile> findByRole(Object role) {
        return findByProperty(ROLE, role);
    }

    public List findAll() {
        log.debug("finding all PlayerProfile instances");
        try {
            String queryString = "from PlayerProfile";
            Query queryObject = getSession().createQuery(queryString);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
            throw re;
        }
    }

    public PlayerProfile merge(PlayerProfile detachedInstance) {
        log.debug("merging PlayerProfile instance");
        try {
            PlayerProfile result = (PlayerProfile) getSession().merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(PlayerProfile instance) {
        log.debug("attaching dirty PlayerProfile instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(PlayerProfile instance) {
        log.debug("attaching clean PlayerProfile instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}
