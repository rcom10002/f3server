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
 * GameFeedback entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see info.knightrcom.data.metadata.GameFeedback
 * @author MyEclipse Persistence Tools
 */

public class GameFeedbackDAO extends BaseHibernateDAO {
    private static final Log log = LogFactory.getLog(GameFeedbackDAO.class);

    // property constants
    public static final String GAME_ID = "gameId";

    public static final String NUMBER = "number";

    public static final String TITLE = "title";

    public static final String DESCRIPTION = "description";

    public static final String STATUS = "status";

    public static final String CREATE_BY = "createBy";

    public static final String UPDATE_BY = "updateBy";

    public void save(GameFeedback transientInstance) {
        log.debug("saving GameFeedback instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }

    public void delete(GameFeedback persistentInstance) {
        log.debug("deleting GameFeedback instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    public GameFeedback findById(java.lang.String id) {
        log.debug("getting GameFeedback instance with id: " + id);
        try {
            GameFeedback instance = (GameFeedback) getSession().get("info.knightrcom.data.metadata.GameFeedback", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    public List<GameFeedback> findByExample(GameFeedback instance) {
        log.debug("finding GameFeedback instance by example");
        try {
            List<GameFeedback> results = (List<GameFeedback>) getSession().createCriteria("info.knightrcom.data.metadata.GameFeedback").add(create(instance)).list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }

    public List findByProperty(String propertyName, Object value) {
        log.debug("finding GameFeedback instance with property: " + propertyName + ", value: " + value);
        try {
            String queryString = "from GameFeedback as model where model." + propertyName + "= ?";
            Query queryObject = getSession().createQuery(queryString);
            queryObject.setParameter(0, value);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find by property name failed", re);
            throw re;
        }
    }

    public List<GameFeedback> findByGameId(Object gameId) {
        return findByProperty(GAME_ID, gameId);
    }

    public List<GameFeedback> findByNumber(Object number) {
        return findByProperty(NUMBER, number);
    }

    public List<GameFeedback> findByTitle(Object title) {
        return findByProperty(TITLE, title);
    }

    public List<GameFeedback> findByDescription(Object description) {
        return findByProperty(DESCRIPTION, description);
    }

    public List<GameFeedback> findByStatus(Object status) {
        return findByProperty(STATUS, status);
    }

    public List<GameFeedback> findByCreateBy(Object createBy) {
        return findByProperty(CREATE_BY, createBy);
    }

    public List<GameFeedback> findByUpdateBy(Object updateBy) {
        return findByProperty(UPDATE_BY, updateBy);
    }

    public List findAll() {
        log.debug("finding all GameFeedback instances");
        try {
            String queryString = "from GameFeedback";
            Query queryObject = getSession().createQuery(queryString);
            return queryObject.list();
        } catch (RuntimeException re) {
            log.error("find all failed", re);
            throw re;
        }
    }

    public GameFeedback merge(GameFeedback detachedInstance) {
        log.debug("merging GameFeedback instance");
        try {
            GameFeedback result = (GameFeedback) getSession().merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(GameFeedback instance) {
        log.debug("attaching dirty GameFeedback instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(GameFeedback instance) {
        log.debug("attaching clean GameFeedback instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}
