package org.carrot2.internal.nanojson;

import java.util.Collection;
import java.util.Map;

/**
 * Common interface for {@link JsonAppendableWriter}, {@link JsonStringWriter} and {@link JsonBuilder}.
 * 
 * @param <SELF>
 *            A subclass of {@link JsonSink}.
 */
public interface JsonSink<SELF extends JsonSink<SELF>> {
	/**
	 * Emits the start of an array.
	 */
	SELF array(Collection<?> c);

	/**
	 * Emits the start of an array with a key.
	 */
	SELF array(String key, Collection<?> c);
	
	/**
	 * Emits the start of an object.
	 */
	SELF object(Map<?, ?> map);

	/**
	 * Emits the start of an object with a key.
	 */
	SELF object(String key, Map<?, ?> map);

	/**
	 * Emits a 'null' token.
	 */
	SELF nul();

	/**
	 * Emits a 'null' token with a key.
	 */
	SELF nul(String key);

	/**
	 * Emits an object if it is a JSON-compatible type, otherwise throws an exception.
	 */
	SELF value(Object o);

	/**
	 * Emits an object with a key if it is a JSON-compatible type, otherwise throws an exception.
	 */
	SELF value(String key, Object o);

	/**
	 * Emits a string value (or null).
	 */
	SELF value(String s);

	/**
	 * Emits an integer value.
	 */
	SELF value(int i);

	/**
	 * Emits a long value.
	 */
	SELF value(long l);

	/**
	 * Emits a boolean value.
	 */
	SELF value(boolean b);

	/**
	 * Emits a double value.
	 */
	SELF value(double d);

	/**
	 * Emits a float value.
	 */
	SELF value(float f);

	/**
	 * Emits a {@link Number} value.
	 */
	SELF value(Number n);

	/**
	 * Emits a string value (or null) with a key.
	 */
	SELF value(String key, String s);

	/**
	 * Emits an integer value with a key.
	 */
	SELF value(String key, int i);
	
	/**
	 * Emits a long value with a key.
	 */
	SELF value(String key, long l);

	/**
	 * Emits a boolean value with a key.
	 */
	SELF value(String key, boolean b);

	/**
	 * Emits a double value with a key.
	 */
	SELF value(String key, double d);

	/**
	 * Emits a float value with a key.
	 */
	SELF value(String key, float f);

	/**
	 * Emits a {@link Number} value with a key.
	 */
	SELF value(String key, Number n);

	/**
	 * Starts an array.
	 */
	SELF array();

	/**
	 * Starts an object.
	 */
	SELF object();

	/**
	 * Starts an array within an object, prefixed with a key.
	 */
	SELF array(String key);

	/**
	 * Starts an object within an object, prefixed with a key.
	 */
	SELF object(String key);

	/**
	 * Ends the current array or object.
	 */
	SELF end();
}
