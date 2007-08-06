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

import java.io.Serializable;

/**
 * <p>This class provides runtime statistics on the performance of {@link CompressingFilter}. If stats are enabled, then
 * an instance of this object will be available in the servlet context under the key {@link #STATS_KEY}. It can be
 * retrieved and used like so:</p> <p/>
 * <pre>
 * ServletContext ctx = ...;
 * // in a JSP, "ctx" is already available as the "application" variable
 * CompressingFilterStats stats = (CompressingFilterStats) ctx.getAttribute(CompressingFilterStats.STATS_KEY);
 * double ratio = stats.getAverageCompressionRatio();
 * ...
 * </pre>
 *
 * @author Sean Owen
 * @since 1.1
 */
public final class CompressingFilterStats implements Serializable {

	private static final long serialVersionUID = -2246829834191152845L;

	/**
	 * Key under which a {@link CompressingFilterStats} object can be found in the servlet context.
	 */
	public static final String STATS_KEY = "com.planetj.servlet.filter.compression.CompressingFilterStats";

	/** @serial */
	private int numResponsesCompressed;
	/** @serial */
	private int totalResponsesNotCompressed;
	/** @serial */
	private long responseInputBytes;
	/** @serial */
	private long responseCompressedBytes;
	/** @serial */
	private int numRequestsCompressed;
	/** @serial */
	private int totalRequestsNotCompressed;
	/** @serial */
	private long requestInputBytes;
	/** @serial */
	private long requestCompressedBytes;
	/** @serial */
	private final OutputStatsCallback responseInputStatsCallback;
	/** @serial */
	private final OutputStatsCallback responseCompressedStatsCallback;
	/** @serial */
	private final InputStatsCallback requestInputStatsCallback;
	/** @serial */
	private final InputStatsCallback requestCompressedStatsCallback;

	CompressingFilterStats() {
		responseInputStatsCallback = new OutputStatsCallback(StatsField.O_RESPONSE_INPUT_BYTES);
		responseCompressedStatsCallback = new OutputStatsCallback(StatsField.O_RESPONSE_COMPRESSED_BYTES);
		requestInputStatsCallback = new InputStatsCallback(StatsField.O_REQUEST_INPUT_BYTES);
		requestCompressedStatsCallback = new InputStatsCallback(StatsField.O_REQUEST_COMPRESSED_BYTES);
	}

	/**
	 * @return the number of responses which {@link CompressingFilter} has compressed.
	 */
	public int getNumResponsesCompressed() {
		return numResponsesCompressed;
	}

	void incrementNumResponsesCompressed() {
		numResponsesCompressed++;
	}

	/**
	 * @return the number of responses which {@link CompressingFilter} has processed but <em>not</em> compressed for some
	 *         reason (compression not supported by the browser, for example).
	 */
	public int getTotalResponsesNotCompressed() {
		return totalResponsesNotCompressed;
	}

	void incrementTotalResponsesNotCompressed() {
		totalResponsesNotCompressed++;
	}

	/**
	 * @deprecated use {@link #getResponseInputBytes()}
	 */
	public long getInputBytes() {
		return responseInputBytes;
	}

	/**
	 * @return total number of bytes written to the {@link CompressingFilter} in responses.
	 */
	public long getResponseInputBytes() {
		return responseInputBytes;
	}

	/**
	 * @deprecated use {@link #getResponseCompressedBytes()}
	 */
	public long getCompressedBytes() {
		return responseCompressedBytes;
	}

	/**
	 * @return total number of compressed bytes written by the {@link CompressingFilter} to the client
	 *  in responses.
	 */
	public long getResponseCompressedBytes() {
		return responseCompressedBytes;
	}

	/**
	 * @deprecated use {@link #getResponseAverageCompressionRatio()}
	 */
	public double getAverageCompressionRatio() {
		return getResponseAverageCompressionRatio();
	}

	/**
	 * @return average compression ratio (input bytes / compressed bytes) in responses,
	 *  or 0 if nothing has yet been compressed. Note that this is (typically) greater than 1, not less than 1.
	 */
	public double getResponseAverageCompressionRatio() {
		return responseCompressedBytes == 0L ? 0.0 : (double) responseInputBytes / (double) responseCompressedBytes;
	}

	/**
	 * @return the number of requests which {@link CompressingFilter} has compressed.
	 * @since 1.6
	 */
	public int getNumRequestsCompressed() {
		return numRequestsCompressed;
	}

	void incrementNumRequestsCompressed() {
		numRequestsCompressed++;
	}

	/**
	 * @return the number of requests which {@link CompressingFilter} has processed but <em>not</em> compressed for some
	 *         reason (no compression requested, for example).
	 * @since 1.6
	 */
	public int getTotalRequestsNotCompressed() {
		return totalRequestsNotCompressed;
	}

	void incrementTotalRequestsNotCompressed() {
		totalRequestsNotCompressed++;
	}

	/**
	 * @return total number of bytes written to the {@link CompressingFilter} in requests.
	 * @since 1.6
	 */
	public long getRequestInputBytes() {
		return requestInputBytes;
	}

	/**
	 * @return total number of compressed bytes written by the {@link CompressingFilter} to the client
	 *  in requests.
	 * @since 1.6
	 */
	public long getRequestCompressedBytes() {
		return requestCompressedBytes;
	}

	/**
	 * @return average compression ratio (input bytes / compressed bytes) in requests,
	 *  or 0 if nothing has yet been compressed. Note that this is (typically) greater than 1, not less than 1.
	 * @since 1.6
	 */
	public double getRequestAverageCompressionRatio() {
		return requestCompressedBytes == 0L ? 0.0 : (double) requestInputBytes / (double) requestCompressedBytes;
	}

	/**
	 * @return a summary of the stats in String form
	 */
	public String toString() {
		return
			"CompressingFilterStats[responses compressed: " + numResponsesCompressed +
			", avg. response compression ratio: " + getResponseAverageCompressionRatio() +
			", requests compressed: " + numRequestsCompressed +
			", avg. request compression ratio: " + getRequestAverageCompressionRatio() + ']';
	}

	OutputStatsCallback getOutputStatsCallback(final StatsField field) {
		switch (field.value) {
			case StatsField.RESPONSE_INPUT_BYTES:
				return responseInputStatsCallback;
			case StatsField.RESPONSE_COMPRESSED_BYTES:
				return responseCompressedStatsCallback;
			default:
				throw new IllegalArgumentException();
		}
	}

	InputStatsCallback getInputStatsCallback(final StatsField field) {
		switch (field.value) {
			case StatsField.REQUEST_INPUT_BYTES:
				return requestInputStatsCallback;
			case StatsField.REQUEST_COMPRESSED_BYTES:
				return requestCompressedStatsCallback;
			default:
				throw new IllegalArgumentException();
		}
	}

	final class OutputStatsCallback implements StatsOutputStream.StatsCallback, Serializable {

		private static final long serialVersionUID = -4483355731273629325L;

		/** @serial */
		private final StatsField field;

		OutputStatsCallback(final StatsField field) {
			this.field = field;
		}

		public void bytesWritten(final int numBytes) {
            Compat.assertion(numBytes >= 0);
			switch (field.value) {
				case StatsField.RESPONSE_INPUT_BYTES:
					responseInputBytes += numBytes;
					break;
				case StatsField.RESPONSE_COMPRESSED_BYTES:
					responseCompressedBytes += numBytes;
					break;
				default:
					throw new IllegalStateException();
			}
		}

		public String toString() {
			return "OutputStatsCallback[field: " + field + ']';
		}
	}

	final class InputStatsCallback implements StatsInputStream.StatsCallback, Serializable {

		private static final long serialVersionUID = 8205059279453932247L;

		/** @serial */
		private final StatsField field;

		InputStatsCallback(final StatsField field) {
			this.field = field;
		}

		public void bytesRead(final int numBytes) {
            Compat.assertion(numBytes >= 0);
			switch (field.value) {
				case StatsField.REQUEST_INPUT_BYTES:
					requestInputBytes += numBytes;
					break;
				case StatsField.REQUEST_COMPRESSED_BYTES:
					requestCompressedBytes += numBytes;
					break;
				default:
					throw new IllegalStateException();
			}
		}

		public String toString() {
			return "InputStatsCallback[field: " + field + ']';
		}
	}

	/**
	 * <p>A simple enum used by {@link OutputStatsCallback} to select a field in this class. This is getting
	 * a little messy but somehow better than defining a bunch of inner classes?</p>
	 *
	 * @since 1.6
	 */
	final static class StatsField implements Serializable {
        private final static String [] text = new String [] {
            "RESPONSE_INPUT_BYTES",
            "RESPONSE_COMPRESSED_BYTES",
            "REQUEST_INPUT_BYTES",
            "REQUEST_COMPRESSED_BYTES"
        };

        public final static int RESPONSE_INPUT_BYTES = 0;
        public final static int RESPONSE_COMPRESSED_BYTES = 1;
        public final static int REQUEST_INPUT_BYTES = 2;
        public final static int REQUEST_COMPRESSED_BYTES = 3;

        public final static StatsField O_RESPONSE_INPUT_BYTES = new StatsField(RESPONSE_INPUT_BYTES);
        public final static StatsField O_RESPONSE_COMPRESSED_BYTES = new StatsField(RESPONSE_COMPRESSED_BYTES);
        public final static StatsField O_REQUEST_INPUT_BYTES = new StatsField(REQUEST_INPUT_BYTES);
        public final static StatsField O_REQUEST_COMPRESSED_BYTES = new StatsField(RESPONSE_COMPRESSED_BYTES);

        public final int value;
        
        public StatsField(int value) {
            this.value = value;
        }
        
        public String toString() {
            return text[this.value];
        }
	}

}
