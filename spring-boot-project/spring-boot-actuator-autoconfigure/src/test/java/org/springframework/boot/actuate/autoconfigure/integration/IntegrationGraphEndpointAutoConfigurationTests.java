/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.boot.actuate.autoconfigure.integration;

import org.junit.Test;

import org.springframework.boot.actuate.integration.IntegrationGraphEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link IntegrationGraphEndpointAutoConfiguration}.
 *
 * @author Tim Ysewyn
 */
public class IntegrationGraphEndpointAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(IntegrationGraphEndpointAutoConfiguration.class));

	@Test
	public void runShouldNotHaveEndpointBean() {
		this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(IntegrationGraphEndpoint.class));
	}

	@Test
	public void runWhenEnabledPropertyIsTrueShouldHaveEndpointBean() {
		this.contextRunner.withPropertyValues("management.endpoint.integrationgraph.enabled:true")
				.run((context) -> assertThat(context).hasSingleBean(IntegrationGraphEndpoint.class));
	}

}
