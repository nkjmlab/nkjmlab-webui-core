package org.nkjmlab.webui.common.user.service.demo;

import org.nkjmlab.webui.common.user.service.UserAccountService;

import jp.go.nict.langrid.servicecontainer.handler.annotation.Service;
import jp.go.nict.langrid.servicecontainer.handler.annotation.Services;

// Example
//import javax.servlet.annotation.WebInitParam;
//import javax.servlet.annotation.WebServlet;
// @WebServlet(urlPatterns = "/UserAccountService/*", initParams = {
//		@WebInitParam(name = "dumpRequests", value = "false"),
//		@WebInitParam(name = "additionalResponseHeaders", value = "Access-Control-Allow-Origin: *") })

@Services({ @Service(name = "UserAccountService", impl = UserAccountService.class) })
public class UserAccountServiceServlet extends
		jp.go.nict.langrid.servicecontainer.handler.jsonrpc.servlet.JsonRpcServlet {

}
