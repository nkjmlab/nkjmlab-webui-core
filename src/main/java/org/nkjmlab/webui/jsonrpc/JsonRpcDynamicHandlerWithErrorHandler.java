package org.nkjmlab.webui.jsonrpc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MimeHeaders;

import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.log4j.LogManager;

import jp.go.nict.langrid.commons.beanutils.Converter;
import jp.go.nict.langrid.commons.beanutils.ConverterForJsonRpc;
import jp.go.nict.langrid.commons.lang.ClassUtil;
import jp.go.nict.langrid.commons.lang.StringUtil;
import jp.go.nict.langrid.commons.rpc.RpcHeader;
import jp.go.nict.langrid.commons.rpc.json.JsonRpcRequest;
import jp.go.nict.langrid.commons.rpc.json.JsonRpcResponse;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.commons.ws.util.MimeHeadersUtil;
import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSON;
import jp.go.nict.langrid.servicecontainer.handler.RIProcessor;
import jp.go.nict.langrid.servicecontainer.handler.ServiceFactory;
import jp.go.nict.langrid.servicecontainer.handler.ServiceLoader;
import jp.go.nict.langrid.servicecontainer.handler.jsonrpc.AbstractJsonRpcHandler;
import jp.go.nict.langrid.servicecontainer.handler.jsonrpc.JsonRpcHandler;

public abstract class JsonRpcDynamicHandlerWithErrorHandler extends AbstractJsonRpcHandler
		implements JsonRpcHandler {

	protected static Logger log = LogManager.getLogger();

	private Converter converter = new ConverterForJsonRpc();

	protected abstract void handleError(ServiceContext sc, ServiceLoader sl, String serviceName,
			JsonRpcRequest req, HttpServletResponse response, OutputStream os, JsonRpcResponse res,
			Throwable t);

	public JsonRpcDynamicHandlerWithErrorHandler() {
	}

	@Override
	public void handle(
			ServiceContext sc, ServiceLoader sl, String serviceName,
			JsonRpcRequest req, HttpServletResponse response, OutputStream os) {
		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			List<RpcHeader> resHeaders = new ArrayList<RpcHeader>();
			Class<?> clazz = null;
			Object result = null;
			if (req.getCallback() != null) {
				response.setContentType("application/javascript");
			} else {
				response.setContentType("application/json-rpc");
			}
			JsonRpcResponse res = new JsonRpcResponse();
			try {
				ServiceFactory f = sl.loadServiceFactory(cl, serviceName);
				if (f == null) {
					response.setStatus(404);
					return;
				}
				RIProcessor.start(sc);
				try {
					Collection<Class<?>> interfaceClasses = f.getInterfaces();
					Method method = null;
					int paramLength = req.getParams() == null ? 0 : req.getParams().length;
					for (Class<?> clz : interfaceClasses) {
						method = ClassUtil.findMethod(clz, req.getMethod(), paramLength);
						if (method == null)
							continue;
						clazz = clz;
						break;
					}
					if (method == null) {
						log.warn(String.format(
								"method \"%s(%s)\" not found in service \"%s\".", req.getMethod(),
								StringUtil.repeatedString("arg", paramLength, ","), serviceName));
						response.setStatus(404);
						handleError(sc, sl, serviceName, req, response, os, res,
								new IllegalArgumentException(String.format(
										"method \"%s(%s)\" not found in service \"%s\".",
										req.getMethod(),
										StringUtil.repeatedString("arg", paramLength, ","),
										serviceName)));
						return;
					}
					Object service = f.createService(cl, sc, clazz);
					// Currently only array("[]") is supported, while JsonRpc accepts Object("{}")
					Class<?>[] ptypes = method.getParameterTypes();
					Object[] params = req.getParams();
					Object[] args = new Object[ptypes.length];
					for (int i = 0; i < args.length; i++) {
						if (params[i].equals("")) {
							if (ptypes[i].isPrimitive()) {
								args[i] = ClassUtil.getDefaultValueForPrimitive(ptypes[i]);
							} else {
								args[i] = null;
							}
						} else {
							args[i] = converter.convert(params[i], ptypes[i]);
						}
					}
					result = method.invoke(service, args);
				} finally {
					MimeHeaders resMimeHeaders = new MimeHeaders();
					RIProcessor.finish(resMimeHeaders, resHeaders);
					MimeHeadersUtil.setToHttpServletResponse(resMimeHeaders, response);
				}
				res.setId(req.getId());
				res.setHeaders(resHeaders.toArray(new RpcHeader[] {}));
				res.setResult(result);
				Writer w = new OutputStreamWriter(os, "UTF-8");
				try {
					String cb = req.getCallback();
					if (cb != null) {
						cb = cb.trim();
						if (cb.length() > 0) {
							w.write(cb);
							w.write("(");
						} else {
							cb = null;
						}
					}
					w.flush();
					JSON.encode(res, w);
					if (cb != null) {
						w.write(")");
					}
				} finally {
					w.flush();
				}
			} catch (InvocationTargetException e) {
				Throwable t = e.getTargetException();
				log.error("failed to handle request for " + serviceName
						+ (clazz != null ? ":" + clazz.getName() : "") + "#" + req.getMethod(), t);
				response.setStatus(500);
				res.setError(createRpcFault(t));
				JSON.encode(res, os);
				handleError(sc, sl, serviceName, req, response, os, res, t);
			} catch (Exception e) {
				log.error("failed to handle request for " + serviceName
						+ (clazz != null ? ":" + clazz.getName() : "") + "#" + req.getMethod(), e);
				response.setStatus(500);
				res.setError(createRpcFault(e));
				JSON.encode(res, os);
				handleError(sc, sl, serviceName, req, response, os, res, e);
			}
			os.flush();
		} catch (IOException e) {
			response.setStatus(500);
			log.warn("IOException occurred.", e);
		}
	}

}
