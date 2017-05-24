package org.nkjmlab.webui.jsonrpc;

import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.nkjmlab.util.slack.SlackMessage;
import org.nkjmlab.util.slack.SlackMessageBuilder;
import org.nkjmlab.util.slack.SlackMessengerService;
import org.nkjmlab.webui.ApplicationContext;

import jp.go.nict.langrid.commons.rpc.RpcFaultUtil;
import jp.go.nict.langrid.commons.rpc.json.JsonRpcRequest;
import jp.go.nict.langrid.commons.rpc.json.JsonRpcResponse;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.servicecontainer.handler.ServiceLoader;

public class JsonRpcDynamicHandlerWithErrorNotifierToSlack
		extends JsonRpcDynamicHandlerWithErrorHandler {

	protected URL postUrl;
	protected String username;
	protected String channel = "log-srv";
	protected String icon = ":ghost:";

	public JsonRpcDynamicHandlerWithErrorNotifierToSlack() {
	}

	@Override
	protected void handleError(ServiceContext sc, ServiceLoader sl, String serviceName,
			JsonRpcRequest req, HttpServletResponse response, OutputStream os, JsonRpcResponse res,
			Throwable t) {
		try {
			if (postUrl == null) {
				postUrl = ApplicationContext.getSlackWebhookUrl();
			}

			if (postUrl == null) {
				throw new RuntimeException("post url should be set.");
			}

			SlackMessengerService.asyncPostMessage(postUrl,
					new SlackMessage(channel, (username != null ? username : serviceName),
							createJsonRpcFaultMessage(sc, req, t), "", icon));
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	protected static String createJsonRpcFaultMessage(ServiceContext sc, JsonRpcRequest req,
			Throwable t) {
		return SlackMessageBuilder.AT_CHANNEL + " " + SlackMessageBuilder.ICON_ERROR + " "
				+ sc.getRequestUrl() + " :"
				+ (req.getMethod() != null ? req.getMethod() : "")
				+ (req.getParams() != null ? Arrays.asList(req.getParams()) : "")
				+ " " + SlackMessageBuilder.wrapPre(
						RpcFaultUtil.throwableToRpcFault("Server.userException", t).toString());
	}

}
