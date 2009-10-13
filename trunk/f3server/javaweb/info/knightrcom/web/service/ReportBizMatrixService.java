package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.constant.GameConfigureConstant;
import info.knightrcom.web.model.EntityInfo;
import info.knightrcom.web.model.entity.BizMatrixInfo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

public class ReportBizMatrixService extends F3SWebService<BizMatrixInfo> {

	/* 
	 * 准备查询时要用的参数
	 * @see info.knightrcom.web.service.F3SWebService#processQuerySetting(org.hibernate.Query, javax.servlet.http.HttpServletRequest)
	 */
	public void processQuerySetting(Query query, HttpServletRequest request) {
		query.setTimestamp(0, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(1, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
		query.setTimestamp(2, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(3, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
	}
	
    @Override
    public String getNamedQuery() {
        return "REPORT_BIZ_MATRIX_INFO";
    }

    @Override
    public String getNamedQueryForCount() {
        return "REPORT_BIZ_MATRIX_INFO_COUNT";
    }

    @Override
    public ResultTransformer getResultTransformer() {
    	return Transformers.aliasToBean(BizMatrixInfo.class);
    }


    @Override
    public Class<?>[] getAliasTypes() {
    	return new Class<?>[] {BizMatrixInfo.class};
    }
    
    /**
     * 报表导出
     * @param request
     * @param response
     * @return
     * @throws IOException 
     */
	@SuppressWarnings("unchecked")
	public String CSV_EXPORT(HttpServletRequest request, HttpServletResponse response) throws IOException {
		EntityInfo<BizMatrixInfo> info = new EntityInfo<BizMatrixInfo>();
		
    	Query query = HibernateSessionFactory.getSession().getNamedQuery(getNamedQuery());
    	processQuerySetting(query, request);
    	query.setResultTransformer(getResultTransformer());
    	List<BizMatrixInfo> list = (List<BizMatrixInfo>)query.list();
    	String url = request.getSession().getServletContext().getRealPath("/");
    	String filename = "BIZ_MATRIX_" + request.getParameter("FROM_DATE") + "-" + request.getParameter("TO_DATE") + ".csv";
    	ICsvMapWriter writer = new CsvMapWriter(new FileWriter(url + GameConfigureConstant.DOWNLOAD_PATH + filename),
				CsvPreference.EXCEL_PREFERENCE);
    	try {
			final String[] header = new String[] { "被充值玩家", "累计充值积分", "累计游戏积分", "累计系统积分", "有效剩余积分"};
			// the actual writing
			writer.writeHeader(header);
			for (BizMatrixInfo bizMatrixInfo : list) {
				// set up some data to write
				final HashMap<String, ? super Object> data = new HashMap<String, Object>();
				data.put(header[0], bizMatrixInfo.getUserId());
				data.put(header[1], bizMatrixInfo.getTotalRechargeScore());
				data.put(header[2], bizMatrixInfo.getTotalGameScore());
				data.put(header[3], bizMatrixInfo.getTotalSysScore());
				data.put(header[4], bizMatrixInfo.getCurrentScore());
				writer.write(data, header);
			}
			info.setTag(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath() + "/" + GameConfigureConstant.DOWNLOAD_PATH + filename);
			info.setResult(F3SWebServiceResult.SUCCESS);
		} finally {
			writer.close();
		}
		
        return toXML(info, new Class[] {BizMatrixInfo.class});
    }
}
