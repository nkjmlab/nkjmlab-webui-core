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

	private UserAccountsTable userAccountsTable;

	public UserAccountService(DbClient client) {
		userAccountsTable = new UserAccountsTable(client);
	}

	@Override
	public void register(UserAccount account) {
		userAccountsTable.register(account);
	}

	@Override
	public void update(UserAccount account) {
		userAccountsTable.update(account);
	}

	@Override
	public void merge(UserAccount account) {
		userAccountsTable.merge(account);
	}

	@Override
	public boolean login(String userId, String groupId, String password) {
		UserSession userSession = getUserSession();
		userAccountsTable.findByUserIdAndGroupId(userId, groupId);
		if (!userAccountsTable.validate(userId, groupId, password)) {
			return false;
		}
		userSession.setMaxInactiveInterval(10 * 60 * 60);
		userSession.setUserId(userId);
		userSession.setGroupId(groupId);
		return true;
	}

	private UserSession getUserSession() {
		return UserSession.of(getRequest());
	}

	public boolean isLogin(HttpServletRequest request) {
		return UserSession.of(request).isLogined();
	}

	protected HttpServletRequest getRequest() {
		return ((ServletServiceContext) getServiceContext()).getRequest();
	}

	@Override
	public boolean logout() {
		getUserSession().logout();
		return true;
	}

}
