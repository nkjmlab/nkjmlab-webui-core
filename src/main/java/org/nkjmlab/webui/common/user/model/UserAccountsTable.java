package org.nkjmlab.webui.common.user.model;

import org.nkjmlab.util.db.DbClient;
import org.nkjmlab.util.db.Keyword;
import org.nkjmlab.util.db.RelationalModel;

public class UserAccountsTable extends RelationalModel<UserAccount> {

	public static final String CREATED_AT = "created_at";
	public static final String MODIFIED_AT = "modified_at";
	public static final String USER_ID = "user_id";
	public static final String GROUP_ID = "group_id";
	public static final String PASSWORD = "password";
	public static final String NICKNAME = "nickname";
	public static final String ROLE = "role";
	public static final String MAIL = "mail";
	public static final String LANGUAGE = "language";
	private static final String FIRST_NAME = "first_name";
	private static final String LAST_NAME = "last_name";
	public static final String OPTIONS = "options";

	public UserAccountsTable(DbClient client) {
		super(new UserAccount().getTableName(), client);
		addColumnDefinition(USER_ID, Keyword.VARCHAR);
		addColumnDefinition(GROUP_ID, Keyword.VARCHAR);
		addColumnDefinition(NICKNAME, Keyword.VARCHAR);
		addColumnDefinition(FIRST_NAME, Keyword.VARCHAR);
		addColumnDefinition(LAST_NAME, Keyword.VARCHAR);
		addColumnDefinition(CREATED_AT, Keyword.TIMESTAMP);
		addColumnDefinition(MODIFIED_AT, Keyword.TIMESTAMP);
		addColumnDefinition(PASSWORD, Keyword.VARCHAR);
		addColumnDefinition(ROLE, Keyword.VARCHAR);
		addColumnDefinition(MAIL, Keyword.VARCHAR);
		addColumnDefinition(LANGUAGE, Keyword.VARCHAR);
		addColumnDefinition(OPTIONS, Keyword.VARCHAR);
		setPrimaryKeyConstraint(USER_ID, GROUP_ID);
	}

	public boolean validate(String userId, String password) {
		UserAccount u = readByPrimaryKey(userId);
		return u.validate(password);
	}

	public UserAccount findByUserIdAndGroupId(String userId, String groupId) {
		return readBy(USER_ID, userId, GROUP_ID, groupId);
	}

	public void register(UserAccount userAccount) {
		String sha256HashedButWithoutSaltPassword = userAccount.getPassword();
		userAccount.setPasswordWithoutSalt(sha256HashedButWithoutSaltPassword);
		if (exists(userAccount)) {
			throw new RuntimeException(userAccount.getUserId() + " is already registered:");
		}
		insert(userAccount);
	}

	public boolean validateWithSaltedPassword(String userId, String groupId, String password) {
		return findByUserIdAndGroupId(userId, groupId).getPassword().equals(password);
	}

	public UserAccount findByUserSession(UserSession userSession) {
		return readByPrimaryKey(userSession.getUserId());
	}

}
