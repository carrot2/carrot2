/*
 * Copyright 2006 Sean Owen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.planetj.servlet.filter.compression;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * <p>Implementation of {@link HttpServletRequest} which can decompress request bodies that have
 * been compressed.</p>
 *
 * @author Sean Owen
 * @since 1.6
 */
// Doesn't have any effect in javac right now, but I'm putting in this annotation to work around a javac bug by
// which it emits warnings on methods like <code>getRealPath()</code>, which are deprecated, but not actually
// overridden in this class.
final class CompressedHttpServletRequest extends HttpServletRequestWrapper {

	private final HttpServletRequest httpRequest;
	private final CompressingStreamFactory compressingStreamFactory;
	private final CompressingFilterContext context;
	private CompressingServletInputStream compressedSIS;
	private BufferedReader bufferedReader;
	private boolean isGetInputStreamCalled;
	private boolean isGetReaderCalled;

	CompressedHttpServletRequest(final HttpServletRequest httpRequest,
	                             final CompressingStreamFactory compressingStreamFactory,
	                             final CompressingFilterContext context) {
		super(httpRequest);
		this.httpRequest = httpRequest;
		this.compressingStreamFactory = compressingStreamFactory;
		this.context = context;
	}


	public ServletInputStream getInputStream() throws IOException {
		if (isGetReaderCalled) {
			throw new IllegalStateException("getReader() has already been called");
		}
		isGetInputStreamCalled = true;
		return getCompressingServletInputStream();
	}

	public BufferedReader getReader() throws IOException {
		if (isGetInputStreamCalled) {
			throw new IllegalStateException("getInputStream() has already been called");
		}
		isGetReaderCalled = true;
		if (bufferedReader == null) {
			bufferedReader = new BufferedReader(new InputStreamReader(getCompressingServletInputStream(),
					                                                  getCharacterEncoding()));
		}
		return bufferedReader;
	}

	private CompressingServletInputStream getCompressingServletInputStream() throws IOException {
		if (compressedSIS == null) {
			compressedSIS = new CompressingServletInputStream(httpRequest.getInputStream(),
			                                                  compressingStreamFactory,
					                                          context);
		}
		return compressedSIS;
	}

	// Header-related methods -- need to make sure we consume and hide the
	// Content-Encoding header. What a lot of work to get that done:

	private static boolean isFilteredHeader(final String headerName) {
		// Filter Content-Encoding since we're handing decompression ourselves;
		// filter Accept-Encoding so that downstream services don't try to compress too
		return CompressingHttpServletResponse.CONTENT_ENCODING_HEADER.equalsIgnoreCase(headerName) ||
			   CompressingHttpServletResponse.ACCEPT_ENCODING_HEADER.equalsIgnoreCase(headerName);
	}

	public String getHeader(final String header) {
		return isFilteredHeader(header) ? null : super.getHeader(header);
	}

	public Enumeration getHeaders(final String header) {
		final Enumeration original = super.getHeaders(header);
		if (original == null) {
			return null; // match container's behavior exactly in this case
		}
		return isFilteredHeader(header) ? EmptyEnumeration.getInstance() : original;
	}

	public long getDateHeader(final String header) {
		return isFilteredHeader(header) ? -1L : super.getDateHeader(header);
	}

	public int getIntHeader(final String header) {
		return isFilteredHeader(header) ? -1 : super.getIntHeader(header);
	}

	public Enumeration getHeaderNames() {
		final Enumeration original = super.getHeaderNames();
		if (original == null) {
			return null; // match container's behavior exactly in this case
		}
		final List headerNames = new ArrayList();
		while (original.hasMoreElements()) {
			final String headerName = (String) original.nextElement();
			if (!isFilteredHeader(headerName)) {
				headerNames.add(headerName);
			}
		}
		return new IteratorEnumeration(headerNames.iterator());
	}

	public String toString() {
		return "CompressedHttpServletRequest";
	}

}
