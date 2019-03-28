/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfigurationTests;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfigurationTests;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorControllerDirectMockMvcTests;

/**
 * A test suite for probing weird ordering problems in the tests.
 *
 * @author Dave Syer
 */
@RunWith(Suite.class)
@SuiteClasses({ BasicErrorControllerDirectMockMvcTests.class,
		JmxAutoConfigurationTests.class, IntegrationAutoConfigurationTests.class })
@Ignore
public class AdhocTestSuite {

}
