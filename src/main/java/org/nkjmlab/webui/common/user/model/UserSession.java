package org.nkjmlab.webui.common.user.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.log4j.LogManager;

public class UserSession {

	protected static Logger log = LogManager.getLogger();

	private static final String USER_ID = "userId";

	private HttpSession session;

	private UserSession(HttpServletRequest request) {
		this.session = request.getSession(true);
	}

	public static UserSession of(HttpServletRequest request) {
		return new UserSession(request);
	}

	public HttpSession getSession() {
		return session;
	}

	public boolean isLogined() {
		if (getUserId().length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean logout() {
		setUserId("");
		return true;
	}

	public String getUserId() {
		return getAttribute(USER_ID) == null ? "" : getAttribute(USER_ID).toString();
	}

	public String getId() {
		return session.getId();
	}

	public Object getAttribute(String key) {
		return session.getAttribute(key);
	}

	public void invalidate() {
		session.invalidate();
	}

	public void setAttribute(String key, Object value) {
		session.setAttribute(key, value);
	}

	public void setMaxInactiveInterval(int maxInterval) {
		session.setMaxInactiveInterval(maxInterval);
	}

	public void setUserId(String userId) {
		session.setAttribute(USER_ID, userId);
	}

}
