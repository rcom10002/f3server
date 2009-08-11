package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.web.model.EntityInfo;
import info.knightrcom.web.model.Pagination;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 *
 */
public abstract class F3SWebService<T> {

    protected Log log = LogFactory.getLog(this.getClass());

    public abstract void processQuerySetting(Query query, HttpServletRequest request);

    public abstract String getNamedQuery();

    public abstract String getNamedQueryForCount();

    public abstract ResultTransformer getResultTransformer();

    public abstract Class<?>[] getAliasTypes();

    /**
     * @param key 简单类名称
     * @param value 方法
     */
    protected F3SWebService() {
        String className = this.getClass().getSimpleName();
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method thisMethod : methods) {
            if (thisMethod.getName().matches("^[A-Z0-9_]+$")) {
                log.info("Method[#] is successfully loaded!".replace("#", thisMethod.getName()));
                F3SWebServiceProxy.registerWebServiceProcesser(className, thisMethod.getName(), thisMethod);
            }
        }
    }

    /**
     * 创建要返回的实体对象
     * @param entity
     * @param result
     * @return
     */
    protected EntityInfo<T> createEntityInfo(T entity, F3SWebServiceResult result) {
        EntityInfo<T> info = new EntityInfo<T>();
        info.setEntity(entity);
        info.setResult(result);
        return info;
    }

    /**
     * 创建要返回的实体对象
     * @param entity
     * @param result
     * @return
     */
    protected EntityInfo<Object> createGeneralEntityInfo(Object entity, F3SWebServiceResult result) {
        EntityInfo<Object> info = new EntityInfo<Object>();
        info.setEntity(entity);
        info.setResult(result);
        return info;
    }

    @SuppressWarnings("unchecked")
    public String serializeResponseStream(HttpServletRequest request, HttpServletResponse response) {
        // 准备查询条件
        EntityInfo<T> info = new EntityInfo<T>();

        // 取得记录总条数
        Query query = HibernateSessionFactory.getSession().getNamedQuery(getNamedQueryForCount());
        this.processQuerySetting(query, request);
        int recordCount = ((Map<String, BigInteger>)query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult()).get("RECORD_COUNT").intValue();

        // 准备执行查询
        query = HibernateSessionFactory.getSession().getNamedQuery(getNamedQuery());
        this.processQuerySetting(query, request);
        query.setResultTransformer(this.getResultTransformer());
        // 设置页码
        int currentPage = 1;
        if (request.getParameter("CURRENT_PAGE").matches("[1-9]\\d*")) {
            currentPage = new Integer(request.getParameter("CURRENT_PAGE")).intValue();
        }
        info.getPagination().setTotalRecord(recordCount);
        info.getPagination().setCurrentPage(currentPage);
        // 设置结果集
        query.setMaxResults(Pagination.DEFAULT_PAGE_SIZE);
        query.setFirstResult(Pagination.DEFAULT_PAGE_SIZE * (info.getPagination().getCurrentPage() - 1));
        List<T> list = (List<T>)query.list();
        info.setEntityList(list);

        log.debug(toXML(info, getAliasTypes()));
        return toXML(info, getAliasTypes());
    }

    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    protected String toXML(EntityInfo<T> entityInfo, Class<?> ... elementTypes) {
        XStream stream = new XStream();
        stream.alias(EntityInfo.class.getSimpleName(), EntityInfo.class);
        stream.registerConverter(new Converter() {

            public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
                Map<String, String> map = (Map<String, String>)value;
                Set<String> keys = ((Map<String, String>)value).keySet();
                for (String key : keys) {
                    StringBuilder prettyKey = new StringBuilder();
                    for (String innerKey : key.toLowerCase().split("_+")) {
                        prettyKey.append(innerKey.toUpperCase().charAt(0)).append(innerKey.substring(1));
                    }
                    writer.startNode(prettyKey.substring(0, 1).toLowerCase() + prettyKey.substring(1));
                    if (map.get(key) != null) {
                        writer.setValue(String.valueOf(map.get(key)));
                    }
                    writer.endNode();
                }
            }

            public Object unmarshal(HierarchicalStreamReader arg0, UnmarshallingContext arg1) {
                return null;
            }

            public boolean canConvert(Class type) {
                return Arrays.asList(type.getInterfaces()).contains(Map.class);
            }
            
        });
        for (Class thisType : elementTypes) {
            stream.alias(thisType.getSimpleName(), thisType);
        }
        stream.setMode(XStream.NO_REFERENCES);
        String responseText = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + stream.toXML(entityInfo);
        log.debug(responseText);
        return responseText;
    }
}
