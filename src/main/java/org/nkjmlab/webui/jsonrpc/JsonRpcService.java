package org.nkjmlab.webui.jsonrpc;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.json.JsonUtils;
import org.nkjmlab.util.log4j.LogManager;
import org.nkjmlab.util.slack.SlackMessageBuilder;
import org.nkjmlab.util.time.DateTimeUtils;
import org.nkjmlab.webui.ApplicationContext;
import org.nkjmlab.webui.jsonrpc.model.JsStackTraceJson;
import org.nkjmlab.webui.util.langrid.ServiceContextUtils;
import org.nkjmlab.webui.util.servlet.UserRequest;
import org.nkjmlab.webui.util.servlet.UserSession;

import jp.go.nict.langrid.servicecontainer.service.AbstractService;

public class JsonRpcService extends AbstractService {
	protected static Logger log = LogManager.getLogger();

	public UserSession getUserSession() {
		return UserSession.of(ServiceContextUtils.getHttpServletRequest(getServiceContext()));
	}

	public UserRequest getUserRequest() {
		return UserRequest.of(ServiceContextUtils.getHttpServletRequest(getServiceContext()));
	}

	public HttpServletRequest getHttpServletRequest() {
		return ServiceContextUtils.getHttpServletRequest(getServiceContext());
	}

	protected URL getRequestUrl() {
		return getServiceContext().getRequestUrl();
	}

	protected String getRealPath(String path) {
		return getServiceContext().getRealPath(path);
	}

	public boolean sendLog(String logLevel, String location, String msg, String options) {
		String mark = SlackMessageBuilder.getMark(logLevel);
		Map<String, Object> msgObj = JsonUtils.decode(msg);
		JsStackTraceJson st = JsonUtils.decode(location, JsStackTraceJson.class);
		String loc = st.getFunctionName() + " (" + st.getFileName() + " :" + st.getLineNumber()
				+ ":" + st.getColumnNumber() + ")";

		ApplicationContext.asyncPostMessage("log-srv", getServiceName(), ":iphone:  ["
				+ DateTimeUtils.toTimestamp(LocalDateTime.now()) + " " + mark + " \n"
				+ loc + " " + SlackMessageBuilder
						.wrapPre(st.toString() + "\n" + JsonUtils.encode(msgObj, true)));
		return false;
	}

}
