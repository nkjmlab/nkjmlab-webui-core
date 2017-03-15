package org.nkjmlab.webui.util.langrid;

import javax.servlet.http.HttpServletRequest;

import org.nkjmlab.webui.util.servlet.UserSession;

import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.commons.ws.ServletServiceContext;

public class ServiceContextUtils {

	public static UserSession getUserSession(ServiceContext context) {
		return UserSession.of(((ServletServiceContext) context).getRequest());
	}

	public static HttpServletRequest getHttpServletRequest(ServiceContext context) {
		return ((ServletServiceContext) context).getRequest();
	}
}
