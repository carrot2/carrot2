/*
 * Copyright 2004-2006 Sean Owen
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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Implementation of {@link HttpServletResponse} which will optionally compress data written to the response.
 *
 * @author Sean Owen
 */
// Doesn't have any effect in javac right now, but I'm putting in this annotation to work around a javac bug by
// which it emits warnings on methods like <code>setStatus()</code>, which are deprecated, but not actually
// overridden in this class.
final class CompressingHttpServletResponse extends HttpServletResponseWrapper {

	static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
	static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
	private static final String CONTENT_LENGTH_HEADER = "Content-Length";
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	private static final String VARY_HEADER = "Vary";
	private static final String X_COMPRESSED_BY_HEADER = "X-Compressed-By";

	private static final String COMPRESSED_BY_VALUE = CompressingFilter.VERSION_STRING;


	private final HttpServletResponse httpResponse;

	private final CompressingFilterContext context;
	private final CompressingFilterLoggerImpl logger;

	private final String compressedContentEncoding;
	private final CompressingStreamFactory compressingStreamFactory;
	private CompressingServletOutputStream compressingSOS;

	private PrintWriter printWriter;
	private boolean isGetOutputStreamCalled;
	private boolean isGetWriterCalled;

	private boolean compressing;

	private int savedContentLength;
	private boolean savedContentLengthSet;
	private String savedContentEncoding;
	private boolean contentTypeOK;


	CompressingHttpServletResponse(final HttpServletResponse httpResponse,
	                               final CompressingStreamFactory compressingStreamFactory,
	                               final String contentEncoding,
	                               final CompressingFilterContext context) {
		super(httpResponse);
		this.httpResponse = httpResponse;
		this.compressedContentEncoding = contentEncoding;
		compressing = false;
		logger = context.getLogger();
		this.compressingStreamFactory = compressingStreamFactory;
		this.context = context;
		contentTypeOK = true;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (isGetWriterCalled) {
			throw new IllegalStateException("getWriter() has already been called");
		}
		isGetOutputStreamCalled = true;
		return getCompressingServletOutputStream();
	}

	public PrintWriter getWriter() throws IOException {
		if (isGetOutputStreamCalled) {
			throw new IllegalStateException("getCompressingOutputStream() has already been called");
		}
		isGetWriterCalled = true;
		if (printWriter == null) {
			printWriter = new PrintWriter(new OutputStreamWriter(getCompressingServletOutputStream(),
			                                                     getCharacterEncoding()),
			                              true);
		}
		return printWriter;
	}

	public void addHeader(final String name, final String value) {
		if (isAllowedHeader(name)) {
			httpResponse.addHeader(name, value);
		}
	}

	public void addIntHeader(final String name, final int value) {
		if (isAllowedHeader(name)) {
			httpResponse.addIntHeader(name, value);
		}
	}

	public void addDateHeader(final String name, final long value) {
		if (isAllowedHeader(name)) {
			httpResponse.addDateHeader(name, value);
		}
	}

	public void setHeader(final String name, final String value) {
		if (CONTENT_ENCODING_HEADER.equalsIgnoreCase(name)) {
			savedContentEncoding = value;
		} else if (CONTENT_LENGTH_HEADER.equalsIgnoreCase(name)) {
			setContentLength(Integer.parseInt(value));
		} else if (CONTENT_TYPE_HEADER.equalsIgnoreCase(name)) {
			setContentType(value);
		} else if (isAllowedHeader(name)) {
			httpResponse.setHeader(name, value);
		}
	}

	public void setIntHeader(final String name, final int value) {
		if (CONTENT_LENGTH_HEADER.equalsIgnoreCase(name)) {
			setContentLength(value);
		} else if (isAllowedHeader(name)) {
			httpResponse.setIntHeader(name, value);
		}
	}

	public void setDateHeader(final String name, final long value) {
		if (isAllowedHeader(name)) {
			httpResponse.setDateHeader(name, value);
		}
	}

	public void flushBuffer() throws IOException {
		flushWriter(); // make sure nothing is buffered in the writer, if applicable
		if (compressingSOS != null) {
			compressingSOS.flush();
		}
	}

	public void reset() {
		flushWriter(); // make sure nothing is buffered in the writer, if applicable
		if (compressingSOS != null) {
			compressingSOS.reset();
		}
		httpResponse.reset();
		if (compressing) {
			setResponseHeaders();
		}
	}

	public void resetBuffer() {
		flushWriter(); // make sure nothing is buffered in the writer, if applicable
		if (compressingSOS != null) {
			compressingSOS.reset();
		}
		httpResponse.resetBuffer();
	}

	public void setContentLength(final int contentLength) {
		if (compressing) {
			// do nothing -- caller-supplied content length is not meaningful
			logger.logDebug("Ignoring application-specified content length since response is compressed");
		} else {
			savedContentLength = contentLength;
			savedContentLengthSet = true;
			logger.logDebug("Saving application-specified content length for later: " + contentLength);
		}
	}

	public void setContentType(final String contentType) {
		contentTypeOK = isCompressableContentType(contentType);
		if (!compressing) {
			httpResponse.setContentType(contentType);
		}
	}

	public String toString() {
		return "CompressingHttpServletResponse[compressing: " + compressing + ']';
	}

	boolean isCompressing() {
		return compressing;
	}

	void close() throws IOException {
		if (compressingSOS != null && !compressingSOS.isClosed()) {
			compressingSOS.close();
		}
	}


	private void setResponseHeaders() {
		logger.logDebug("Setting compression-related headers");
		httpResponse.setHeader(CONTENT_ENCODING_HEADER, compressedContentEncoding);
		httpResponse.addHeader(VARY_HEADER, ACCEPT_ENCODING_HEADER);
		if (context.isDebug()) {
			httpResponse.setHeader(X_COMPRESSED_BY_HEADER, COMPRESSED_BY_VALUE);
		}
	}

	void rawStreamCommitted() {
        Compat.assertion(!compressing);
		logger.logDebug("Committing response without compression");
		if (savedContentLengthSet) {
			httpResponse.setContentLength(savedContentLength);
		}
		if (savedContentEncoding != null) {
			httpResponse.setHeader(CONTENT_ENCODING_HEADER, savedContentEncoding);
		}
	}

	void switchToCompression() {
        Compat.assertion(!compressing);
		logger.logDebug("Switching to compression in the response");
		compressing = true;
		setResponseHeaders();
	}

	/**
	 * <p>Returns true if and only if the named HTTP header may be set directly by the application, as some headers must be
	 * handled specially. null is allowed, though it setting a header named null will probably generate an exception from
	 * the underlying {@link HttpServletResponse}. {@link #CONTENT_LENGTH_HEADER}, {@link #CONTENT_ENCODING_HEADER} and
	 * {@link #X_COMPRESSED_BY_HEADER} are not allowed.</p>
	 *
	 * @param header name of HTTP header
	 * @return true if and only if header can be set directly by application
	 */
	private boolean isAllowedHeader(final String header) {
		final boolean result =
		    header == null ||
		    !(CONTENT_LENGTH_HEADER.equalsIgnoreCase(header) ||
		      CONTENT_ENCODING_HEADER.equalsIgnoreCase(header) ||
		      X_COMPRESSED_BY_HEADER.equalsIgnoreCase(header));
		if (!result && logger.isDebug()) {
			logger.logDebug("Header '" + header + "' cannot be set by application");
		}
		return result;
	}

	private void flushWriter() {
		if (printWriter != null) {
			printWriter.flush();
		}
	}

	/**
	 * Checks to see if the given content type should be compressed. This checks against the
	 * <code>includeContentTypes</code> and <code>excludeContentTypes</code> filter init parameters; if the former
	 * is set and the given content type is in that parameter's list, or if the latter is set and the content type
	 * is not in that list, then this method returns <code>true</code>.
	 *
	 * @param contentType content type of response
	 * @return true if and only if the given content type should be compressed
	 */
	private boolean isCompressableContentType(final String contentType) {
		String contentTypeOnly = contentType;
		if (contentType != null) {
			final int semicolonIndex = contentType.indexOf((int) ';');
			if (semicolonIndex >= 0) {
				contentTypeOnly = contentType.substring(0, semicolonIndex);
			}
		}

		final boolean isContained = context.getContentTypes().contains(contentTypeOnly);
		return context.isIncludeContentTypes() ? isContained : !isContained;
	}

	private CompressingServletOutputStream getCompressingServletOutputStream() throws IOException {
		if (compressingSOS == null) {
			compressingSOS =
			    new CompressingServletOutputStream(httpResponse.getOutputStream(),
			                                       compressingStreamFactory,
			                                       this,
			                                       context);
		}

		// Do we already know we don't want to compress?
		// Is there a reason we know compression will be used, already?
		if (mustNotCompress()) {
			compressingSOS.abortCompression();
		} else if (mustCompress()) {
			compressingSOS.engageCompression();
		}

		return compressingSOS;
	}

	private boolean mustNotCompress() {
		if (!contentTypeOK) {
			logger.logDebug("Will not compress since configuration excludes this content type");
			return true;
		}
		if (savedContentLengthSet &&
		    savedContentLength < context.getCompressionThreshold()) {
			logger.logDebug("Will not compress since page has set a content length which is less than " +
			                "the compression threshold: " + savedContentLength);
			return true;
		}
		return false;
	}

	private boolean mustCompress() {
		if (savedContentLengthSet &&
		    savedContentLength >= context.getCompressionThreshold()) {
			logger.logDebug("Will begin compression immediately since page has set a content length which is " +
			                "is greater than or equal to the compression threshold: " + savedContentLength);
			return true;
		}
		return false;
	}
}
