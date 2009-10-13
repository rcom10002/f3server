package info.knightrcom.web.service;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.util.StringHelper;
import info.knightrcom.web.constant.GameConfigureConstant;
import info.knightrcom.web.model.EntityInfo;
import info.knightrcom.web.model.entity.RechargeHistoryInfo;

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

public class ReportRechargeHistoryService extends F3SWebService<RechargeHistoryInfo> {

	/* 
	 * 准备查询时要用的参数
	 * @see info.knightrcom.web.service.F3SWebService#processQuerySetting(org.hibernate.Query, javax.servlet.http.HttpServletRequest)
	 */
	public void processQuerySetting(Query query, HttpServletRequest request) {
        String fromPlayer = StringHelper.escapeSQL(request.getParameter("FROM_PLAYER")) == null ? "" : StringHelper.escapeSQL(request.getParameter("FROM_PLAYER"));
        query.setString(0, "%" + fromPlayer + "%");
        String tpPlayer = StringHelper.escapeSQL(request.getParameter("TO_PLAYER")) == null ? "" : StringHelper.escapeSQL(request.getParameter("TO_PLAYER"));
        query.setString(1, "%" + tpPlayer + "%");
        query.setString(2, request.getParameter("CREATE_MONTH"));
        query.setString(3, request.getParameter("CREATE_MONTH"));
	}
	
    @Override
    public String getNamedQuery() {
        return "REPORT_RECHARGE_HISTORY_INFO";
    }

    @Override
    public String getNamedQueryForCount() {
        return "REPORT_RECHARGE_HISTORY_INFO_COUNT";
    }

    @Override
    public ResultTransformer getResultTransformer() {
    	return Transformers.aliasToBean(RechargeHistoryInfo.class);
    }


    @Override
    public Class<?>[] getAliasTypes() {
    	return new Class<?>[] {RechargeHistoryInfo.class};
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
		EntityInfo<RechargeHistoryInfo> info = new EntityInfo<RechargeHistoryInfo>();
		
    	Query query = HibernateSessionFactory.getSession().getNamedQuery(getNamedQuery());
    	processQuerySetting(query, request);
    	query.setResultTransformer(getResultTransformer());
    	List<RechargeHistoryInfo> list = (List<RechargeHistoryInfo>)query.list();
    	String url = request.getSession().getServletContext().getRealPath("/");
    	String filename = "RECHARGE_HISTORY_" + request.getParameter("CREATE_MONTH") + ".csv";
    	ICsvMapWriter writer = new CsvMapWriter(new FileWriter(url + GameConfigureConstant.DOWNLOAD_PATH + filename),
				CsvPreference.EXCEL_PREFERENCE);
    	try {
			final String[] header = new String[] { "充值玩家", "充值前积分", "充值后积分", "充值积分", "被充值玩家", "被充值前积分", "被充值后积分", "充值时间"};
			// the actual writing
			writer.writeHeader(header);
			for (RechargeHistoryInfo rechargeRecord : list) {
				// set up some data to write
				final HashMap<String, ? super Object> data = new HashMap<String, Object>();
				data.put(header[0], rechargeRecord.getFromPlayer());
				data.put(header[1], rechargeRecord.getFromOrgScore());
				data.put(header[2], rechargeRecord.getFromCurScore());
				data.put(header[3], rechargeRecord.getScore());
				data.put(header[4], rechargeRecord.getToPlayer());
				data.put(header[5], rechargeRecord.getToOrgScore());
				data.put(header[6], rechargeRecord.getToCurScore());
				data.put(header[7], rechargeRecord.getCreateTime());
				writer.write(data, header);
			}
			info.setTag(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath() + "/" + GameConfigureConstant.DOWNLOAD_PATH + filename);
			info.setResult(F3SWebServiceResult.SUCCESS);
		} finally {
			writer.close();
		}
		
        return toXML(info, new Class[] {RechargeHistoryInfo.class});
    }
}
