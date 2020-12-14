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

import java.util.Collection;
import java.util.Map;
import java.util.Stack;

/**
 * Builds a {@link JsonObject} or {@link JsonArray}.
 * 
 * @param <T>
 *            The type of JSON object to build.
 */
public final class JsonBuilder<T> implements JsonSink<JsonBuilder<T>> {
	private Stack<Object> json = new Stack<Object>();
	private T root;

	JsonBuilder(T root) {
		this.root = root;
		json.push(root);
	}

	/**
	 * Completes this builder, closing any unclosed objects and returns the built object.
	 */
	public T done() {
		return root;
	}
	
	@Override
	public JsonBuilder<T> array(Collection<?> c) {
		return value(c);
	}

	@Override
	public JsonBuilder<T> array(String key, Collection<?> c) {
		return value(key, c);
	}

	@Override
	public JsonBuilder<T> object(Map<?, ?> map) {
		return value(map);
	}

	@Override
	public JsonBuilder<T> object(String key, Map<?, ?> map) {
		return value(key, map);
	}

	@Override
	public JsonBuilder<T> nul() {
		return value((Object)null);
	}

	@Override
	public JsonBuilder<T> nul(String key) {
		return value(key, (Object)null);
	}

	@Override
	public JsonBuilder<T> value(Object o) {
		arr().add(o);
		return this;
	}

	@Override
	public JsonBuilder<T> value(String key, Object o) {
		obj().put(key, o);
		return this;
	}

	@Override
	public JsonBuilder<T> value(String s) {
		return value((Object)s);
	}

	@Override
	public JsonBuilder<T> value(int i) {
		return value((Object)i);
	}

	@Override
	public JsonBuilder<T> value(long l) {
		return value((Object)l);
	}

	@Override
	public JsonBuilder<T> value(boolean b) {
		return value((Object)b);
	}

	@Override
	public JsonBuilder<T> value(double d) {
		return value((Object)d);
	}

	@Override
	public JsonBuilder<T> value(float f) {
		return value((Object)f);
	}

	@Override
	public JsonBuilder<T> value(Number n) {
		return value((Object)n);
	}

	@Override
	public JsonBuilder<T> value(String key, String s) {
		return value(key, (Object)s);
	}

	@Override
	public JsonBuilder<T> value(String key, int i) {
		return value(key, (Object)i);
	}

	@Override
	public JsonBuilder<T> value(String key, long l) {
		return value(key, (Object)l);
	}

	@Override
	public JsonBuilder<T> value(String key, boolean b) {
		return value(key, (Object)b);
	}

	@Override
	public JsonBuilder<T> value(String key, double d) {
		return value(key, (Object)d);
	}

	@Override
	public JsonBuilder<T> value(String key, float f) {
		return value(key, (Object)f);
	}

	@Override
	public JsonBuilder<T> value(String key, Number n) {
		return value(key, (Object)n);
	}

	@Override
	public JsonBuilder<T> array() {
		JsonArray a = new JsonArray();
		value(a);
		json.push(a);
		return this;
	}

	@Override
	public JsonBuilder<T> object() {
		JsonObject o = new JsonObject();
		value(o);
		json.push(o);
		return this;
	}

	@Override
	public JsonBuilder<T> array(String key) {
		JsonArray a = new JsonArray();
		value(key, a);
		json.push(a);
		return this;
	}

	@Override
	public JsonBuilder<T> object(String key) {
		JsonObject o = new JsonObject();
		value(key, o);
		json.push(o);
		return this;
	}

	@Override
	public JsonBuilder<T> end() {
		if (json.size() == 1)
			throw new JsonWriterException("Cannot end the root object or array");
		json.pop();
		return this;
	}

	private JsonObject obj() {
		try {
			return (JsonObject)json.peek();
		} catch (ClassCastException e) {
			throw new JsonWriterException("Attempted to write a keyed value to a JsonArray");
		}
	}
	
	private JsonArray arr() {
		try {
			return (JsonArray)json.peek();
		} catch (ClassCastException e) {
			throw new JsonWriterException("Attempted to write a non-keyed value to a JsonObject");
		}
	}
}
