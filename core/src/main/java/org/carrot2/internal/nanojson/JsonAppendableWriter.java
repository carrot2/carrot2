/*
 * Copyright 2011 The nanojson Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.carrot2.internal.nanojson;

import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;

//@formatter:off
/**
 * JSON writer that emits JSON to a {@link Appendable}.
 * 
 * Create this class with {@link JsonWriter#on(Appendable)} or
 * {@link JsonWriter#on(OutputStream)}.
 * 
 * <pre>
 * OutputStream out = ...;
 * JsonWriter
 *     .indent("  ")
 *     .on(out)
 *     .object()
 *         .array("a")
 *             .value(1)
 *             .value(2)
 *         .end()
 *         .value("b", false)
 *         .value("c", true)
 *     .end()
 * .done();
 * </pre>
 */
// @formatter:on
public final class JsonAppendableWriter extends
		JsonWriterBase<JsonAppendableWriter> implements
		JsonSink<JsonAppendableWriter> {
	JsonAppendableWriter(Appendable appendable, String indent) {
		super(appendable, indent);
	}

	JsonAppendableWriter(OutputStream out, String indent) {
		super(out, indent);
	}

	/**
	 * Closes this JSON writer and flushes the underlying {@link Appendable} if
	 * it is also {@link Flushable}.
	 * 
	 * @throws JsonWriterException
	 *             if the underlying {@link Flushable} {@link Appendable} failed
	 *             to flush.
	 */
	public void done() throws JsonWriterException {
		super.doneInternal();
		try {
			if (appendable instanceof Flushable)
				((Flushable) appendable).flush();
			else if (out != null)
				out.flush();
		} catch (IOException e) {
			throw new JsonWriterException(e);
		}
	}
}
