package org.nkjmlab.webui.util.tomcat;

import java.io.File;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.log4j.LogManager;

public class TomcatServer {
	protected static Logger log = LogManager.getLogger();

	private Tomcat tomcat;
	private int port = 8080;

	public TomcatServer(int port) {
		this.port = port;
		tomcat = new Tomcat();
		tomcat.setPort(port);
	}

	public void addWebapp(String contextPath, File docBase) {
		try {
			tomcat.addWebapp(contextPath, docBase.getAbsolutePath());
		} catch (ServletException e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	public void addServletAndMappping(String contextPath, File docBase, String servletName,
			Servlet servlet, String mapping) {
		Tomcat.addServlet(tomcat.addContext(contextPath, docBase.getAbsolutePath()), servletName,
				servlet).addMapping(mapping);
	}

	public void startAndAwait() {
		try {
			tomcat.start();
		} catch (LifecycleException e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
		tomcat.getServer().await();
	}

	public int getPort() {
		return port;
	}

}
