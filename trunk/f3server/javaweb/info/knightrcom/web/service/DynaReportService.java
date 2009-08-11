package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.data.metadata.GlobalConfigDAO;
import info.knightrcom.web.model.EntityInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;


public class DynaReportService extends F3SWebServiceAdaptor {

	public Class<?>[] getAliasTypes() {
    	return new Class<?>[] {List.class};
    }
	
	/**
	 * 读取SQL返回结果
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String READ_DYNA_REPORT(HttpServletRequest request, HttpServletResponse response) {
		EntityInfo<List<Map>> info = new EntityInfo<List<Map>>();
		try {
			String mth = request.getParameter("MTH");
			String sql = request.getParameter("SQL");
			String templateId = request.getParameter("TEMPLATE_ID");
			
			// 判断是否选择的是报表模板
			if (mth.equals("0")) {
				final GlobalConfig globalconfig = new GlobalConfigDAO().findById(templateId);
				sql = globalconfig.getValue();
			}
			// 每页显示20条数据
	        info.getPagination().setPageSize(20);
			// 设置页码
	        int currentPage = 1;
	        if (request.getParameter("CURRENT_PAGE").matches("[1-9]\\d*")) {
	            currentPage = new Integer(request.getParameter("CURRENT_PAGE")).intValue();
	        }
	        
			Query query = HibernateSessionFactory.getSession().createQuery(sql);
			List<Map> result = query.list();
			
//			SQLQuery query = HibernateSessionFactory.getSession().createSQLQuery(sql);
//			List<Map> result = query.list();
			
	        info.getPagination().setTotalRecord(result.size());
	        info.getPagination().setCurrentPage(currentPage);
	        // 设置结果集
	        query.setMaxResults(info.getPagination().getPageSize());
	        query.setFirstResult(info.getPagination().getPageSize() * (info.getPagination().getCurrentPage() - 1));
	        info.setEntityList(query.list());
	        info.setResult(F3SWebServiceResult.SUCCESS);
		} catch (Exception e) {
			info.setResult(F3SWebServiceResult.FAIL);
			List msgList = new ArrayList();
			Map msgMap = new HashMap();
			msgMap.put("ERROR", e.getMessage());
			msgList.add(msgMap);
			info.setEntity(msgList);
		}
		return toXML(info, getAliasTypes());
    }

}
