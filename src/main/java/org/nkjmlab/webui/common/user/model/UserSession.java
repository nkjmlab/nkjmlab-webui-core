package org.nkjmlab.webui.common.user.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.log4j.ServletLogManager;

public class UserSession {

	protected static Logger log = ServletLogManager.getLogger();

	private static final String USER_ID = "userId";

	private static final String GROUP_ID = "groupId";

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

	public UserAccount getUser() {
		return new UserAccount(getUserId(), getGroupId());
	}

	public String getGroupId() {
		return getAttribute(GROUP_ID) == null ? ""
				: getAttribute(GROUP_ID).toString();
	}

	public String getUserId() {
		return getAttribute(USER_ID) == null ? ""
				: getAttribute(USER_ID).toString();
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

	public void setGroupId(String groupId) {
		session.setAttribute(GROUP_ID, groupId);
	}

}
