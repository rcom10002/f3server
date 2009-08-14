package info.knightrcom.web.service;

import info.knightrcom.F3ServerProxy;
import info.knightrcom.F3ServerProxy.LogType;
import info.knightrcom.data.HibernateSessionFactory;
import info.knightrcom.data.metadata.LogInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorReportService extends F3SWebServiceAdaptor<Object> {

    /**
     * @param request
     * @param response
     */
    public void UPLOAD_ERROR_INFORMATION(HttpServletRequest request, HttpServletResponse response) {
        LogInfo logInfo = F3ServerProxy.createLogInfo(
                request.getParameter("NAME"), 
                request.getParameter("MESSAGE"), 
                request.getParameter("STACK_TRACE"), 
                LogType.CLIENT_ERROR);
        HibernateSessionFactory.getSession().save(logInfo);
    }
}
