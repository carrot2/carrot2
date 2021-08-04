/**
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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

//@formatter:off
/**
 * Factory for JSON writers that target {@link String}s and {@link Appendable}s. 
 * 
 * Creates writers that write JSON to a {@link String}, an {@link OutputStream}, or an
 * {@link Appendable} such as a {@link StringBuilder}, a {@link Writer} a {@link PrintStream} or a {@link CharBuffer}.
 * 
 * <pre>
 * String json = JsonWriter
 *     .indent("  ")
 *     .string()
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
//@formatter:on
public final class JsonWriter {
	private JsonWriter() {
	}

	/**
	 * Allows for additional configuration of the {@link JsonWriter}.
	 */
	public static final class JsonWriterContext {
		private String indent;

		private JsonWriterContext(String indent) {
			this.indent = indent;
		}

		//@formatter:off
		/**
		 * Creates a new {@link JsonStringWriter}.
		 *
         * <pre>
		 * String json = JsonWriter.indent("  ").string()
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
		//@formatter:on
		public JsonStringWriter string() {
			return new JsonStringWriter(indent);
		}

		/**
		 * Creates a {@link JsonAppendableWriter} that can output to an
		 * {@link Appendable} subclass, such as a {@link StringBuilder}, a
		 * {@link Writer} a {@link PrintStream} or a {@link CharBuffer}.
		 */
		public JsonAppendableWriter on(Appendable appendable) {
			return new JsonAppendableWriter(appendable, indent);
		}

		//@formatter:off
		/**
		 * Creates a {@link JsonAppendableWriter} that can output to an {@link PrintStream} subclass.
		 * 
		 * <pre>
		 * JsonWriter.indent("  ").on(System.out)
		 * 		.object()
		 * 			.value(&quot;a&quot;, 1)
		 * 			.value(&quot;b&quot;, 2)
		 * 		.end()
		 * 	.done();
		 * </pre>
		 */
		//@formatter:on
		public JsonAppendableWriter on(PrintStream appendable) {
			return new JsonAppendableWriter((Appendable)appendable, indent);
		}

		//@formatter:off
		/**
		 * Creates a {@link JsonAppendableWriter} that can output to an {@link OutputStream} subclass. Uses the UTF-8
		 * {@link Charset}. To specify a different charset, use the {@link JsonWriter#on(Appendable)} method with an
		 * {@link OutputStreamWriter}.
		 * 
		 * <pre>
		 * JsonWriter.indent("  ").on(System.out)
		 * 		.object()
		 * 			.value(&quot;a&quot;, 1)
		 * 			.value(&quot;b&quot;, 2)
		 * 		.end()
		 * 	.done();
		 * </pre>
		 */
		//@formatter:on
		public JsonAppendableWriter on(OutputStream out) {
			return new JsonAppendableWriter(new OutputStreamWriter(out,
					StandardCharsets.UTF_8), indent);
		}

	}

	//@formatter:off
	/**
	 * Creates a {@link JsonWriter} source that will write indented output with the given indent.
	 * 
	 * <pre>
	 * String json = JsonWriter.indent("  ").string()
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
	//@formatter:on
	public static JsonWriter.JsonWriterContext indent(String indent) {
		if (indent == null) {
			throw new IllegalArgumentException("indent must be non-null");
		}

		for (int i = 0; i < indent.length(); i++) {
			if (indent.charAt(i) != ' ' && indent.charAt(i) != '\t') {
				throw new IllegalArgumentException("Only tabs and spaces are allowed for indent.");
			}
		}

		return new JsonWriterContext(indent);
	}

	//@formatter:off
	/**
	 * Creates a new {@link JsonStringWriter}.
	 * 
     * <pre>
	 * String json = JsonWriter.string()
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
	//@formatter:on
	public static JsonStringWriter string() {
		return new JsonStringWriter(null);
	}

	/**
	 * Emits a single value (a JSON primitive such as a {@link Number},
	 * {@link Boolean}, {@link String}, a {@link Map} or {@link JsonObject}, or
	 * a {@link Collection} or {@link JsonArray}.
	 * 
	 * Emit a {@link String}, JSON-escaped:
	 * 
	 * <pre>
	 * JsonWriter.string(&quot;abc\n\&quot;&quot;) // &quot;\&quot;abc\\n\\&quot;\&quot;&quot;
	 * </pre>
	 * 
	 * <pre>
	 * JsonObject obj = new JsonObject();
	 * obj.put("abc", 1);
	 * JsonWriter.string(obj) // "{\"abc\":1}"
	 * </pre>
	 */
	public static String string(Object value) {
		return new JsonStringWriter(null).value(value).done();
	}

	/**
	 * Creates a {@link JsonAppendableWriter} that can output to an
	 * {@link Appendable} subclass, such as a {@link StringBuilder}, a
	 * {@link Writer} a {@link PrintStream} or a {@link CharBuffer}.
	 */
	public static JsonAppendableWriter on(Appendable appendable) {
		return new JsonAppendableWriter(appendable, null);
	}

	//@formatter:off
	/**
	 * Creates a {@link JsonAppendableWriter} that can output to an {@link PrintStream} subclass.
	 * 
	 * <pre>
	 * JsonWriter.on(System.out)
	 * 		.object()
	 * 			.value(&quot;a&quot;, 1)
	 * 			.value(&quot;b&quot;, 2)
	 * 		.end()
	 * 	.done();
	 * </pre>
	 */
	//@formatter:on
	public static JsonAppendableWriter on(PrintStream appendable) {
		return new JsonAppendableWriter((Appendable)appendable, null);
	}

	//@formatter:off
	/**
	 * Creates a {@link JsonAppendableWriter} that can output to an {@link OutputStream} subclass. Uses the UTF-8
	 * {@link Charset}. To specify a different charset, use the {@link JsonWriter#on(Appendable)} method with an
	 * {@link OutputStreamWriter}.
	 * 
	 * <pre>
	 * JsonWriter.on(System.out)
	 * 		.object()
	 * 			.value(&quot;a&quot;, 1)
	 * 			.value(&quot;b&quot;, 2)
	 * 		.end()
	 * 	.done();
	 * </pre>
	 */
	//@formatter:on
	public static JsonAppendableWriter on(OutputStream out) {
		return new JsonAppendableWriter(out, null);
	}

	/**
	 * Escape a string value.
	 * 
	 * @param value The input string value.
	 * @return the escaped JSON value
	 */
	public static String escape(String value) {
		String s = string(value);
		return s.substring(1, s.length() - 1);
	}
}
