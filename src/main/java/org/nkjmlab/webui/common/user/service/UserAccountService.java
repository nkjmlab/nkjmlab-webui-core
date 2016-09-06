package org.nkjmlab.webui.common.user.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.db.DbClient;
import org.nkjmlab.util.log4j.LogManager;
import org.nkjmlab.webui.common.user.model.UserAccount;
import org.nkjmlab.webui.common.user.model.UserAccountsTable;
import org.nkjmlab.webui.common.user.model.UserSession;

import jp.go.nict.langrid.commons.ws.ServletServiceContext;
import jp.go.nict.langrid.servicecontainer.service.AbstractService;

public class UserAccountService extends AbstractService implements UserAccountServiceInterface {

	protected static Logger log = LogManager.getLogger();

	private UserAccountsTable users;

	public UserAccountService(DbClient client) {
		users = new UserAccountsTable(client);
	}

	@Override
	public boolean login(String userId, String groupId) {
		UserSession userSession = UserSession.of(getRequest());
		users.insertIfAbsent(new UserAccount(userId, groupId));
		if (userSession.isLogined()) {
			log.debug("already logined as {}", userSession.getUserId());

			if (!userSession.getUserId().equals(userId)) {
				log.debug("userId is changed from {} to {}", userSession.getUserId(), userId);
				userSession.setUserId(userId);
			}
			if (!userSession.getGroupId().equals(groupId)) {
				log.debug("groupId is changed from {} to {}", userSession.getGroupId(), groupId);
				userSession.setGroupId(groupId);
			}
			return true;
		} else {
			log.debug("create new session for {}", userId);
			userSession.setMaxInactiveInterval(10 * 60 * 60);
			userSession.setUserId(userId);
			userSession.setGroupId(groupId);
			return true;
		}
	}

	public boolean isLogin(HttpServletRequest request) {
		return UserSession.of(request).isLogined();
	}

	protected HttpServletRequest getRequest() {
		return ((ServletServiceContext) getServiceContext()).getRequest();
	}

}
