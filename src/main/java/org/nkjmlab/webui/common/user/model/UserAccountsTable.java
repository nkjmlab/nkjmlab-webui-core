package org.nkjmlab.webui.common.user.model;

import org.nkjmlab.util.db.DbClient;
import org.nkjmlab.util.db.Keyword;
import org.nkjmlab.util.db.RelationalModel;

public class UserAccountsTable extends RelationalModel<UserAccount> {

	public static final String CREATED = "created";
	public static final String MODIFIED = "modified";
	public static final String GROUP_ID = "group_id";
	public static final String USER_ID = "user_id";
	public static final String PASSWORD = "password";
	public static final String NAME = "name";
	public static final String ROLE = "role";
	public static final String MAIL_ADDRESS = "mailAddress";
	public static final String LANGUAGE = "language";
	public static final String OPTIONS = "options";

	public UserAccountsTable(DbClient client) {
		super(new UserAccount().getTableName(), client);
		addColumnDefinition(USER_ID, Keyword.VARCHAR);
		addColumnDefinition(GROUP_ID, Keyword.VARCHAR);
		addColumnDefinition(NAME, Keyword.VARCHAR);
		addColumnDefinition(CREATED, Keyword.TIMESTAMP);
		addColumnDefinition(MODIFIED, Keyword.TIMESTAMP);
		addColumnDefinition(PASSWORD, Keyword.VARCHAR);
		addColumnDefinition(ROLE, Keyword.VARCHAR);
		addColumnDefinition(MAIL_ADDRESS, Keyword.VARCHAR);
		addColumnDefinition(LANGUAGE, Keyword.VARCHAR);
		addColumnDefinition(OPTIONS, Keyword.VARCHAR);
		setPrimaryKeyConstraint(USER_ID, GROUP_ID);
	}

	public boolean validate(String userId, String groupId, String password) {
		UserAccount u = findByUserIdAndGroupId(userId, groupId);
		return u.validate(password);
	}

	public UserAccount findByUserIdAndGroupId(String userId, String groupId) {
		return readBy(USER_ID, userId, GROUP_ID, groupId);
	}

	public void register(UserAccount userAccount) {
		String sha1HashedButWithoutSaltPassword = userAccount.getPassword();
		userAccount.setPasswordWithoutSalt(sha1HashedButWithoutSaltPassword);
		insert(userAccount);
	}

}
