package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.PeriodlySum;
import info.knightrcom.data.metadata.PlayerScore;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.model.EntityInfo;
import info.knightrcom.web.model.entity.ReportBusinessInfo;

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

public class ReportBusinessService extends F3SWebService<PlayerScore> {

    public void processQuerySetting(Query query, HttpServletRequest request) {

        query.setTimestamp(0, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(1, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        String userId = StringHelper.escapeSQL(request.getParameter("USER_ID")) == null ? "" : StringHelper.escapeSQL(request.getParameter("USER_ID"));
        query.setString(2, "%" + userId + "%");
        query.setTimestamp(3, StringHelper.toTimeStamp(request.getParameter("FROM_DATE"), "yyyyMMdd"));
        query.setTimestamp(4, StringHelper.toTimeStamp(request.getParameter("TO_DATE"), "yyyyMMdd"));
        query.setInteger(5, new Integer(request.getParameter("SHOW_CONDITION")));
        query.setInteger(6, new Integer(request.getParameter("SHOW_CONDITION")));
        query.setString(7, StringHelper.escapeSQL(request.getParameter("USER_ID")));
        query.setString(8, userId + "%");
    }

    public String getNamedQuery() {
        return "BUSINESS_INFO";
    }

    public String getNamedQueryForCount() {
        return "BUSINESS_INFO_COUNT";
    }

    public ResultTransformer getResultTransformer() {
        return Transformers.aliasToBean(ReportBusinessInfo.class);
    }

    @Override
    public Class<?>[] getAliasTypes() {
        return new Class<?>[] {PlayerScore.class, ReportBusinessInfo.class};
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
		EntityInfo<PlayerScore> info = new EntityInfo<PlayerScore>();
		
    	Query query = HibernateSessionFactory.getSession().getNamedQuery(getNamedQuery());
    	processQuerySetting(query, request);
    	query.setResultTransformer(getResultTransformer());
    	List<ReportBusinessInfo> list = (List<ReportBusinessInfo>)query.list();
    	String url = request.getSession().getServletContext().getRealPath("/");
    	String filename = "BUSINESS_SUM_" + new java.util.Date().getTime() + ".csv";
    	ICsvMapWriter writer = new CsvMapWriter(new FileWriter(url + filename),
				CsvPreference.EXCEL_PREFERENCE);
    	try {
			final String[] header = new String[] { "玩家ID", "游戏ID", "游戏类别", "游戏时间", "状态", "得分", "时间开始", "结束开始"};
			// the actual writing
			writer.writeHeader(header);
			for (ReportBusinessInfo business : list) {
				// set up some data to write
				final HashMap<String, ? super Object> data = new HashMap<String, Object>();
				data.put(header[0], business.getUserId());
				data.put(header[1], business.getGameId());
				data.put(header[2], business.getGameType());
				data.put(header[3], business.getCreateTime());
				data.put(header[4], business.getWinandlose());
				data.put(header[5], business.getScore());
				data.put(header[6], business.getStartTime());
				data.put(header[7], business.getEndTime());
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
