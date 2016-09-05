package org.nkjmlab.webui.common.jaxrs;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.mvc.Viewable;
import org.nkjmlab.util.log4j.ServletLogManager;

/**
 * @author nkjm
 *
 */
public class JaxrsWebUiView {

	protected static Logger log = ServletLogManager.getLogger();

	@Context
	protected HttpHeaders header;

	@Context
	protected HttpServletRequest request;

	@Context
	protected HttpServletResponse response;

	@Context
	protected ServletContext servletContext;

	public String getApplicationPath() {
		return "/views";
	}

	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	public Viewable index() {
		return getHtmlView("/", "index", getParameterMap());
	}

	/**
	 * Get a HTML file.
	 *
	 * @param pageName
	 *            the page name for get.
	 * @return the viewable object including the html file.
	 */
	@GET
	@Path("/{pageName}.html")
	@Produces(MediaType.TEXT_HTML)
	public Viewable getHtml(@PathParam("pageName") String pageName) {
		return getHtmlView("/", pageName, getParameterMap());
	}

	@GET
	@Path("/{dirs:.*?}/{pageName}.html")
	@Produces(MediaType.TEXT_HTML)
	public Viewable getHtml(@PathParam("dirs") String dirs,
			@PathParam("pageName") String pageName) {
		return getHtmlView("/" + dirs + "/", pageName, getParameterMap());
	}

	public Viewable getHtmlView(String dirs, String pageName, Map<String, String[]> params) {
		try {
			return new Viewable(getHtmlFilePath(dirs, pageName));
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	public String getDirPath(String dirs) {
		return getApplicationPath() + dirs;
	}

	public String getHtmlFilePath(String dirs, String pageName) {
		return getDirPath(dirs) + pageName + ".html";
	}

	protected Map<String, String[]> getParameterMap() {
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameterMap = (Map<String, String[]>) request.getParameterMap();
		return parameterMap;
	}

}