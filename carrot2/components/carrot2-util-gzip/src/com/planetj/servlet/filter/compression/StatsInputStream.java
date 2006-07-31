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

import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link InputStream} that decorates another {@link InputStream} and notes when bytes are read from the stream.
 * Callers create an instance of {@link StatsInputStream} with an instance of {@link StatsInputStream.StatsCallback},
 * which receives notification of reads. This information might be used to tally the number of bytes
 * read from a stream.
 *
 * @author Sean Owen
 * @since 1.6
 */
final class StatsInputStream extends InputStream {

	private final InputStream inputStream;
	private final StatsInputStream.StatsCallback statsCallback;

	StatsInputStream(final InputStream inputStream, final StatsInputStream.StatsCallback statsCallback) {
        Compat.assertion(inputStream != null && statsCallback != null);
		this.inputStream = inputStream;
		this.statsCallback = statsCallback;
	}

	public int read() throws IOException {
		final int result = inputStream.read();
		if (result >= 0) {
			// here, result is the byte read, or -1 if EOF
			statsCallback.bytesRead(1);
		}
		return result;
	}

	public int read(final byte[] b) throws IOException {
		final int result = inputStream.read(b);
		if (result >= 0) {
			// here, result is number of bytes read
			statsCallback.bytesRead(result);
		}
		return result;
	}

	public int read(final byte[] b, final int offset, final int length) throws IOException {
		final int result = inputStream.read(b, offset, length);
		if (result >= 0) {
			// here, result is number of bytes read			
			statsCallback.bytesRead(result);
		}
		return result;
	}

	// Leave implementation of readLine() in superclass alone, even if it's not so efficient

	public long skip(final long n) throws IOException {
		return inputStream.skip(n);
	}

	public int available() throws IOException {
		return inputStream.available();
	}

	public void close() throws IOException {
		inputStream.close();
	}

	public synchronized void mark(final int readlimit) {
		inputStream.mark(readlimit);
	}

	public synchronized void reset() throws IOException {
		inputStream.reset();
	}

	public boolean markSupported() {
		return inputStream.markSupported();
	}

	public String toString() {
		return "StatsInputStream[" + String.valueOf(inputStream) + ']';
	}


	static interface StatsCallback {
		void bytesRead(int numBytes);
	}

}
