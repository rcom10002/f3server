package info.knightrcom.data.metadata;

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

	public static final String DISPLAY_NAME = "displayName";
	
    public static final String VALUE = "value";
    
	public static final String DESC0 = "desc0";
	
	public static final String DESC1 = "desc1";
	
	public static final String DESC2 = "desc2";
	
	public static final String DESC3 = "desc3";
	
	public static final String DESC4 = "desc4";
	
	public static final String DESC5 = "desc5";
	
	public static final String DESC6 = "desc6";
	
	public static final String DESC7 = "desc7";
	
	public static final String DESC8 = "desc8";
	
	public static final String DESC9 = "desc9";
	
	public static final String DISPLAY_INDEX = "displayIndex";

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

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
	public List<GlobalConfig> findByProperty(String propertyName, Object value) {
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

	public List<GlobalConfig> findByDisplayName(Object displayName) {
		return findByProperty(DISPLAY_NAME, displayName);
	}

    public List<GlobalConfig> findByValue(Object value) {
        return findByProperty(VALUE, value);
    }

	public List<GlobalConfig> findByDesc0(Object desc0) {
		return findByProperty(DESC0, desc0);
	}

	public List<GlobalConfig> findByDesc1(Object desc1) {
		return findByProperty(DESC1, desc1);
	}

	public List<GlobalConfig> findByDesc2(Object desc2) {
		return findByProperty(DESC2, desc2);
	}

	public List<GlobalConfig> findByDesc3(Object desc3) {
		return findByProperty(DESC3, desc3);
	}

	public List<GlobalConfig> findByDesc4(Object desc4) {
		return findByProperty(DESC4, desc4);
	}

	public List<GlobalConfig> findByDesc5(Object desc5) {
		return findByProperty(DESC5, desc5);
	}

	public List<GlobalConfig> findByDesc6(Object desc6) {
		return findByProperty(DESC6, desc6);
	}

	public List<GlobalConfig> findByDesc7(Object desc7) {
		return findByProperty(DESC7, desc7);
	}

	public List<GlobalConfig> findByDesc8(Object desc8) {
		return findByProperty(DESC8, desc8);
	}

	public List<GlobalConfig> findByDesc9(Object desc9) {
		return findByProperty(DESC9, desc9);
	}

	public List<GlobalConfig> findByDisplayIndex(Object displayIndex) {
		return findByProperty(DISPLAY_INDEX, displayIndex);
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

    @SuppressWarnings("unchecked")
	public List<GlobalConfig> findAll() {
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
