package info.knightrcom.web.service;

import info.knightrcom.F3ServerProxy;
import info.knightrcom.util.ExecutePlan;
import info.knightrcom.web.model.EntityInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApplicationServerOperationService extends F3SWebServiceAdaptor<Object> {

	/**
	 * 启动游戏应用服务器
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String START_APPLICATION_SERVER(HttpServletRequest request, HttpServletResponse response) throws Exception {
		OperateExecutePlan executePlan = new OperateExecutePlan() {
			@Override
			public Object tryPart() throws Exception {
				if (F3ServerProxy.isServerRunning()) {
					return newEntityInfo(F3SWebServiceResult.UPDATE_WARNING);
				}
				F3ServerProxy.startServer();
				return newEntityInfo(F3SWebServiceResult.UPDATE_SUCCESS);
			}
		};
        return toXML((EntityInfo<Object>)executePlan.execute());
	}

	/**
	 * 停止游戏应用服务器
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String STOP_APPLICATION_SERVER(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		OperateExecutePlan executePlan = new OperateExecutePlan() {
			@Override
			public Object tryPart() throws Exception {
				if (!F3ServerProxy.isServerRunning()) {
					return newEntityInfo(F3SWebServiceResult.UPDATE_WARNING);
				}
				F3ServerProxy.stopServer();
				return newEntityInfo(F3SWebServiceResult.UPDATE_SUCCESS);
			}
		};
        return toXML((EntityInfo<Object>)executePlan.execute());
	}

	/**
	 * 重启游戏应用服务器
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String RESTART_APPLICATION_SERVER(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		OperateExecutePlan executePlan = new OperateExecutePlan() {
			@Override
			public Object tryPart() throws Exception {
				F3ServerProxy.stopServer();
				F3ServerProxy.startServer();
				return newEntityInfo(F3SWebServiceResult.UPDATE_SUCCESS);
			}
		};
        return toXML((EntityInfo<Object>)executePlan.execute());
	}

	/**
	 *
	 */
	private static abstract class OperateExecutePlan extends ExecutePlan {
		@Override
		public Object beforeTryPart() {
	        EntityInfo<Object> info = new EntityInfo<Object>();
	    	info.setResult(F3SWebServiceResult.SUCCESS);
	    	setParams("RESULT", info);
	    	return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object exceptionPart() {
	        EntityInfo<Object> info = (EntityInfo<Object>)getParams("RESULT");
	    	info.setResult(F3SWebServiceResult.FAIL);
	    	return info;
		}
	}
}
