package info.knightrcom.web.service;

import java.util.UUID;

import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.LogInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorReportService extends F3SWebServiceAdaptor<Object> {

	/**
	 * @param request
	 * @param response
	 */
	public void ADD_ERROR_INFORMATION(HttpServletRequest request, HttpServletResponse response) {
		LogInfo logInfo = new LogInfo();
		logInfo.setLogId(UUID.randomUUID().toString());
		logInfo.setInfo(request.getParameter("CLIENT_ERROR"));
		HibernateSessionFactory.getSession().save(logInfo);
	}
}
