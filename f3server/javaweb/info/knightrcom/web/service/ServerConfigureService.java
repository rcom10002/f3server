package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.data.metadata.GlobalConfigDAO;
import info.knightrcom.web.constant.GameConfigureConstant;
import info.knightrcom.web.model.EntityInfo;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

@SuppressWarnings("unchecked")
public class ServerConfigureService extends F3SWebService<GlobalConfig> {

    @Override
    public Class<?>[] getAliasTypes() {
    	return new Class<?>[] {GlobalConfig.class};
    }

    @Override
    public String getNamedQuery() {
    	 return "GLOBAL_CONFIGURE";
    }

    @Override
    public String getNamedQueryForCount() {
    	return "GLOBAL_CONFIGURE_COUNT";
    }

    @Override
    public ResultTransformer getResultTransformer() {
    	return Transformers.ALIAS_TO_ENTITY_MAP;
    }

    @Override
    public void processQuerySetting(Query query, HttpServletRequest request) {
    	query.setString(0, GameConfigureConstant.SERVER_PARAM_NAME);
    }
    
    /**
	 * 服务器参数读取
	 * @param request
	 * @param response
	 * @return
	 */
	public String READ_SERVER_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
		final GlobalConfig globalconfig = new GlobalConfigDAO().findById(request.getParameter("GLOBALCONFIG_ID"));
        EntityInfo<GlobalConfig> info = new EntityInfo<GlobalConfig>();
        info.setEntity(globalconfig);
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {GlobalConfig.class});
    }
	
	/**
	 * 服务器参数新增
	 * @param request
	 * @param response
	 * @return
	 */
	public String CREATE_SERVER_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("VARIABLE_NAME");
        String value = request.getParameter("VARIABLE_VALUE");
		GlobalConfig globalconfig = new GlobalConfig();
		globalconfig.setGlobalConfigId(UUID.randomUUID().toString());
		globalconfig.setName(name);
		globalconfig.setValue(value);
		globalconfig.setType(GameConfigureConstant.SERVER_PARAM_NAME);
		globalconfig.setCreateTime(new Date());
		globalconfig.setUpdateTime(new Date());
        HibernateSessionFactory.getSession().save(globalconfig);
        EntityInfo<GlobalConfig> info = new EntityInfo<GlobalConfig>();
        info.setEntity(globalconfig);
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {GlobalConfig.class});
    }
    
	/**
	 * 服务器参数保存
	 * @param request
	 * @param response
	 * @return
	 */
	public String UPDATE_SERVER_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("VARIABLE_NAME");
        String value = request.getParameter("VARIABLE_VALUE");
		GlobalConfig globalconfig = new GlobalConfigDAO().findById(request.getParameter("GLOBALCONFIG_ID"));
		globalconfig.setName(name);
		globalconfig.setValue(value);
        globalconfig.setUpdateTime(new Date());
        HibernateSessionFactory.getSession().update(globalconfig);
        EntityInfo<GlobalConfig> info = new EntityInfo<GlobalConfig>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {GlobalConfig.class});
    }
	
	/**
	 * 服务器参数删除
	 * @param request
	 * @param response
	 * @return
	 */
	public String DELETE_SERVER_CONFIGURE(HttpServletRequest request, HttpServletResponse response) {
		GlobalConfig globalconfig = new GlobalConfigDAO().findById(request.getParameter("GLOBALCONFIG_ID"));
        new GlobalConfigDAO().delete(globalconfig);
        EntityInfo<GlobalConfig> info = new EntityInfo<GlobalConfig>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {GlobalConfig.class});
    }
}
