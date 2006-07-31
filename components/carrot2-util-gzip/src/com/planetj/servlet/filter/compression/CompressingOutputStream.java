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
 * Implementations of this interface encapsulate an {@link OutputStream} that compresses data written to it. This
 * includes the compressing {@link OutputStream} itself (see {@link #getCompressingOutputStream()}), and the ability to
 * tell the stream that no more data will be written, so that the stream may write any trailing data needed by the
 * compression algorithm (see {@link #finish()}).
 *
 * @author Sean Owen
 */
interface CompressingOutputStream {

	OutputStream getCompressingOutputStream();

	void finish() throws IOException;

}
