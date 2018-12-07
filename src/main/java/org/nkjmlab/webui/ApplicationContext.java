package org.nkjmlab.webui;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.Logger;
import org.nkjmlab.util.concurrent.NamedSingleThreadFactory;
import org.nkjmlab.util.db.DbClient;
import org.nkjmlab.util.db.DbClientFactory;
import org.nkjmlab.util.db.DbConfig;
import org.nkjmlab.util.db.H2ClientWithConnectionPool;
import org.nkjmlab.util.db.H2ConfigFactory;
import org.nkjmlab.util.db.H2Server;
import org.nkjmlab.util.io.FileUtils;
import org.nkjmlab.util.lang.RuntimeUtils;
import org.nkjmlab.util.log4j.Log4jConfigurator;
import org.nkjmlab.util.log4j.LogManager;
import org.nkjmlab.util.net.UrlUtils;
import org.nkjmlab.util.slack.SlackMessage;
import org.nkjmlab.util.slack.SlackMessengerService;

public abstract class ApplicationContext implements ServletContextListener {

	protected static Logger log = LogManager.getLogger();

	private static H2ClientWithConnectionPool client;
	private static File databaseDir;
	private static URL slackWebhookUrl;
	private static ServletContext servletContext;

	protected static final ScheduledExecutorService scheduleTasksService = Executors
			.newSingleThreadScheduledExecutor(
					new NamedSingleThreadFactory("scheduled-tasks", true));

	static {
		Log4jConfigurator.setOverride(false);
		log.info("Request to start h2 server if absent.");
		H2Server.start();
		//ThymeleafTemplateProcessor.setcacheTTLMs(60 * 1000L);
	}

	public static ServletContext getServletContext() {
		return servletContext;
	}

	public static DbClient getDbClient() {
		if (client == null) {
			throw new RuntimeException("Database name should be set.");
		}
		return client;
	}

	public void contextInitialized(ServletContextEvent event, String dbName, String slackUrl) {
		servletContext = event.getServletContext();

		FileUtils.getFileInUserDirectory("h2-db").mkdirs();
		databaseDir = FileUtils
				.getFileInUserDirectory("h2-db/" + event.getServletContext().getContextPath());
		DbConfig conf = H2ConfigFactory.create(new File(databaseDir, dbName));
		log.info(conf);
		client = DbClientFactory.createH2ClientWithConnectionPool(conf);
		slackWebhookUrl = UrlUtils.of(slackUrl);
		log.info(slackUrl);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		client.dispose();
		SlackMessengerService.shutdown();
		log.info("{} is destroyed", getClass().getName());
	}

	public static URL getSlackWebhookUrl() {
		return slackWebhookUrl;
	}

	public static File getDatabaseDir() {
		return databaseDir;
	}

	public static void asyncPostException(String channel, String username, Throwable e) {
		SlackMessengerService.asyncPostException(slackWebhookUrl, channel, username, e);
	}

	public static void asyncPostMessage(String channel, String username, String text) {
		SlackMessengerService.asyncPostMessage(slackWebhookUrl,
				new SlackMessage(channel, username, text));
	}

	protected void addLoggingMemoryUsageTask(long intervalMin) {
		scheduleTasksService.scheduleWithFixedDelay(() -> {
			log.info(RuntimeUtils.getMemoryUsege());
		}, 0, intervalMin, TimeUnit.MINUTES);
	}

	protected void setDbMaxConnections(int maxConnections) {
		client.setMaxConnections(maxConnections);
	}

}
