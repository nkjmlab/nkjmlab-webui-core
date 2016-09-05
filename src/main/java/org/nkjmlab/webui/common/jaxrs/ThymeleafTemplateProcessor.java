package org.nkjmlab.webui.common.jaxrs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.mvc.Viewable;
import org.glassfish.jersey.server.mvc.spi.AbstractTemplateProcessor;
import org.nkjmlab.util.log4j.ServletLogManager;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * Default variable name is "ctx".
 * @author nkjm
 *
 */
@Provider
public class ThymeleafTemplateProcessor
		extends AbstractTemplateProcessor<String> implements Serializable {

	private static String variableName = "ctx";

	protected static Logger log = ServletLogManager.getLogger();

	@Context
	private HttpServletRequest request;

	@Context
	private HttpServletResponse response;

	@Context
	private ServletContext servletContext;

	private TemplateEngine templateEngine;

	@Inject
	public ThymeleafTemplateProcessor(Configuration config, ServletContext servletContext) {
		super(config, servletContext, "html", "html");
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(
				servletContext);
		templateResolver.setPrefix("/");
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setCacheTTLMs(0L);

		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
	}

	@Override
	protected String resolve(String templatePath, Reader reader)
			throws Exception {
		return templatePath;
	}

	@Override
	public void writeTo(String templateReference, Viewable viewable,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream out) throws IOException {
		WebContext context = new WebContext(request, response, servletContext);
		context.setVariable(getVariableName(), viewable.getModel());
		Writer writer = new OutputStreamWriter(out);
		templateEngine.process(templateReference, context, writer);
		writer.flush();
	}

	private static String getVariableName() {
		return variableName;
	}

	public static void setVariableName(String variableName2) {
		variableName = variableName2;
	}

}