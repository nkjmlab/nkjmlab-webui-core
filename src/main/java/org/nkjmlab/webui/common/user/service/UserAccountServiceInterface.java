package org.nkjmlab.webui.common.user.service;

import org.nkjmlab.webui.common.user.model.UserAccount;

import jp.go.nict.langrid.commons.rpc.intf.Parameter;

public interface UserAccountServiceInterface {

	boolean login(
			@Parameter(sample = "nkjm") String userId,
			@Parameter(sample = "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3") String password);

	boolean logout();

	boolean register(UserAccount account);

	boolean merge(UserAccount account);

	boolean update(UserAccount account);

	boolean updatePassword(String userId, String password, String newPassword);

}
