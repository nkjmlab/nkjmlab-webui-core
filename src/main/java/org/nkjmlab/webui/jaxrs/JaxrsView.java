package org.nkjmlab.webui.jaxrs;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.mvc.Viewable;
import org.nkjmlab.util.log4j.LogManager;
import org.nkjmlab.webui.jaxrs.thymeleaf.ThymeleafModel;
import org.nkjmlab.webui.service.user.model.UserAccount;
import org.nkjmlab.webui.util.servlet.ServletUrlUtils;
import org.nkjmlab.webui.util.servlet.UserRequest;
import org.nkjmlab.webui.util.servlet.UserSession;

/**
 * @author nkjm
 *
 */
public abstract class JaxrsView {

	protected static Logger log = LogManager.getLogger();

	@Context
	protected HttpHeaders header;

	@Context
	protected HttpServletRequest request;

	@Context
	protected HttpServletResponse response;

	@Context
	protected ServletContext servletContext;

	/** relative path from src/main/webapp.  src/main/webapp is regarded as / **/
	private String viewRootPath;

	public JaxrsView() {
		this.viewRootPath = "/jaxrs-view-root";
	}

	public abstract Viewable getView(String pathInfo, Map<String, String[]> params);

	@GET
	@Path("{path:.*}")
	@Produces(MediaType.TEXT_HTML)
	public Viewable getView() {
		return getView(request.getPathInfo(), getParameterMap());
	}

	@POST
	@Path("{path:.*}")
	@Produces(MediaType.TEXT_HTML)
	public Viewable postView() {
		return getView(request.getPathInfo(), getParameterMap());
	}

	protected Map<String, String[]> getParameterMap() {
		Map<String, String[]> parameterMap = (Map<String, String[]>) request.getParameterMap();
		return parameterMap;
	}

	public Viewable createView(String pathInfo, ThymeleafModel model) {
		return new Viewable(getViewRootPath() + pathInfo, model);
	}

	protected Viewable getDefaultView(String pathInfo, Map<String, String[]> parameterMap) {
		ThymeleafModel model = new ThymeleafModel(parameterMap);
		if (pathInfo.equals("/")) {
			pathInfo = "/index.html";
		}
		return createView(pathInfo, model);
	}

	private String getViewRootPath() {
		return viewRootPath;
	}

	protected String getCurrentUserId() {
		return getCurrentUserSession().getUserId();
	}

	protected UserSession getCurrentUserSession() {
		return UserSession.of(request);
	}

	protected boolean isRootPath(String pathInfo) {
		if (pathInfo == null || pathInfo.equals("") || pathInfo.equals("/")) {
			return true;
		}
		return false;
	}

	protected String getHostUrl() {
		return ServletUrlUtils.getHostUrl(request);
	}

	protected String getServletUrl() {
		return ServletUrlUtils.getServletUrl(request);
	}

	public String getFullRequestUrl() {
		return ServletUrlUtils.getFullRequestUrl(request);
	}

	protected Viewable redirectTo(String pathInfo) {
		return redirectTo(pathInfo, new ThymeleafModel());
	}

	protected Viewable redirectTo(String pathInfo, ThymeleafModel model) {
		try {
			response.sendRedirect(getServletUrl() + pathInfo);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
		return createView("/index.html", model);
	}

	protected boolean containsNoAuthPathElements(String[] noAuthPaths, String pathInfo) {
		for (String noAuthPath : noAuthPaths) {
			if (pathInfo.contains(noAuthPath)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isLoginedSession() {
		return UserSession.of(request).isLogined();
	}

	protected UserSession getUserSession() {
		return UserSession.of(request);
	}

	protected UserRequest getUserRequest() {
		return UserRequest.of(request);
	}

	protected ThymeleafModel createModelWithUserAccountIfExists(UserAccount ua) {
		ThymeleafModel model = new ThymeleafModel();
		if (ua == null) {
			return model;
		}
		model.put("currentUser", ua);
		model.setLocale(ua.getLocale());
		return model;
	}

}