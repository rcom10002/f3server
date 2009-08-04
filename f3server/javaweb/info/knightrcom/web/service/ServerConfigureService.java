package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.util.ModelUtil;
import info.knightrcom.web.constant.GameConfigureConstant;
import info.knightrcom.web.model.EntityInfo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

@SuppressWarnings("unchecked")
public class ServerConfigureService extends F3SWebService<List<Map>> {

    @Override
    public Class<?>[] getAliasTypes() {
    	return new Class<?>[] {List.class};
    }

    @Override
    public String getNamedQuery() {
    	 return "GLOBAL_CONFIG";
    }

    @Override
    public String getNamedQueryForCount() {
    	return "GLOBAL_CONFIG_COUNT";
    }

    @Override
    public ResultTransformer getResultTransformer() {
    	return Transformers.ALIAS_TO_ENTITY_MAP;
    }

    @Override
    public void processQuerySetting(Query query, HttpServletRequest request) {
        
    }
    
    /**
	 * 服务器参数读取
	 * @param request
	 * @param response
	 * @return
	 */
	public String READ_SERVER_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
    	// 设置页码
        int currentPage = 1;
        if (request.getParameter("CURRENT_PAGE").matches("[1-9]\\d*")) {
            currentPage = new Integer(request.getParameter("CURRENT_PAGE")).intValue();
        }
    	Properties configParams = ModelUtil.readProperties(GameConfigureConstant.SERVER_PARAM_NAME);
    	
    	List<Map> configMaps = new ArrayList<Map>();
    	for (Entry<Object, Object> param : configParams.entrySet()) {
    		Map map = new HashMap();
    		map.put(param.getKey(), param.getValue());
    		configMaps.add(map);
	    }

        EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        info.setEntity(configMaps);
        info.getPagination().setPageSize(configMaps.size());
        info.getPagination().setTotalRecord(configMaps.size()*configMaps.size());
        info.getPagination().setCurrentPage(currentPage);
        return toXML(info, getAliasTypes());
    }
	
	/**
	 * 服务器参数新增
	 * @param request
	 * @param response
	 * @return
	 */
	public String CREATE_SERVER_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
		EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
    		String variableName = request.getParameter("VARIABLE_NAME");
	        String variableValue = request.getParameter("VARIABLE_VALUE");
	        Properties propertiesOrig = ModelUtil.readProperties(GameConfigureConstant.SERVER_PARAM_NAME);
	        propertiesOrig.setProperty(variableName.toUpperCase(), variableValue);
	        // 数据更新至DB
	    	// 获取原始记录
	    	Criteria criteria = HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class);
	    	criteria.add(Expression.eq("name", GameConfigureConstant.SERVER_PARAM_NAME));
	    	GlobalConfig globalConfig = (GlobalConfig) criteria.uniqueResult();
			// 更新记录
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			propertiesOrig.storeToXML(outStream, null);
			globalConfig.setValue(outStream.toString("utf-8"));
			globalConfig.setUpdateTime(new Date());
			HibernateSessionFactory.getSession().save(globalConfig);
			info.setResult(F3SWebServiceResult.SUCCESS);
        } catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
        return toXML(info, getAliasTypes());
    }
    
	/**
	 * 服务器参数保存
	 * @param request
	 * @param response
	 * @return
	 */
	public String UPDATE_SERVER_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
		EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
	        String variableName = request.getParameter("VARIABLE_NAME");
	        String variableNameTager = toStandVariableName(variableName);
	        String variableValue = request.getParameter("VARIABLE_VALUE");
	        Properties properties = ModelUtil.readProperties(GameConfigureConstant.SERVER_PARAM_NAME);
	    	
	    	for (Entry<Object, Object> param : properties.entrySet()) {
	    		// 更新变量值
	    		if (param.getKey().equals(variableNameTager)) {
	    			 properties.setProperty(variableNameTager, variableValue);
	    		}
		    }
	        
	        // 数据更新至DB
	    	// 获取原始记录
	    	Criteria criteria = HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class);
	    	criteria.add(Expression.eq("name", GameConfigureConstant.SERVER_PARAM_NAME));
	    	GlobalConfig globalConfig = (GlobalConfig) criteria.uniqueResult();
			// 更新记录
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			properties.storeToXML(outStream, null);
			globalConfig.setValue(outStream.toString("utf-8"));
			globalConfig.setUpdateTime(new Date());
			HibernateSessionFactory.getSession().save(globalConfig);
			info.setResult(F3SWebServiceResult.SUCCESS);
        } catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
        return toXML(info, getAliasTypes());
    }
	
	/**
	 * 服务器参数删除
	 * @param request
	 * @param response
	 * @return
	 */
	public String DELETE_SERVER_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
		EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
    	try {
	        String variableName = request.getParameter("VARIABLE_NAME");
	        String variableNameTager = toStandVariableName(variableName);
	        Properties properties = ModelUtil.readProperties(GameConfigureConstant.SERVER_PARAM_NAME);
	        // 重构参数列表[将删除的参数键值不重新加载]
	        Properties propertiesDest = new Properties();
	    	for (Entry<Object, Object> param : properties.entrySet()) {
	    		// 更新变量值
	    		if (param.getKey().equals(variableNameTager)) {
	    			continue;
	    		}
	    		propertiesDest.put(param.getKey(), param.getValue());
		    }
	        
	        // 数据更新至DB
	    	// 获取原始记录
	    	Criteria criteria = HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class);
	    	criteria.add(Expression.eq("name", GameConfigureConstant.SERVER_PARAM_NAME));
	    	GlobalConfig globalConfig = (GlobalConfig) criteria.uniqueResult();
			// 更新记录
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			propertiesDest.storeToXML(outStream, null);
			globalConfig.setValue(outStream.toString("utf-8"));
			globalConfig.setUpdateTime(new Date());
			HibernateSessionFactory.getSession().save(globalConfig);
			info.setResult(F3SWebServiceResult.SUCCESS);
        } catch (Exception e) {
        	e.printStackTrace();
        	info.setResult(F3SWebServiceResult.FAIL);
        }
        return toXML(info, getAliasTypes());
    }
	
	/**
	 * 转成标准的变量名
	 * 
	 * minVirtualOnlineNumber -> MIN_VIRTUAL_ONLINE_NUMBER
	 * @param variableName
	 * @return
	 */
	private String toStandVariableName(String variableName) {
		StringBuffer strBuf = new StringBuffer();
        char chr[] = variableName.toCharArray();
        for (int i = 0; i < chr.length; i++) {
        	if (("" + chr[i]).matches("[A-Z]")) {
        		strBuf.append("_");
	        }
        	strBuf.append("" + chr[i]);
		}
        return strBuf.toString().toUpperCase();
	}
}
