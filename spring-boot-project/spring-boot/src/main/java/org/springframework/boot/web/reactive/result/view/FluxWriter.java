/*
 * Copyright 2016-2017 the original author or authors.
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

package org.springframework.boot.web.reactive.result.view;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;

/**
 * A {@link Writer} that can write a {@link Flux} (or {@link Publisher}) to a data buffer.
 * Used to render progressive output in a {@link MustacheView}.
 *
 * @author Dave Syer
 */
class FluxWriter extends Writer {

	private final Supplier<DataBuffer> factory;

	private final Charset charset;

	private List<String> current = new ArrayList<>();

	private List<Object> accumulated = new ArrayList<>();

	public FluxWriter(Supplier<DataBuffer> factory) {
		this(factory, Charset.defaultCharset());
	}

	public FluxWriter(Supplier<DataBuffer> factory, Charset charset) {
		this.factory = factory;
		this.charset = charset;
	}

	public Publisher<? extends Publisher<? extends DataBuffer>> getBuffers() {
		Flux<String> buffers = Flux.empty();
		if (!this.current.isEmpty()) {
			this.accumulated.add(new ArrayList<>(this.current));
			this.current.clear();
		}
		for (Object thing : this.accumulated) {
			if (thing instanceof Publisher) {
				@SuppressWarnings("unchecked")
				Publisher<String> publisher = (Publisher<String>) thing;
				buffers = buffers.concatWith(publisher);
			}
			else {
				@SuppressWarnings("unchecked")
				List<String> list = (List<String>) thing;
				buffers = buffers.concatWithValues(list.toArray(new String[0]));
			}
		}
		return buffers.map(string -> Mono.just(buffer().write(string, this.charset)));
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		this.current.add(new String(cbuf, off, len));
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}

	public void release() {
		// TODO: maybe implement this and call it on error
	}

	private DataBuffer buffer() {
		return this.factory.get();
	}

	public void write(Object thing) {
		if (thing instanceof Publisher) {
			if (!this.current.isEmpty()) {
				this.accumulated.add(new ArrayList<>(this.current));
				this.current.clear();
			}
			this.accumulated.add(thing);
		}
		else {
			if (thing instanceof String) {
				this.current.add((String) thing);
			}
		}
	}

}