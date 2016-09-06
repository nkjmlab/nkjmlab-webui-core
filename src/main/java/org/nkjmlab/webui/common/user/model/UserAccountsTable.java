package org.nkjmlab.webui.common.user.model;

import org.nkjmlab.util.db.DbClient;
import org.nkjmlab.util.db.Keyword;
import org.nkjmlab.util.db.RelationalModel;

public class UserAccountsTable extends RelationalModel<UserAccount> {

	public static final String TABLE_NAME = "USER_ACCOUNTS";

	public static final String ID = "id";
	public static final String CREATED = "created";
	public static final String GROUP_ID = "group_id";

	public UserAccountsTable(DbClient client) {
		super(TABLE_NAME, client);
		setAttribute(ID, Keyword.VARCHAR, Keyword.PRIMARY_KEY);
		setAttribute(CREATED, Keyword.TIMESTAMP_AS_CURRENT_TIMESTAMP);
		setAttribute(GROUP_ID, Keyword.VARCHAR);
	}

}
