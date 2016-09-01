package org.nkjmlab.webui.common.jaxrs;

import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.nkjmlab.util.log4j.ServletLogManager;

/**
 * Application config class. add {@code @ApplicationPath} Annotation.
 *
 * @author nkjm
 *
 */
public class JaxrsConfig extends ResourceConfig {
	protected static Logger log = ServletLogManager.getLogger();

	public JaxrsConfig() {
		packages(this.getClass().getPackage().getName());
		register(ThymeleafTemplateProcessor.class);
		register(MvcFeature.class);
		log.info("{} is loaded.", getClass().getName());
	}

}
