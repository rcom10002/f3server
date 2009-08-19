package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.GlobalConfig;
import info.knightrcom.data.metadata.GlobalConfigDAO;
import info.knightrcom.data.metadata.PeriodlySum;
import info.knightrcom.web.model.EntityInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;


@SuppressWarnings("unchecked")
public class DynaReportService extends F3SWebServiceAdaptor {

	@Override
	public Class<?>[] getAliasTypes() {
    	return new Class<?>[] {List.class};
    }
	
	@Override
    public ResultTransformer getResultTransformer() {
        return Transformers.ALIAS_TO_ENTITY_MAP;
    }
	
	/**
	 * 读取SQL返回结果
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
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
			
			Query query = HibernateSessionFactory.getSession().createSQLQuery(sql);
			List<Map> result = query.list();
			
	        info.getPagination().setTotalRecord(result.size());
	        info.getPagination().setCurrentPage(currentPage);
	        // 设置结果集
	        query.setMaxResults(info.getPagination().getPageSize());
	        query.setFirstResult(info.getPagination().getPageSize() * (info.getPagination().getCurrentPage() - 1));
	        query.setResultTransformer(this.getResultTransformer());
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
	
	/**
     * 报表导出
     * @param request
     * @param response
     * @return
     * @throws IOException 
     */
	public String CSV_EXPORT(HttpServletRequest request, HttpServletResponse response) throws IOException {
		EntityInfo<Map> info = new EntityInfo<Map>();
		
    	String url = request.getSession().getServletContext().getRealPath("/");
    	String filename = "DYNA_REPORT_" + new java.util.Date().getTime() + ".csv";
    	ICsvMapWriter writer = new CsvMapWriter(new FileWriter(url + filename),
				CsvPreference.EXCEL_PREFERENCE);
    	
    	String mth = request.getParameter("MTH");
		String sql = request.getParameter("SQL");
		String templateId = request.getParameter("TEMPLATE_ID");
		
		// 判断是否选择的是报表模板
		if (mth.equals("0")) {
			final GlobalConfig globalconfig = new GlobalConfigDAO().findById(templateId);
			sql = globalconfig.getValue();
		}
		
		Query query = HibernateSessionFactory.getSession().createSQLQuery(sql);
		query.setResultTransformer(getResultTransformer());
		List<Map> list = query.list();
		
    	try {
    		List headerList = new ArrayList();
    		Map mapHeader = list.get(0);
    		Iterator items = mapHeader.keySet().iterator();
    		while (items.hasNext()) {
    			headerList.add(items.next().toString());
    		}
			final String[] header = (String[]) headerList.toArray(new String[0]);
			// the actual writing
			writer.writeHeader(header);
			for (Map map : list) {
				// set up some data to write
				final HashMap<String, ? super Object> data = new HashMap<String, Object>();
				Iterator cols = map.keySet().iterator();
				// fill column value
				while (cols.hasNext()) {
					String col = cols.next().toString();
					data.put(col, map.get(col) == null ? "" : map.get(col));
				}
				writer.write(data, header);
			}
			info.setTag("http://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath() + "/"+ filename);
			info.setResult(F3SWebServiceResult.SUCCESS);
		} finally {
			writer.close();
		}
		
        return toXML(info, new Class[] {PeriodlySum.class});
    }

}
