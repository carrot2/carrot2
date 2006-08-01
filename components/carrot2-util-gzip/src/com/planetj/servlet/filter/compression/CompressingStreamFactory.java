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

import java.io.*;
import java.util.*;
import java.util.zip.*;

import javax.servlet.http.HttpServletRequest;

import org.carrot2.util.zip.JZlibDeflaterOutputStream;
import org.carrot2.util.zip.JZlibGZIPOutputStream;

/**
 * <p>Implementations of this abstract class can add compression of a particular type to a given {@link OutputStream}.
 * They each return a {@link CompressingOutputStream}, which is just a thin wrapper on top of an {@link OutputStream} that
 * adds the ability to "finish" a stream (see {@link CompressingOutputStream}).</p>
 * <p/>
 * <p>This class contains implementations based on several popular compression algorithms, such as gzip. For example,
 * the gzip implementation can decorate an {@link OutputStream} using an instance of {@link GZIPOutputStream} and in
 * that way add gzip compression to the stream.</p>
 *
 * @author Sean Owen
 */
abstract class CompressingStreamFactory {

	/**
	 * Implementation based on {@link GZIPOutputStream} and {@link GZIPInputStream}.
	 */
	private static final CompressingStreamFactory GZIP_CSF = new JZlibGZIPCompressingStreamFactory();

	/**
	 * Implementation based on {@link ZipOutputStream} and {@link ZipInputStream}.
	 */
	private static final CompressingStreamFactory ZIP_CSF = new ZipCompressingStreamFactory();

	/**
	 * Implementation based on {@link DeflaterOutputStream}.
	 */
	private static final CompressingStreamFactory DEFLATE_CSF = new JZlibDeflateCompressingStreamFactory();

	/**
	 * "No encoding" content type: "identity".
	 */
	static final String NO_ENCODING = "identity";

	private static final String GZIP_ENCODING = "gzip";
	private static final String X_GZIP_ENCODING = "x-gzip";
	private static final String DEFLATE_ENCODING = "deflate";
	private static final String COMPRESS_ENCODING = "compress";
	private static final String X_COMPRESS_ENCODING = "x-compress";

	/**
	 * "Any encoding" content type: the "*" wildcard.
	 */
	private static final String ANY_ENCODING = "*";
	/**
	 * Ordered list of preferred encodings.
	 */
	private static final String[] preferredEncodings = new String[]{
		GZIP_ENCODING,
		DEFLATE_ENCODING,
		COMPRESS_ENCODING,
		X_GZIP_ENCODING,
		X_COMPRESS_ENCODING,
		NO_ENCODING
	};

	/**
	 * Cache mapping previously seen "Accept-Encoding" header Strings to an appropriate instance of {@link
	 * CompressingStreamFactory}.
	 */
	private static final Map /* <String, String> */ bestEncodingCache =
	    Collections.synchronizedMap(new HashMap(101));

	/**
	 * Maps content type String to appropriate implementation of {@link CompressingStreamFactory}.
	 */
	private static final Map /* <String, CompressingStreamFactory> */ factoryMap;

	static {
		final Map /* <String, CompressingStreamFactory> */ temp = new HashMap(11);
		temp.put(GZIP_ENCODING, GZIP_CSF);
		temp.put(X_GZIP_ENCODING, GZIP_CSF);
		temp.put(COMPRESS_ENCODING, ZIP_CSF);
		temp.put(X_COMPRESS_ENCODING, ZIP_CSF);
		temp.put(DEFLATE_ENCODING, DEFLATE_CSF);
		factoryMap = Collections.unmodifiableMap(temp);
	}


	abstract CompressingOutputStream getCompressingStream(OutputStream servletOutputStream,
	                                                      CompressingFilterContext context) throws IOException;

	abstract CompressingInputStream getCompressingStream(InputStream servletInputStream,
	                                                     CompressingFilterContext context) throws IOException;

	private static OutputStream getStatsOutputStream(final OutputStream outputStream,
	                                                 final CompressingFilterContext context,
	                                                 final CompressingFilterStats.StatsField field) {
		Compat.assertion(outputStream != null);
		final OutputStream result;
		if (context.isStatsEnabled()) {
			final CompressingFilterStats stats = context.getStats();
			final CompressingFilterStats.OutputStatsCallback callbackOutput = stats.getOutputStatsCallback(field);
			result = new StatsOutputStream(outputStream, callbackOutput);
		} else {
			result = outputStream;
		}
		return result;
	}

	private static InputStream getStatsInputStream(final InputStream inputStream,
	                                               final CompressingFilterContext context,
	                                               final CompressingFilterStats.StatsField field) {
        Compat.assertion(inputStream != null);
		final InputStream result;
		if (context.isStatsEnabled()) {
			final CompressingFilterStats stats = context.getStats();
			final CompressingFilterStats.InputStatsCallback callbackInput = stats.getInputStatsCallback(field);
			result = new StatsInputStream(inputStream, callbackInput);
		} else {
			result = inputStream;
		}
		return result;
	}

	private static boolean isSupportedResponseContentEncoding(final String contentEncoding) {
		return NO_ENCODING.equals(contentEncoding) || factoryMap.containsKey(contentEncoding);
	}

	static boolean isSupportedRequestContentEncoding(final String contentEncoding) {
		// A little kludgy -- deflate isn't supported in requests
		return
			NO_ENCODING.equals(contentEncoding) ||
			(factoryMap.containsKey(contentEncoding) && !DEFLATE_ENCODING.equals(contentEncoding));
	}

	/**
	 * Returns the {@link CompressingStreamFactory} instance associated to the given content encoding.
	 *
	 * @param contentEncoding content encoding (e.g. "gzip")
	 * @return {@link CompressingStreamFactory} for content encoding
	 */
	static CompressingStreamFactory getFactoryForContentEncoding(final String contentEncoding) {
        Compat.assertion(factoryMap.containsKey(contentEncoding));
		return (CompressingStreamFactory) factoryMap.get(contentEncoding);
	}

	/**
	 * Determines best content encoding for the response, based on the request -- in particular, based on its
	 * "Accept-Encoding" header.
	 *
	 * @param httpRequest request
	 * @return best content encoding
	 */
	static String getBestContentEncoding(final HttpServletRequest httpRequest) {

		final String forcedEncoding = (String) httpRequest.getAttribute(CompressingFilter.FORCE_ENCODING_KEY);
		String bestEncoding;
		if (forcedEncoding != null) {

			bestEncoding = forcedEncoding;

		} else {

			final String acceptEncodingHeader = httpRequest.getHeader(
					CompressingHttpServletResponse.ACCEPT_ENCODING_HEADER);
			if (acceptEncodingHeader == null) {

				bestEncoding = NO_ENCODING;

			} else {

				bestEncoding = (String) bestEncodingCache.get(acceptEncodingHeader);

				if (bestEncoding == null) {

					// No cached value; must parse header to determine best encoding
					// I don't synchronize on bestEncodingCache; it's not worth it to avoid the rare case where
					// two thread get in here and both parse the header. It's only a tiny bit of extra work, and
					// avoids the synchronization overhead.

					if (acceptEncodingHeader.indexOf(',') >= 0) {
						// multiple encodings are accepted
						bestEncoding = selectBestEncoding(acceptEncodingHeader);
					} else {
						// one encoding is accepted
						bestEncoding = parseBestEncoding(acceptEncodingHeader);
					}

					bestEncodingCache.put(acceptEncodingHeader, bestEncoding);
				}
			}
		}

		// User-specified encoding might not be supported
		if (!isSupportedResponseContentEncoding(bestEncoding)) {
			bestEncoding = NO_ENCODING;
		}

		return bestEncoding;
	}

	private static String parseBestEncoding(final String acceptEncodingHeader) {
		final ContentEncodingQ contentEncodingQ = parseContentEncodingQ(acceptEncodingHeader);
		final String contentEncoding = contentEncodingQ.getContentEncoding();
		final String bestEncoding;
		if (contentEncodingQ.getQ() > 0.0) {
			if (ANY_ENCODING.equals(contentEncoding)) {
				bestEncoding = preferredEncodings[0];
			} else {
				bestEncoding = contentEncoding;
			}
		} else {
			// haven't explicitly said that any particular encoding is accepted;
			// default to no encoding
			bestEncoding = NO_ENCODING;
		}
        Compat.assertion(bestEncoding != null);
		return bestEncoding;
	}

	private static String selectBestEncoding(final String acceptEncodingHeader) {

		// multiple encodings are accepted; determine best one

		String bestEncoding = null;

		double bestQ = 0.0;

		Set unacceptableEncodings = null;
		boolean willAcceptAnything = false;

        final String [] tokens = acceptEncodingHeader.split(",");
		for (int i = 0; i < tokens.length; i++) {
            final String token = tokens[i];
			final ContentEncodingQ contentEncodingQ = parseContentEncodingQ(token);
			final String contentEncoding = contentEncodingQ.getContentEncoding();
			final double q = contentEncodingQ.getQ();
			if (ANY_ENCODING.equals(contentEncoding)) {
				willAcceptAnything = q > 0.0;
			} else {
				if (q > 0.0) {
					if (q > bestQ) {
						bestQ = q;
						bestEncoding = contentEncoding;
					}
				} else {
					// lazy instantiation
					if (unacceptableEncodings == null) {
						unacceptableEncodings = new HashSet(3);
					}
					unacceptableEncodings.add(contentEncoding);
				}
			}
		}

		if (bestEncoding == null) {

			if (willAcceptAnything) {
				if (unacceptableEncodings == null || unacceptableEncodings.isEmpty()) {
					bestEncoding = preferredEncodings[0];
				} else {
					for (int i = 0; i < preferredEncodings.length; i++) {
                        final String encoding = preferredEncodings[i];
						if (!unacceptableEncodings.contains(encoding)) {
							bestEncoding = encoding;
							break;
						}
					}
					if (bestEncoding == null) {
						bestEncoding = NO_ENCODING;
					}
				}
			} else {
				bestEncoding = NO_ENCODING;
			}
		}

		Compat.assertion(bestEncoding != null);
		return bestEncoding;
	}

	private static ContentEncodingQ parseContentEncodingQ(final String contentEncodingString) {

		double q = 1.0;

		final int qvalueStartIndex = contentEncodingString.indexOf(';');
		final String contentEncoding;
		if (qvalueStartIndex >= 0) {
			contentEncoding = contentEncodingString.substring(0, qvalueStartIndex).trim();
			final String qvalueString = contentEncodingString.substring(qvalueStartIndex + 1).trim();
			if (qvalueString.startsWith("q=")) {
				try {
					q = Double.parseDouble(qvalueString.substring(2));
				} catch (NumberFormatException nfe) {
					// That's bad -- browser sent an invalid number. All we can do is ignore it, and
					// pretend that no q value was specified, so that it effectively defaults to 1.0
				}
			}
		} else {
			contentEncoding = contentEncodingString.trim();
		}

		return new ContentEncodingQ(contentEncoding, q);
	}


	private static final class ContentEncodingQ {

		private final String contentEncoding;
		private final double q;

		private ContentEncodingQ(final String contentEncoding, final double q) {
            Compat.assertion(contentEncoding != null && contentEncoding.length() > 0);
			this.contentEncoding = contentEncoding;
			this.q = q;
		}

		private String getContentEncoding() {
			return contentEncoding;
		}

		private double getQ() {
			return q;
		}

		public String toString() {
			return contentEncoding + ";q=" + q;
		}
	}

    private static class JZlibGZIPCompressingStreamFactory extends CompressingStreamFactory {
        CompressingOutputStream getCompressingStream(final OutputStream outputStream,
                                                     final CompressingFilterContext context) throws IOException {
            return new CompressingOutputStream() {
                private final JZlibGZIPOutputStream gzipOutputStream =
                    new JZlibGZIPOutputStream(
                        CompressingStreamFactory.getStatsOutputStream(
                            outputStream, context, CompressingFilterStats.StatsField.O_RESPONSE_INPUT_BYTES));
                private final OutputStream statsOutputStream =
                    CompressingStreamFactory.getStatsOutputStream(
                        gzipOutputStream, context, CompressingFilterStats.StatsField.O_RESPONSE_COMPRESSED_BYTES);

                public OutputStream getCompressingOutputStream() {
                    return statsOutputStream;
                }

                public void finish() throws IOException {
                    gzipOutputStream.flush();
                }
            };
        }

        CompressingInputStream getCompressingStream(final InputStream inputStream,
                                                    final CompressingFilterContext context) {
            return new CompressingInputStream() {
                public InputStream getCompressingInputStream() throws IOException {
                    return CompressingStreamFactory.getStatsInputStream(
                        new GZIPInputStream(
                            CompressingStreamFactory.getStatsInputStream(
                                inputStream, context, CompressingFilterStats.StatsField.O_REQUEST_COMPRESSED_BYTES)
                        ),
                        context,
                        CompressingFilterStats.StatsField.O_REQUEST_INPUT_BYTES);
                }
            };
        }
    }

	private static class GZIPCompressingStreamFactory extends CompressingStreamFactory {
		CompressingOutputStream getCompressingStream(final OutputStream outputStream,
		                                             final CompressingFilterContext context) throws IOException {
			return new CompressingOutputStream() {
				private final GZIPOutputStream gzipOutputStream =
				    new GZIPOutputStream(
					    CompressingStreamFactory.getStatsOutputStream(
						    outputStream, context, CompressingFilterStats.StatsField.O_RESPONSE_INPUT_BYTES));
				private final OutputStream statsOutputStream =
				    CompressingStreamFactory.getStatsOutputStream(
					    gzipOutputStream, context, CompressingFilterStats.StatsField.O_RESPONSE_COMPRESSED_BYTES);

				public OutputStream getCompressingOutputStream() {
					return statsOutputStream;
				}

				public void finish() throws IOException {
					gzipOutputStream.finish();
				}
			};
		}

		CompressingInputStream getCompressingStream(final InputStream inputStream,
		                                            final CompressingFilterContext context) {
		    return new CompressingInputStream() {
			    public InputStream getCompressingInputStream() throws IOException {
				    return CompressingStreamFactory.getStatsInputStream(
					    new GZIPInputStream(
						    CompressingStreamFactory.getStatsInputStream(
							    inputStream, context, CompressingFilterStats.StatsField.O_REQUEST_COMPRESSED_BYTES)
					    ),
					    context,
					    CompressingFilterStats.StatsField.O_REQUEST_INPUT_BYTES);
			    }
		    };
		}
	}

	private static class ZipCompressingStreamFactory extends CompressingStreamFactory {
		CompressingOutputStream getCompressingStream(final OutputStream outputStream,
		                                             final CompressingFilterContext context) {
			return new CompressingOutputStream() {
				private final ZipOutputStream zipOutputStream =
				    new ZipOutputStream(
					    CompressingStreamFactory.getStatsOutputStream(
						    outputStream, context, CompressingFilterStats.StatsField.O_RESPONSE_INPUT_BYTES));
				private final OutputStream statsOutputStream =
				    CompressingStreamFactory.getStatsOutputStream(
					    zipOutputStream, context, CompressingFilterStats.StatsField.O_RESPONSE_COMPRESSED_BYTES);

				public OutputStream getCompressingOutputStream() {
					return statsOutputStream;
				}

				public void finish() throws IOException {
					zipOutputStream.finish();
				}
			};
		}

		CompressingInputStream getCompressingStream(final InputStream inputStream,
		                                            final CompressingFilterContext context) {
		    return new CompressingInputStream() {
			    public InputStream getCompressingInputStream() {
				    return CompressingStreamFactory.getStatsInputStream(
					    new ZipInputStream(
						    CompressingStreamFactory.getStatsInputStream(
							    inputStream, context, CompressingFilterStats.StatsField.O_REQUEST_COMPRESSED_BYTES)
					    ),
					    context,
					    CompressingFilterStats.StatsField.O_REQUEST_INPUT_BYTES);
			    }
		    };
		}
	}

	private static class DeflateCompressingStreamFactory extends CompressingStreamFactory {
		CompressingOutputStream getCompressingStream(final OutputStream outputStream,
		                                             final CompressingFilterContext context) {
			return new CompressingOutputStream() {
				private final DeflaterOutputStream deflaterOutputStream =
				    new DeflaterOutputStream(
					    CompressingStreamFactory.getStatsOutputStream(
						    outputStream, context, CompressingFilterStats.StatsField.O_RESPONSE_INPUT_BYTES));
				private final OutputStream statsOutputStream =
				    CompressingStreamFactory.getStatsOutputStream(
					    deflaterOutputStream, context, CompressingFilterStats.StatsField.O_RESPONSE_COMPRESSED_BYTES);

				public OutputStream getCompressingOutputStream() {
					return statsOutputStream;
				}

				public void finish() throws IOException {
					deflaterOutputStream.finish();
				}
			};
		}

		CompressingInputStream getCompressingStream(final InputStream inputStream,
		                                            final CompressingFilterContext context) {
			throw new UnsupportedOperationException();
		}
	}
    
    private static class JZlibDeflateCompressingStreamFactory extends CompressingStreamFactory {
        CompressingOutputStream getCompressingStream(final OutputStream outputStream,
                                                     final CompressingFilterContext context) {
            return new CompressingOutputStream() {
                private final JZlibDeflaterOutputStream deflaterOutputStream =
                    new JZlibDeflaterOutputStream(
                        CompressingStreamFactory.getStatsOutputStream(
                            outputStream, context, CompressingFilterStats.StatsField.O_RESPONSE_INPUT_BYTES));
                private final OutputStream statsOutputStream =
                    CompressingStreamFactory.getStatsOutputStream(
                        deflaterOutputStream, context, CompressingFilterStats.StatsField.O_RESPONSE_COMPRESSED_BYTES);

                public OutputStream getCompressingOutputStream() {
                    return statsOutputStream;
                }

                public void finish() throws IOException {
                    deflaterOutputStream.flush();
                }
            };
        }

        CompressingInputStream getCompressingStream(final InputStream inputStream,
                                                    final CompressingFilterContext context) {
            throw new UnsupportedOperationException();
        }
    }    
}