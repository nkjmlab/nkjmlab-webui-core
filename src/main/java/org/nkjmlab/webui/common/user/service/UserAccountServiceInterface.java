package org.nkjmlab.webui.common.user.service;

import jp.go.nict.langrid.commons.rpc.intf.Parameter;

public interface UserAccountServiceInterface {

	boolean login(
			@Parameter(sample = "nkjm") String userId,
			@Parameter(sample = "nkjmlab") String groupId);

}
