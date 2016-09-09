package org.nkjmlab.webui.common.jaxrs;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.mvc.Viewable;
import org.nkjmlab.util.log4j.LogManager;
import org.nkjmlab.webui.common.user.model.UserSession;

/**
 * @author nkjm
 *
 */
public class JaxrsView {

	protected static Logger log = LogManager.getLogger();

	@Context
	protected HttpHeaders header;

	@Context
	protected HttpServletRequest request;

	@Context
	protected HttpServletResponse response;

	@Context
	protected ServletContext servletContext;

	/** relative path from src/main/webapp **/
	private String viewRoot;

	public JaxrsView() {
		this.viewRoot = "jaxrs-view-root";
	}

	@GET
	@Path("/{path:.*}")
	@Produces(MediaType.TEXT_HTML)
	public Viewable getView() {
		return getView(request.getPathInfo(), getParameterMap());
	}

	public Viewable getView(String filePathFromViewRoot, Map<String, String[]> params) {
		try {
			return new Viewable(filePathFromViewRoot);
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String[]> getParameterMap() {
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameterMap = (Map<String, String[]>) request.getParameterMap();
		return parameterMap;
	}

	public String getApplicationPath() {
		return request.getServletPath();
	}

	public Viewable createView(String pathToFileFromViewRoot, ThymeleafModel model) {
		return new Viewable("/" + viewRoot + pathToFileFromViewRoot, model);
	}

	public Viewable createView(String pathToFileFromViewRoot) {
		if (pathToFileFromViewRoot.endsWith("/")) {
			pathToFileFromViewRoot += "index.html";
		}

		return new Viewable("/" + viewRoot + pathToFileFromViewRoot);
	}

	protected String getCurrentUserId() {
		return getCurrentUserSession().getUserId();
	}

	protected UserSession getCurrentUserSession() {
		return UserSession.of(request);
	}

}