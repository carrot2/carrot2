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

import java.io.IOException;
import java.io.OutputStream;

/**
 * An {@link OutputStream} that decorates another {@link OutputStream} and notes when bytes are written to the stream.
 * Callers create an instance of {@link StatsOutputStream} with an instance of {@link StatsCallback}, which receives
 * notification of writes. This information might be used to tally the number of bytes written to a stream.
 *
 * @author Sean Owen
 */
final class StatsOutputStream extends OutputStream {

	private final OutputStream outputStream;
	private final StatsCallback statsCallback;

	StatsOutputStream(final OutputStream outputStream, final StatsCallback statsCallback) {
        Compat.assertion(outputStream != null && statsCallback != null);
		this.outputStream = outputStream;
		this.statsCallback = statsCallback;
	}

	public void write(final int b) throws IOException {
		outputStream.write(b);
		statsCallback.bytesWritten(1);
	}

	public void write(final byte[] b) throws IOException {
		outputStream.write(b);
		if (b != null && b.length > 0) {
			statsCallback.bytesWritten(b.length);
		}
	}

	public void write(final byte[] b, final int off, final int len) throws IOException {
		outputStream.write(b, off, len);
		if (len > 0) {
			statsCallback.bytesWritten(len);
		}
	}

	public void flush() throws IOException {
		outputStream.flush();
	}

	public void close() throws IOException {
		outputStream.close();
	}

	public String toString() {
		return "StatsOutputStream[" + String.valueOf(outputStream) + ']';
	}


	static interface StatsCallback {

		void bytesWritten(int numBytes);
	}

}