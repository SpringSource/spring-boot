/*
 * Copyright 2012-2021 the original author or authors.
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

package org.springframework.boot.autoconfigure.web.reactive;

import java.nio.charset.Charset;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.util.unit.DataSize;

/**
 * {@link ConfigurationProperties Configuration properties} for configuring multipart
 * support in Spring Webflux. Used to configure the {@link DefaultPartHttpMessageReader}.
 *
 * @author Chris Bono
 * @since 2.6.0
 */
@ConfigurationProperties(prefix = "spring.webflux.multipart", ignoreUnknownFields = false)
public class MultipartProperties {

	/**
	 * Maximum amount of memory allowed per part in bytes. Leave blank to use default of
	 * 256K or set to -1 to store all contents in memory. Ignored when streaming is
	 * enabled.
	 */
	private DataSize maxInMemorySize;

	/**
	 * Maximum amount of memory allowed per headers section of each part in bytes. Leave
	 * blank to use default of 8K or set to -1 to enforce no limits.
	 */
	private DataSize maxHeadersSize;

	/**
	 * Maximum amount of disk space allowed per part in bytes. Default is -1 which
	 * enforces no limits. Ignored when streaming is enabled or 'maxInMemorySize' is set
	 * to -1.
	 */
	private DataSize maxDiskUsagePerPart;

	/**
	 * Maximum number of parts allowed in a given multipart request. Default is -1 which
	 * enforces no limits.
	 */
	private Integer maxParts;

	/**
	 * Whether or not to stream directly from the parsed input buffer stream without
	 * storing in memory nor file. Default is non-streaming.
	 */
	private Boolean streaming;

	/**
	 * Directory used to store parts larger than 'maxInMemorySize'. Default is a directory
	 * named 'spring-webflux-multipart' created under the system temporary directory.
	 * Ignored when streaming is enabled or 'maxInMemorySize' is set to -1.
	 */
	private String fileStorageDirectory;

	/**
	 * Character set used to decode headers. Default is UTF-8.
	 */
	private Charset headersCharset;

	public DataSize getMaxInMemorySize() {
		return maxInMemorySize;
	}

	public void setMaxInMemorySize(DataSize maxInMemorySize) {
		this.maxInMemorySize = maxInMemorySize;
	}

	public DataSize getMaxHeadersSize() {
		return maxHeadersSize;
	}

	public void setMaxHeadersSize(DataSize maxHeadersSize) {
		this.maxHeadersSize = maxHeadersSize;
	}

	public DataSize getMaxDiskUsagePerPart() {
		return maxDiskUsagePerPart;
	}

	public void setMaxDiskUsagePerPart(DataSize maxDiskUsagePerPart) {
		this.maxDiskUsagePerPart = maxDiskUsagePerPart;
	}

	public Integer getMaxParts() {
		return maxParts;
	}

	public void setMaxParts(Integer maxParts) {
		this.maxParts = maxParts;
	}

	public Boolean getStreaming() {
		return streaming;
	}

	public void setStreaming(Boolean streaming) {
		this.streaming = streaming;
	}

	public String getFileStorageDirectory() {
		return fileStorageDirectory;
	}

	public void setFileStorageDirectory(String fileStorageDirectory) {
		this.fileStorageDirectory = fileStorageDirectory;
	}

	public Charset getHeadersCharset() {
		return headersCharset;
	}

	public void setHeadersCharset(Charset headersCharset) {
		this.headersCharset = headersCharset;
	}

}
