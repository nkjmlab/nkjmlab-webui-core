package org.nkjmlab.webui.util.servlet;

import javax.servlet.http.HttpServletRequest;

import org.nkjmlab.util.net.UrlUtils;

public class ServletUrlUtils {

	public static String getServletUrl(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath() + request.getServletPath();
	}

	public static String getFullRequestUrl(HttpServletRequest request) {
		StringBuffer requestURL = request.getRequestURL();
		String queryString = request.getQueryString();

		if (queryString == null) {
			return requestURL.toString();
		} else {
			return requestURL.append('?').append(queryString).toString();
		}
	}

	public static String getHostUrl(HttpServletRequest request) {
		return UrlUtils.getHostUrl(getServletUrl(request));
	}
}
