package org.nkjmlab.webui.common.jaxrs;

import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.nkjmlab.util.log4j.LogManager;

/**
 * Application config class. add {@code @ApplicationPath} Annotation.
 *
 * @author nkjm
 *
 */
public class JaxrsConfig extends ResourceConfig {
	protected static Logger log = LogManager.getLogger();

	public JaxrsConfig() {
		this("model");
	}

	public JaxrsConfig(String variableName) {
		packages(getClass().getPackage().getName());
		log.info("{} is set for target to scanning.", getClass().getPackage().getName());
		ThymeleafTemplateProcessor.setVariableName(variableName);
		register(ThymeleafTemplateProcessor.class);
		log.info("The variable name of model in a view is {}", variableName);
		register(MvcFeature.class);
		log.info("{} is loaded.", getClass().getName());
	}

}
