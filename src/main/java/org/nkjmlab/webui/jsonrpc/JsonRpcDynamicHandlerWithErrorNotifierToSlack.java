package org.nkjmlab.webui.jsonrpc;

import java.io.OutputStream;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.nkjmlab.util.net.UrlUtils;
import org.nkjmlab.util.slack.SlackMessage;
import org.nkjmlab.util.slack.SlackMessengerService;

import jp.go.nict.langrid.commons.rpc.json.JsonRpcRequest;
import jp.go.nict.langrid.commons.rpc.json.JsonRpcResponse;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.servicecontainer.handler.ServiceLoader;

public abstract class JsonRpcDynamicHandlerWithErrorNotifierToSlack
		extends JsonRpcDynamicHandlerWithErrorHandler {

	protected String postUrl;
	protected String username = getClass().getSimpleName();
	protected String channel = "log-srv";
	protected String icon = ":ghost:";

	public JsonRpcDynamicHandlerWithErrorNotifierToSlack() {
	}

	@Override
	protected void handleError(ServiceContext sc, ServiceLoader sl, String serviceName,
			JsonRpcRequest req, HttpServletResponse response, OutputStream os, JsonRpcResponse res,
			Throwable t) {
		if (postUrl == null) {
			throw new RuntimeException("post url should be set.");
		}

		SlackMessengerService.postMessage(UrlUtils.of(postUrl), new SlackMessage(channel, username,
				":x: " + sc.getRequestUrl() + " :"
						+ (req.getMethod() != null ? req.getMethod() : "")
						+ (req.getParams() != null
								? Arrays.asList(req.getParams()) : "")
						+ " ```" + createRpcFault(t).toString() + "```",
				icon));
	}

}
