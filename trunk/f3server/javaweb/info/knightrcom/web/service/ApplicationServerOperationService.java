package info.knightrcom.web.service;

import info.knightrcom.F3Server;
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
	 */
	public void START_APPLICATION_SERVER(HttpServletRequest request, HttpServletResponse response) throws Exception {
		F3Server.startServer(null);
        EntityInfo<Object> info = new EntityInfo<Object>();
    	info.setResult(F3SWebServiceResult.SUCCESS);
        toXML(info);
	}

	/**
	 * 停止游戏应用服务器
	 */
	public void STOP_APPLICATION_SERVER(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		F3Server.shutdownServer();
        EntityInfo<Object> info = new EntityInfo<Object>();
    	info.setResult(F3SWebServiceResult.SUCCESS);
        toXML(info);
	}

	/**
	 * 重启游戏应用服务器
	 */
	public void RESTART_APPLICATION_SERVER(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		F3Server.shutdownServer();
		F3Server.startServer(null);
        EntityInfo<Object> info = new EntityInfo<Object>();
    	info.setResult(F3SWebServiceResult.SUCCESS);
        toXML(info);
	}

}
