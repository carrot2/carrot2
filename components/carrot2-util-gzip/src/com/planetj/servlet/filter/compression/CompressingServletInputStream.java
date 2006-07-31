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
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Implementation of {@link javax.servlet.ServletInputStream} which will decompress data read from it.</p>
 *
 * @author Sean Owen
 * @since 1.6
 */
final class CompressingServletInputStream extends ServletInputStream {

	private final InputStream compressingStream;
	private boolean closed;

	CompressingServletInputStream(final InputStream rawStream,
	                              final CompressingStreamFactory compressingStreamFactory,
	                              final CompressingFilterContext context) throws IOException {
		this.compressingStream =
			compressingStreamFactory.getCompressingStream(rawStream, context).getCompressingInputStream();
	}

	public int read() throws IOException {
		checkClosed();
		return compressingStream.read();
	}

	public int read(final byte[] b) throws IOException {
		checkClosed();
		return compressingStream.read(b);
	}

	public int read(final byte[] b, final int offset, final int length) throws IOException {
		checkClosed();
		return compressingStream.read(b, offset, length);
	}

	// Leave implementation of readLine() in superclass alone, even if it's not so efficient

	public long skip(final long n) throws IOException {
		checkClosed();
		return compressingStream.skip(n);
	}

	public int available() throws IOException {
		checkClosed();
		return compressingStream.available();
	}

	public void close() throws IOException {
		if (!closed) {
			compressingStream.close();
			closed = true;
		}
	}

	public synchronized void mark(final int readlimit) {
		checkClosed();
		compressingStream.mark(readlimit);
	}

	public synchronized void reset() throws IOException {
		checkClosed();
		compressingStream.reset();
	}

	public boolean markSupported() {
		checkClosed();
		return compressingStream.markSupported();
	}

	private void checkClosed() {
		if (closed) {
			throw new IllegalStateException("Stream is already closed");
		}
	}

	public String toString() {
		return "CompressingServletInputStream";
	}

}
