package org.nkjmlab.webui.common.user.service;

import java.sql.Timestamp;
import java.util.Date;

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
	public boolean register(UserAccount account) {
		String inputPassword = account.getPassword();
		account.setCreatedAt(new Timestamp(new Date().getTime()));
		account.setModifiedAt(account.getCreatedAt());
		userAccountsTable.register(account);
		login(account.getUserId(), inputPassword);
		return true;
	}

	@Override
	public boolean update(UserAccount account) {
		if (!getUserSession().isLogined()) {
			return false;
		}
		if (!getCurrentUserAccount().isSameUserAccount(account)) {
			return false;
		}
		account.mergeWith(getCurrentUserAccount());
		userAccountsTable.update(account);
		return true;
	}

	private UserAccount getCurrentUserAccount() {
		return userAccountsTable.findByUserSession(getUserSession());
	}

	@Override
	public boolean merge(UserAccount account) {
		userAccountsTable.merge(account);
		return true;
	}

	@Override
	public boolean login(String userId, String password) {
		UserSession userSession = getUserSession();
		UserAccount userAccount = userAccountsTable.readByPrimaryKey(userId);
		if (userAccount == null) {
			throw new RuntimeException(userId + " is not registered:");
		}
		if (!userAccountsTable.validate(userId, password)) {
			throw new RuntimeException("Password is not correct:");
		}
		userSession.setMaxInactiveInterval(10 * 60 * 60);
		userSession.setUserId(userId);
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

	@Override
	public boolean updatePassword(String userId, String password, String newPassword) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean exists(String userId) {
		UserAccount ua = new UserAccount();
		ua.setUserId(userId);
		return userAccountsTable.exists(ua);
	}

}
