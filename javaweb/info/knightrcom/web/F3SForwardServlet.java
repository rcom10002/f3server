package info.knightrcom.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Forwarding Servlet
 */
public class F3SForwardServlet extends F3SServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3962663873731941042L;

	/**
     * Constructor of the object.
     */
    public F3SForwardServlet() {
        super();
    }

    /* (non-Javadoc)
     * @see info.knightrcom.web.F3SServlet#doProcess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
//            response.getWriter();
//            if (request.getRequestURI().matches("^.*?\\.f3s$")) {
//            	// f3s服务
//                // F3SWebServiceHandler.doService(request, response);
//            	getF3SServlet(F3SApplicationServlet.class).doProcess(request, response);
//            } else {
            	// 静态文件请求
                String deletePattern = "^/.*/|\\.\\w+$";
                String forwardURI = request.getRequestURI().replaceAll(deletePattern, "");
                String realForwardURI = getServletConfig().getInitParameter(forwardURI);
                request.getRequestDispatcher("/WEB-INF/" + realForwardURI).forward(request, response);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
