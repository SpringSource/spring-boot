/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.logging.logback;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.jul.LevelChangePropagator;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.Slf4JLoggingSystem;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.Status;

/**
 * {@link LoggingSystem} for <a href="http://logback.qos.ch">logback</a>.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 */
public class LogbackLoggingSystem extends Slf4JLoggingSystem {

	private static final Map<LogLevel, Level> LEVELS;
	static {
		Map<LogLevel, Level> levels = new HashMap<LogLevel, Level>();
		levels.put(LogLevel.TRACE, Level.TRACE);
		levels.put(LogLevel.DEBUG, Level.DEBUG);
		levels.put(LogLevel.INFO, Level.INFO);
		levels.put(LogLevel.WARN, Level.WARN);
		levels.put(LogLevel.ERROR, Level.ERROR);
		levels.put(LogLevel.FATAL, Level.ERROR);
		levels.put(LogLevel.OFF, Level.OFF);
		LEVELS = Collections.unmodifiableMap(levels);
	}

	private static final TurboFilter FILTER = new TurboFilter() {

		@Override
		public FilterReply decide(Marker marker, ch.qos.logback.classic.Logger logger,
				Level level, String format, Object[] params, Throwable t) {
			return FilterReply.DENY;
		}

	};

	public LogbackLoggingSystem(ClassLoader classLoader) {
		super(classLoader);
	}

	@Override
	protected String[] getStandardConfigLocations() {
		return new String[] { "logback-test.groovy", "logback-test.xml",
				"logback.groovy", "logback.xml" };
	}

	@Override
	public void beforeInitialize() {
		super.beforeInitialize();
		getLogger(null).getLoggerContext().getTurboFilterList().add(FILTER);
		configureJBossLoggingToUseSlf4j();
	}

	@Override
	public void initialize(LoggingInitializationContext initializationContext,
			String configLocation, LogFile logFile) {
		getLogger(null).getLoggerContext().getTurboFilterList().remove(FILTER);
		super.initialize(initializationContext, configLocation, logFile);
	}

	@Override
	protected void loadDefaults(LoggingInitializationContext initializationContext,
			LogFile logFile) {
		LoggerContext context = getLoggerContext();
		context.stop();
		context.reset();
        installLevelChangePropagator(context);
        LogbackConfigurator configurator = new LogbackConfigurator(context);
		new DefaultLogbackConfiguration(initializationContext, logFile)
				.apply(configurator);
	}

	@Override
	protected void loadConfiguration(LoggingInitializationContext initializationContext,
			String location, LogFile logFile) {
		Assert.notNull(location, "Location must not be null");
		if (logFile != null) {
			logFile.applyToSystemProperties();
		}
		LoggerContext loggerContext = getLoggerContext();
		loggerContext.stop();
		loggerContext.reset();
        installLevelChangePropagator(loggerContext);
		try {
			configureByResourceUrl(initializationContext, loggerContext,
					ResourceUtils.getURL(location));
		}
		catch (Exception ex) {
			throw new IllegalStateException("Could not initialize Logback logging from "
					+ location, ex);
		}
		List<Status> statuses = loggerContext.getStatusManager().getCopyOfStatusList();
		StringBuilder errors = new StringBuilder();
		for (Status status : statuses) {
			if (status.getLevel() == Status.ERROR) {
				errors.append(errors.length() > 0 ? "\n" : "");
				errors.append(status.toString());
			}
		}
		if (errors.length() > 0) {
			throw new IllegalStateException("Logback configuration error "
					+ "detected: \n" + errors);
		}
	}

    /**
     * ref {@link org.slf4j.bridge.SLF4JBridgeHandler}
     */
    private void installLevelChangePropagator(LoggerContext context) {
        LevelChangePropagator levelChangePropagator = new LevelChangePropagator();
        levelChangePropagator.setResetJUL(true);
        levelChangePropagator.setContext(context);
        levelChangePropagator.start();
        context.addListener(levelChangePropagator);
    }

	private void configureByResourceUrl(
			LoggingInitializationContext initializationContext,
			LoggerContext loggerContext, URL url) throws JoranException {
		if (url.toString().endsWith("xml")) {
			JoranConfigurator configurator = new SpringBootJoranConfigurator(
					initializationContext);
			configurator.setContext(loggerContext);
			configurator.doConfigure(url);
		}
		else {
			new ContextInitializer(loggerContext).configureByResource(url);
		}
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
		getLoggerContext().getStatusManager().clear();
	}

	@Override
	protected void reinitialize(LoggingInitializationContext initializationContext) {
		getLoggerContext().reset();
		getLoggerContext().getStatusManager().clear();
		loadConfiguration(initializationContext, getSelfInitializationConfig(), null);
	}

	private void configureJBossLoggingToUseSlf4j() {
		System.setProperty("org.jboss.logging.provider", "slf4j");
	}

	@Override
	public void setLogLevel(String loggerName, LogLevel level) {
		getLogger(loggerName).setLevel(LEVELS.get(level));
	}

	protected ch.qos.logback.classic.Logger getLogger(String name) {
		LoggerContext factory = getLoggerContext();
		return factory.getLogger(StringUtils.isEmpty(name) ? Logger.ROOT_LOGGER_NAME
				: name);

	}

    protected LoggerContext getLoggerContext() {
		ILoggerFactory factory = StaticLoggerBinder.getSingleton().getLoggerFactory();
		Assert.isInstanceOf(LoggerContext.class, factory, String.format(
				"LoggerFactory is not a Logback LoggerContext but Logback is on "
						+ "the classpath. Either remove Logback or the competing "
						+ "implementation (%s loaded from %s). If you are using "
						+ "Weblogic you will need to add 'org.slf4j' to "
						+ "prefer-application-packages in WEB-INF/weblogic.xml",
				factory.getClass(), getLocation(factory)));
		return (LoggerContext) factory;
	}

	private Object getLocation(ILoggerFactory factory) {
		try {
			ProtectionDomain protectionDomain = factory.getClass().getProtectionDomain();
			CodeSource codeSource = protectionDomain.getCodeSource();
			if (codeSource != null) {
				return codeSource.getLocation();
			}
		}
		catch (SecurityException ex) {
			// Unable to determine location
		}
		return "unknown location";
	}

}
