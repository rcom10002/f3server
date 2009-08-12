package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.data.metadata.GlobalConfigDAO;
import info.knightrcom.web.constant.GameConfigureConstant;
import info.knightrcom.web.model.EntityInfo;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

@SuppressWarnings("unchecked")
public class ReportTemplateManageService extends F3SWebService<GlobalConfig> {

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
        query.setString(0, GameConfigureConstant.REPORT_TEMPLATE_MANAGE);
    }
    
    /**
	 * 报表模板读取
	 * @param request
	 * @param response
	 * @return
	 */
	public String READ_SQL_TEMPLATE(HttpServletRequest request, HttpServletResponse response) {
		final GlobalConfig globalconfig = new GlobalConfigDAO().findById(request.getParameter("GLOBALCONFIG_ID"));
        EntityInfo<GlobalConfig> info = new EntityInfo<GlobalConfig>();
        info.setEntity(globalconfig);
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {GlobalConfig.class});
    }
	
	/**
	 * 报表模板新增
	 * @param request
	 * @param response
	 * @return
	 */
	public String CREATE_SQL_TEMPLATE(HttpServletRequest request, HttpServletResponse response) {
		EntityInfo<GlobalConfig> info = new EntityInfo<GlobalConfig>();
		String title = request.getParameter("TITLE");
        String sqlContent = request.getParameter("CONTENT");
        if (getGlobalConfigListByName(title).size() > 0) {
        	info.setResult(F3SWebServiceResult.WARNING);
        	return toXML(info, new Class[] {GlobalConfig.class});
        }
		GlobalConfig globalconfig = new GlobalConfig();
		globalconfig.setGlobalConfigId(UUID.randomUUID().toString());
		globalconfig.setName(title);
		globalconfig.setValue(sqlContent);
		globalconfig.setType(GameConfigureConstant.REPORT_TEMPLATE_MANAGE);
		globalconfig.setCreateTime(new Date());
		globalconfig.setUpdateTime(new Date());
        HibernateSessionFactory.getSession().save(globalconfig);
        info.setEntity(globalconfig);
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {GlobalConfig.class});
    }
    
	/**
	 * 报表模板保存
	 * @param request
	 * @param response
	 * @return
	 */
	public String UPDATE_SQL_TEMPLATE(HttpServletRequest request, HttpServletResponse response) {
		EntityInfo<GlobalConfig> info = new EntityInfo<GlobalConfig>();
		String title = request.getParameter("TITLE");
        String sqlContent = request.getParameter("CONTENT");
		GlobalConfig globalconfig = new GlobalConfigDAO().findById(request.getParameter("GLOBALCONFIG_ID"));
		globalconfig.setName(title);
		globalconfig.setValue(sqlContent);
        globalconfig.setUpdateTime(new Date());
        HibernateSessionFactory.getSession().update(globalconfig);
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {GlobalConfig.class});
    }
	
	/**
	 * 报表模板删除
	 * @param request
	 * @param response
	 * @return
	 */
	public String DELETE_SQL_TEMPLATE(HttpServletRequest request, HttpServletResponse response) {
		GlobalConfig globalconfig = new GlobalConfigDAO().findById(request.getParameter("GLOBALCONFIG_ID"));
        new GlobalConfigDAO().delete(globalconfig);
        EntityInfo<GlobalConfig> info = new EntityInfo<GlobalConfig>();
        info.setResult(F3SWebServiceResult.SUCCESS);
        return toXML(info, new Class[] {GlobalConfig.class});
    }
	
	/**
	 * 判断报表模板名是否存在
	 * @param name
	 * @return
	 */
	private List<GlobalConfig> getGlobalConfigListByName(String name) {
		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(GlobalConfig.class);
        criteria.add(Expression.eq("type", GameConfigureConstant.REPORT_TEMPLATE_MANAGE));
        criteria.add(Expression.eq("name", name));
        return criteria.list();
	}
}
