package info.knightrcom.web.service;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;

abstract class F3SWebServiceAdaptor<T> extends F3SWebService<T> {

	@Override
	public Class<?>[] getAliasTypes() {
		return null;
	}

	@Override
	public String getNamedQuery() {
		return null;
	}

	@Override
	public String getNamedQueryForCount() {
		return null;
	}

	@Override
	public ResultTransformer getResultTransformer() {
		return null;
	}

	@Override
	public void processQuerySetting(Query query, HttpServletRequest request) {
	}

}
