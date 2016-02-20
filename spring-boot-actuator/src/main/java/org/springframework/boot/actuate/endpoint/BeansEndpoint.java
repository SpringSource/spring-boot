/*
 * Copyright 2012-2014 the original author or authors.
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

package org.springframework.boot.actuate.endpoint;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.LiveBeansView;
import org.springframework.core.env.Environment;

/**
 * Exposes JSON view of Spring beans. If the {@link Environment} contains a key setting
 * the {@link LiveBeansView#MBEAN_DOMAIN_PROPERTY_NAME} then all application contexts in
 * the JVM will be shown (and the corresponding MBeans will be registered per the standard
 * behavior of LiveBeansView). Otherwise only the current application context.
 *
 * @author Dave Syer
 */
@ConfigurationProperties(prefix = "endpoints.beans", ignoreUnknownFields = false)
public class BeansEndpoint extends AbstractEndpoint<List<Object>>
		implements ApplicationContextAware {

	private final LiveBeansView liveBeansView = new LiveBeansView();

	private final JsonParser parser = JsonParserFactory.getJsonParser();

	public BeansEndpoint() {
		super("beans");
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		if (context.getEnvironment()
				.getProperty(LiveBeansView.MBEAN_DOMAIN_PROPERTY_NAME) == null) {
			this.liveBeansView.setApplicationContext(context);
		}
	}

	@Override
	public List<Object> invoke() {
		return this.parser.parseList(this.liveBeansView.getSnapshotAsJson());
	}
}
