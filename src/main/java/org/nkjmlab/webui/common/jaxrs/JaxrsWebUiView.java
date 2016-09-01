package org.nkjmlab.webui.common.jaxrs;

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

	public String getRelativePath() {
		return "/views/";
	}

	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	public Viewable index() {
		return getHtmlView("index");
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
	public Viewable getWebPage(@PathParam("pageName") String pageName) {
		return getHtmlView(pageName);
	}

	public Viewable getHtmlView(String pageName) {
		try {
			return new Viewable(getRelativePath() + pageName + ".html");
		} catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}

	}

}