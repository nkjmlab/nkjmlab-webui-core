package org.nkjmlab.webui.common.user.model;

import org.nkjmlab.util.db.DbClient;
import org.nkjmlab.util.db.RelationalModel;

public class UserAccountsTable extends RelationalModel<UserAccount> {

	public static final String TABLE_NAME = "USER_ACCOUNTS";

	public static final String ID = "id";
	public static final String CREATED = "created";
	public static final String INSTANCE_CLASS = "instance_class";
	public static final String GROUP_ID = "group_id";

	private DbClient client;

	public UserAccountsTable(DbClient client) {
		this.client = client;
	}

	@Override
	public String getRelationName() {
		return TABLE_NAME;
	}

	@Override
	public String getRelationalSchema() {
		return getRelationName() + "(" + ID + " varchar primary key, " + CREATED
				+ " TIMESTAMP AS CURRENT_TIMESTAMP NOT NULL, " + INSTANCE_CLASS
				+ " varchar, " + GROUP_ID + " varchar)";
	}

	@Override
	public DbClient getClient() {
		return client;
	}

}
