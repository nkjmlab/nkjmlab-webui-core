package org.nkjmlab.webui.service.user.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import org.nkjmlab.util.base64.Base64FileUtils;
import org.nkjmlab.util.db.DbClient;
import org.nkjmlab.webui.jsonrpc.JsonRpcService;
import org.nkjmlab.webui.service.user.model.UserAccount;
import org.nkjmlab.webui.service.user.model.UserAccountsTable;
import org.nkjmlab.webui.util.servlet.UserSession;

public class UserAccountService extends JsonRpcService implements UserAccountServiceInterface {

	private UserAccountsTable userAccountsTable;

	public UserAccountService() {
	}

	public UserAccountService(DbClient client) {
		userAccountsTable = new UserAccountsTable(client);
	}

	@Override
	public boolean signup(UserAccount account) {
		register(account);
		login(account.getUserId(), account.getEncryptedInputPassword());
		return true;
	}

	@Override
	public boolean register(UserAccount account) {
		account.setCreatedAt(new Timestamp(new Date().getTime()));
		account.setModifiedAt(account.getCreatedAt());
		userAccountsTable.register(account);
		return false;
	}

	@Override
	public boolean update(UserAccount account) {
		if (!getUserSession().isLogined()) {
			return false;
		}
		if (!getCurrentUserAccount().isSameUserAccount(account)) {
			return false;
		}
		account.setIfAbsent(getCurrentUserAccount());
		userAccountsTable.update(account);
		return true;
	}

	@Override
	public boolean delete(String userId) {
		UserAccount u = new UserAccount();
		u.setUserId(userId);
		userAccountsTable.delete(u);
		return true;
	}

	public UserAccount getCurrentUserAccount() {
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
			throw new RuntimeException(userId + " is not registered.");
		}
		if (!userAccountsTable.validate(userId, password)) {
			throw new RuntimeException("Password is not correct.");
		}
		userSession.setMaxInactiveInterval(10 * 60 * 60);
		userSession.setUserId(userId);
		log.info("{} is logined. login session id={}", userSession.getId());
		return true;
	}

	@Override
	public boolean logout() {
		getUserSession().logout();
		return true;
	}

	@Override
	public boolean updatePasswordByAdmin(String userId, String newPassword) {
		if (getCurrentUserAccount().isAdmin()) {
			UserAccount ua = userAccountsTable.readByPrimaryKey(userId);
			ua.setEncryptedInputPassword(newPassword);
			userAccountsTable.update(ua);
		}
		return false;
	}

	@Override
	public boolean updatePassword(String userId, String oldPassword, String newPassword) {
		if (userAccountsTable.validate(userId, oldPassword)) {
			UserAccount ua = userAccountsTable.readByPrimaryKey(userId);
			ua.setEncryptedInputPassword(newPassword);
			userAccountsTable.update(ua);
		}
		return false;
	}

	@Override
	public boolean exists(String userId) {
		UserAccount ua = new UserAccount();
		ua.setUserId(userId);
		return userAccountsTable.exists(ua);
	}

	@Override
	public boolean uploadUsersCsv(String base64EncodedFile) {
		log.debug(base64EncodedFile);
		File outputFile = Base64FileUtils.decodeAndWriteToFileInTempDir(
				"users-" + System.currentTimeMillis() + ".csv", base64EncodedFile);
		log.info("Output file is {}.", outputFile);
		return true;
	}

}
