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

package org.springframework.boot;

import java.io.File;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ApplicationTemp}.
 *
 * @author Phillip Webb
 */
public class ApplicationTempTests {

	@Test
	public void generatesConsistentTemp() throws Exception {
		ApplicationTemp t1 = new ApplicationTemp();
		ApplicationTemp t2 = new ApplicationTemp();
		assertThat(t1.getFolder(), notNullValue());
		assertThat(t1.getFolder(), equalTo(t2.getFolder()));
	}

	@Test
	public void differentBasedOnUserDir() throws Exception {
		String userDir = System.getProperty("user.dir");
		try {
			File t1 = new ApplicationTemp().getFolder();
			System.setProperty("user.dir", "abc");
			File t2 = new ApplicationTemp().getFolder();
			assertThat(t1, not(equalTo(t2)));
		}
		finally {
			System.setProperty("user.dir", userDir);
		}
	}

	@Test
	public void getSubFolder() throws Exception {
		ApplicationTemp temp = new ApplicationTemp();
		assertThat(temp.getFolder("abc"), equalTo(new File(temp.getFolder(), "abc")));
	}

}
