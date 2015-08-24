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

package org.springframework.boot.devtools.autoconfigure;

import org.junit.Test;
import org.springframework.boot.devtools.autoconfigure.OptionalLiveReloadServer;
import org.springframework.boot.devtools.livereload.LiveReloadServer;

import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link OptionalLiveReloadServer}.
 *
 * @author Phillip Webb
 */
public class OptionalLiveReloadServerTests {

	@Test
	public void nullServer() throws Exception {
		OptionalLiveReloadServer server = new OptionalLiveReloadServer(null);
		server.startServer();
		server.triggerReload();
	}

	@Test
	public void serverWontStart() throws Exception {
		LiveReloadServer delegate = mock(LiveReloadServer.class);
		OptionalLiveReloadServer server = new OptionalLiveReloadServer(delegate);
		willThrow(new RuntimeException("Error")).given(delegate).start();
		server.startServer();
		server.triggerReload();
		verify(delegate, never()).triggerReload();
	}

}
