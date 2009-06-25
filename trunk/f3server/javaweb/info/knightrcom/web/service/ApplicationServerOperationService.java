package info.knightrcom.web.service;

import info.knightrcom.F3Server;
import info.knightrcom.util.ExecutePlan;
import info.knightrcom.web.model.EntityInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;

public class ApplicationServerOperationService extends F3SWebService<Object> {

	@Override
	public Class<?>[] getAliasTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamedQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNamedQueryForCount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultTransformer getResultTransformer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processQuerySetting(Query query, HttpServletRequest request) {
		// TODO Auto-generated method stub
		
	}

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
				F3Server.startServer(null);
				return null;
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
				F3Server.shutdownServer();
				return null;
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
				F3Server.shutdownServer();
				F3Server.startServer(null);
				return null;
			}
		};
        return toXML((EntityInfo<Object>)executePlan.execute());
	}

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
