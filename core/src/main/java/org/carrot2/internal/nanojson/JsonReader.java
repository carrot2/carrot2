package org.carrot2.internal.nanojson;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Streaming reader for JSON documents.
 */
public final class JsonReader {
	private JsonTokener tokener;
	private int token;
	private BitSet states = new BitSet();
	private int stateIndex = 0;
	private boolean inObject;
	private boolean first = true;
	private StringBuilder key = new StringBuilder();

	/**
	 * The type of value that the {@link JsonReader} is positioned over.
	 */
	public enum Type {
		/**
		 * An object.
		 */
		OBJECT,
		/**
		 * An array.
		 */
		ARRAY,
		/**
		 * A string.
		 */
		STRING,
		/**
		 * A number.
		 */
		NUMBER,
		/**
		 * A boolean value (true or false).
		 */
		BOOLEAN,
		/**
		 * A null value.
		 */
		NULL,
	};

	/**
	 * Create a {@link JsonReader} from a {@link String}.
	 */
	public static JsonReader from(String s) throws JsonParserException {
		return from(new StringReader(s));
	}

	/**
	 * Create a {@link JsonReader} from a {@link Reader}.
	 */
	public static JsonReader from(Reader reader) throws JsonParserException {
		return new JsonReader(new JsonTokener(reader));
	}

	/**
	 * Internal constructor.
	 */
	JsonReader(JsonTokener tokener) throws JsonParserException {
		this.tokener = tokener;
		token = tokener.advanceToToken();
	}

	/**
	 * Returns to the array or object structure above the current one, and
	 * advances to the next key or value.
	 */
	public boolean pop() throws JsonParserException {
		// CHECKSTYLE_OFF: EmptyStatement
		while (!next());
		// CHECKSTYLE_ON: EmptyStatement
		first = false;
		inObject = states.get(--stateIndex);
		return token != JsonTokener.TOKEN_EOF;
	}

	/**
	 * Returns the current type of the value.
	 */
	public Type current() throws JsonParserException {
		switch (token) {
		case JsonTokener.TOKEN_TRUE:
		case JsonTokener.TOKEN_FALSE:
			return Type.BOOLEAN;
		case JsonTokener.TOKEN_NULL:
			return Type.NULL;
		case JsonTokener.TOKEN_NUMBER:
			return Type.NUMBER;
		case JsonTokener.TOKEN_STRING:
			return Type.STRING;
		case JsonTokener.TOKEN_OBJECT_START:
			return Type.OBJECT;
		case JsonTokener.TOKEN_ARRAY_START:
			return Type.ARRAY;
		default:				
			throw createTokenMismatchException(JsonTokener.TOKEN_NULL, JsonTokener.TOKEN_TRUE, 
					JsonTokener.TOKEN_FALSE, JsonTokener.TOKEN_NUMBER, JsonTokener.TOKEN_STRING,
					JsonTokener.TOKEN_OBJECT_START, JsonTokener.TOKEN_ARRAY_START);
		}
	}

	/**
	 * Starts reading an object at the current value.
	 */
	public void object() throws JsonParserException {
		if (token != JsonTokener.TOKEN_OBJECT_START)
			throw createTokenMismatchException(JsonTokener.TOKEN_OBJECT_START);
		states.set(stateIndex++, inObject);
		inObject = true;
		first = true;
	}

	/**
	 * Reads the key for the object at the current value. Does not advance to the next value.
	 */
	public String key() throws JsonParserException {
		if (!inObject)
			throw tokener.createParseException(null, "Not reading an object", true);
		return key.toString();
	}

	/**
	 * Starts reading an array at the current value.
	 */
	public void array() throws JsonParserException {
		if (token != JsonTokener.TOKEN_ARRAY_START)
			throw createTokenMismatchException(JsonTokener.TOKEN_ARRAY_START);
		states.set(stateIndex++, inObject);
		inObject = false;
		first = true;
	}

	/**
	 * Returns the current value.
	 */
	public Object value() throws JsonParserException {
		switch (token) {
		case JsonTokener.TOKEN_TRUE:
			return true;
		case JsonTokener.TOKEN_FALSE:
			return false;
		case JsonTokener.TOKEN_NULL:
			return null;
		case JsonTokener.TOKEN_NUMBER:
			return number();
		case JsonTokener.TOKEN_STRING:
			return string();
		default:				
			throw createTokenMismatchException(JsonTokener.TOKEN_NULL, JsonTokener.TOKEN_TRUE, JsonTokener.TOKEN_FALSE,
					JsonTokener.TOKEN_NUMBER, JsonTokener.TOKEN_STRING);
		}
	}

	/**
	 * Parses the current value as a null.
	 */
	public void nul() throws JsonParserException {
		if (token != JsonTokener.TOKEN_NULL)
			throw createTokenMismatchException(JsonTokener.TOKEN_NULL);
	}

	/**
	 * Parses the current value as a string.
	 */
	public String string() throws JsonParserException {
		if (token == JsonTokener.TOKEN_NULL)
			return null;
		if (token != JsonTokener.TOKEN_STRING)
			throw createTokenMismatchException(JsonTokener.TOKEN_NULL, JsonTokener.TOKEN_STRING);
		return tokener.reusableBuffer.toString();
	}

	/**
	 * Parses the current value as a boolean.
	 */
	public boolean bool() throws JsonParserException {
		if (token == JsonTokener.TOKEN_TRUE)
			return true;
		else if (token == JsonTokener.TOKEN_FALSE)
			return false;
		else
			throw createTokenMismatchException(JsonTokener.TOKEN_TRUE, JsonTokener.TOKEN_FALSE);
	}

	/**
	 * Parses the current value as a {@link Number}.
	 */
	public Number number() throws JsonParserException {
		if (token == JsonTokener.TOKEN_NULL)
			return null;
		return new JsonLazyNumber(tokener.reusableBuffer.toString(), tokener.isDouble);
	}

	/**
	 * Parses the current value as a long.
	 */
	public long longVal() throws JsonParserException {
		String s = tokener.reusableBuffer.toString();
		return tokener.isDouble ? (long)Double.parseDouble(s) : Long.parseLong(s);
	}

	/**
	 * Parses the current value as an integer.
	 */
	public int intVal() throws JsonParserException {
		String s = tokener.reusableBuffer.toString();
		return tokener.isDouble ? (int)Double.parseDouble(s) : Integer.parseInt(s);
	}

	/**
	 * Parses the current value as a float.
	 */
	public float floatVal() throws JsonParserException {
		String s = tokener.reusableBuffer.toString();
		return Float.parseFloat(s);
	}

	/**
	 * Parses the current value as a double.
	 */
	public double doubleVal() throws JsonParserException {
		String s = tokener.reusableBuffer.toString();
		return Double.parseDouble(s);
	}

	/**
	 * Advance to the next value in this array or object. If no values remain,
	 * return to the parent array or object.
	 * 
	 * @return true if we still have values to read in this array or object,
	 *         false if we have completed this object (and implicitly moved back
	 *         to the parent array or object)
	 */
	public boolean next() throws JsonParserException {
		if (stateIndex == 0) {
			throw tokener.createParseException(null, "Unabled to call next() at the root", true); 
		}
		
		token = tokener.advanceToToken();

		if (inObject) {
			if (token == JsonTokener.TOKEN_OBJECT_END) {
				inObject = states.get(--stateIndex);
				first = false;
				return false;
			}
			
			if (!first) {
				if (token != JsonTokener.TOKEN_COMMA)
					throw createTokenMismatchException(JsonTokener.TOKEN_COMMA, JsonTokener.TOKEN_OBJECT_END);
				token = tokener.advanceToToken();
			}

			if (token != JsonTokener.TOKEN_STRING)
				throw createTokenMismatchException(JsonTokener.TOKEN_STRING);
			key.setLength(0);
			key.append(tokener.reusableBuffer); // reduce string garbage 
			if ((token = tokener.advanceToToken()) != JsonTokener.TOKEN_COLON)
				throw createTokenMismatchException(JsonTokener.TOKEN_COLON);
			token = tokener.advanceToToken();
		} else {
			if (token == JsonTokener.TOKEN_ARRAY_END) {
				inObject = states.get(--stateIndex);
				first = false;
				return false;
			}
			if (!first) {
				if (token != JsonTokener.TOKEN_COMMA)
					throw createTokenMismatchException(JsonTokener.TOKEN_COMMA, JsonTokener.TOKEN_ARRAY_END);
				token = tokener.advanceToToken();
			}
		}

		if (token != JsonTokener.TOKEN_NULL && token != JsonTokener.TOKEN_STRING
				&& token != JsonTokener.TOKEN_NUMBER && token != JsonTokener.TOKEN_TRUE
				&& token != JsonTokener.TOKEN_FALSE && token != JsonTokener.TOKEN_OBJECT_START
				&& token != JsonTokener.TOKEN_ARRAY_START)
			throw createTokenMismatchException(JsonTokener.TOKEN_NULL, JsonTokener.TOKEN_STRING,
					JsonTokener.TOKEN_NUMBER, JsonTokener.TOKEN_TRUE, JsonTokener.TOKEN_FALSE,
					JsonTokener.TOKEN_OBJECT_START, JsonTokener.TOKEN_ARRAY_START);

		first = false;
		
		return true;
	}
	
	private JsonParserException createTokenMismatchException(int... t) {
		return tokener.createParseException(null, "token mismatch (expected " + Arrays.toString(t)
						+ ", was " + token + ")",
				true);
	}
}
