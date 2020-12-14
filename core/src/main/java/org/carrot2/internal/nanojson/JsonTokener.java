package org.carrot2.internal.nanojson;

import java.io.IOException;
import java.io.Reader;

/**
 * Internal class for tokenizing JSON. Used by both {@link JsonParser} and {@link JsonReader}.
 */
final class JsonTokener {
	// Used by tests
	static final int BUFFER_SIZE = 32 * 1024;

	static final int BUFFER_ROOM = 256;
	static final int MAX_ESCAPE = 5; // uXXXX (don't need the leading slash)

	private int linePos = 1, rowPos, charOffset, utf8adjust;
	private int tokenCharPos, tokenCharOffset;

	private boolean eof;
	private int index;
	private final Reader reader;
	private final char[] buffer = new char[BUFFER_SIZE];
	private int bufferLength;

	private final boolean utf8 = false;

	protected StringBuilder reusableBuffer = new StringBuilder();
	protected boolean isDouble;

	static final char[] TRUE = { 'r', 'u', 'e' };
	static final char[] FALSE = { 'a', 'l', 's', 'e' };
	static final char[] NULL = { 'u', 'l', 'l' };

	static final int TOKEN_EOF = 0;
	static final int TOKEN_COMMA = 1;
	static final int TOKEN_COLON = 2;
	static final int TOKEN_OBJECT_END = 3;
	static final int TOKEN_ARRAY_END = 4;
	static final int TOKEN_NULL = 5;
	static final int TOKEN_TRUE = 6;
	static final int TOKEN_FALSE = 7;
	static final int TOKEN_STRING = 8;
	static final int TOKEN_NUMBER = 9;
	static final int TOKEN_OBJECT_START = 10;
	static final int TOKEN_ARRAY_START = 11;
	static final int TOKEN_VALUE_MIN = TOKEN_NULL;

	
	JsonTokener(Reader reader) throws JsonParserException {
		this.reader = reader;
		init();
	}

	private void init() throws JsonParserException {
		eof = refillBuffer();
		consumeWhitespace();
	}

	/**
	 * Expects a given string at the current position.
	 */
	void consumeKeyword(char first, char[] expected) throws JsonParserException {
		if (ensureBuffer(expected.length) < expected.length) {
			throw createHelpfulException(first, expected, 0);
		}

		for (int i = 0; i < expected.length; i++)
			if (buffer[index++] != expected[i])
				throw createHelpfulException(first, expected, i);

		fixupAfterRawBufferRead();

		// The token should end with something other than an ASCII letter
		if (isAsciiLetter(peekChar()))
			throw createHelpfulException(first, expected, expected.length);
	}

	/**
	 * Steps through to the end of the current number token (a non-digit token).
	 */
	void consumeTokenNumber(char savedChar) throws JsonParserException {
		reusableBuffer.setLength(0);
		reusableBuffer.append(savedChar);
		isDouble = false;

		// The JSON spec is way stricter about number formats than
		// Double.parseDouble(). This is a hand-rolled pseudo-parser that
		// verifies numbers we read.
		int state;
		if (savedChar == '-') {
			state = 1;
		} else if (savedChar == '0') {
			state = 3;
		} else {
			state = 2;
		}
		
		outer: while (true) {
			int n = ensureBuffer(BUFFER_ROOM);
			if (n == 0)
				break outer;

			for (int i = 0; i < n; i++) {
				char nc = buffer[index];
				if (!isDigitCharacter(nc))
					break outer;

				int ns = -1;
				sw:
				switch (state) {
				case 1: // start leading negative
					if (nc == '-' && state == 0) {
						ns = 1; break sw;
					}
					if (nc == '0') {
						ns = 3; break sw;
					}
					if (nc >= '0' && nc <= '9') {
						ns = 2; break sw;
					}
					break;
				case 2: // no leading zero
				case 3: // leading zero
					if ((nc >= '0' && nc <= '9') && state == 2) {
						ns = 2; break sw;
					}
					if (nc == '.') {
						isDouble = true;
						ns = 4; break sw;
					}
					if (nc == 'e' || nc == 'E') {
						isDouble = true;
						ns = 6; break sw;
					}
					break;
				case 4: // after period
				case 5: // after period, one digit read
					if (nc >= '0' && nc <= '9') {
						ns = 5; break sw;
					}
					if ((nc == 'e' || nc == 'E') && state == 5) {
						isDouble = true;
						ns = 6; break sw;
					}
					break;
				case 6: // after exponent
				case 7: // after exponent and sign
					if (nc == '+' || nc == '-' && state == 6) {
						ns = 7; break sw;
					}
					if (nc >= '0' && nc <= '9') {
						ns = 8; break sw;
					}
					break;
				case 8: // after digits
					if (nc >= '0' && nc <= '9') {
						ns = 8; break sw;
					}
					break;
				default:
					assert false : "Impossible"; // will throw malformed number
				}
				reusableBuffer.append(nc);
				index++;
				if (ns == -1)
					throw createParseException(null, "Malformed number: " + reusableBuffer, true);
				state = ns;
			}
		}
		
		if (state != 2 && state != 3 && state != 5 && state != 8)
			throw createParseException(null, "Malformed number: " + reusableBuffer, true);
		
		// Special case for -0
		if (state == 3 && savedChar == '-')
			isDouble = true;
		
		fixupAfterRawBufferRead();
	}

	/**
	 * Steps through to the end of the current string token (the unescaped double quote).
	 */
	void consumeTokenString() throws JsonParserException {
		reusableBuffer.setLength(0);
		
		// Assume no escapes or UTF-8 in the string to start (fast path)
		start:
		while (true) {
			int n = ensureBuffer(BUFFER_ROOM);
			if (n == 0)
				throw createParseException(null, "String was not terminated before end of input", true);
			
			for (int i = 0; i < n; i++) {
				char c = stringChar();
				if (c == '"') {
					// Use the index before we fixup
					reusableBuffer.append(buffer, index - i - 1, i);
					fixupAfterRawBufferRead();
					return;
				}
				if (c == '\\' || (utf8 && (c & 0x80) != 0)) {
					reusableBuffer.append(buffer, index - i - 1, i);
					index--;
					break start;
				}
			}
			
			reusableBuffer.append(buffer, index - n, n);
		}
		
		outer: while (true) {
			int n = ensureBuffer(BUFFER_ROOM);
			if (n == 0)
				throw createParseException(null, "String was not terminated before end of input", true);
	
			int end = index + n;
			while (index < end) {
				char c = stringChar();

				switch (c) {
				case '\"':
					fixupAfterRawBufferRead();
					return;
				case '\\':
					// Ensure that we have at least MAX_ESCAPE here in the buffer
					if (end - index < MAX_ESCAPE) {
						// Re-adjust the buffer end, unlikely path
						n = ensureBuffer(MAX_ESCAPE);
						end = index + n;
						// Make sure that there's enough chars for a \\uXXXX escape
						if (buffer[index] == 'u' && n < MAX_ESCAPE) {
							index = bufferLength; // Reset index to last valid location
							throw createParseException(null,
									"EOF encountered in the middle of a string escape",
									false);
						}
					}
					char escape = buffer[index++];
					switch (escape) {
					case 'b':
						reusableBuffer.append('\b');
						break;
					case 'f':
						reusableBuffer.append('\f');
						break;
					case 'n':
						reusableBuffer.append('\n');
						break;
					case 'r':
						reusableBuffer.append('\r');
						break;
					case 't':
						reusableBuffer.append('\t');
						break;
					case '"':
					case '/':
					case '\\':
						reusableBuffer.append(escape);
						break;
					case 'u':
						int escaped = 0;
	
						for (int j = 0; j < 4; j++) {
							escaped <<= 4;
							int digit = buffer[index++];
							if (digit >= '0' && digit <= '9') {
								escaped |= (digit - '0');
							} else if (digit >= 'A' && digit <= 'F') {
								escaped |= (digit - 'A') + 10;
							} else if (digit >= 'a' && digit <= 'f') {
								escaped |= (digit - 'a') + 10;
							} else {
								throw createParseException(null, "Expected unicode hex escape character: "
									+ (char)digit + " (" + digit + ")", false);
							}
						}
	
						reusableBuffer.append((char)escaped);
						break;
					default:
						throw createParseException(null, "Invalid escape: \\" + escape, false);
					}
					break;
				default:
					reusableBuffer.append(c);
				}
			}
			
			if (index > bufferLength) {
				index = bufferLength; // Reset index to last valid location
				throw createParseException(null,
						"EOF encountered in the middle of a string escape",
						false);
			}
		}
	}

	/**
	 * Advances a character, throwing if it is illegal in the context of a JSON string.
	 */
	private char stringChar() throws JsonParserException {
		char c = buffer[index++];
		if (c < 32)
			throwControlCharacterException(c);
		return c;
	}

	private void throwControlCharacterException(char c) throws JsonParserException {
		// Need to ensure that we position this at the correct location for the error
		if (c == '\n') {
			linePos++;
			rowPos = index + 1 + charOffset;
			utf8adjust = 0;
		}
		throw createParseException(null,
				"Strings may not contain control characters: 0x" + Integer.toString(c, 16), false);
	}

	/**
	 * Quick test for digit characters.
	 */
	private boolean isDigitCharacter(int c) {
		return (c >= '0' && c <= '9') || c == 'e' || c == 'E' || c == '.' || c == '+' || c == '-';
	}

	/**
	 * Quick test for whitespace characters.
	 */
	boolean isWhitespace(int c) {
		return c == ' ' || c == '\n' || c == '\r' || c == '\t';
	}

	/**
	 * Quick test for ASCII letter characters.
	 */
	boolean isAsciiLetter(int c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	/**
	 * Returns true if EOF.
	 */
	private boolean refillBuffer() throws JsonParserException {
		try {
			int r = reader.read(buffer, 0, buffer.length);
			if (r <= 0) {
				return true;
			}
			charOffset += bufferLength;
			index = 0;
			bufferLength = r;
			return false;
		} catch (IOException e) {
			throw createParseException(e, "IOException", true);
		}
	}

	/**
	 * Peek one char ahead, don't advance, returns {@code EOF} on end of input.
	 */
	private int peekChar() {
		return eof ? -1 : buffer[index];
	}

	/**
	 * Ensures that there is enough room in the buffer to directly access the next N chars via buffer[].
	 */
	int ensureBuffer(int n) throws JsonParserException {
		// We're good here
		if (bufferLength - n >= index) {
			return n;
		}

		// Nope, we need to read more, but we also have to retain whatever buffer we have
		if (index > 0) {
			charOffset += index;
			bufferLength = bufferLength - index;
			System.arraycopy(buffer, index, buffer, 0, bufferLength);
			index = 0;
		}
		try {
			while (buffer.length > bufferLength) {
				int r = reader.read(buffer, bufferLength, buffer.length - bufferLength);
				if (r <= 0) {
					return bufferLength - index;
				}
				bufferLength += r;
				if (bufferLength > n)
					return bufferLength - index;
			}

			// Should be impossible
			assert false : "Unexpected internal error";
			throw new IOException("Unexpected internal error");
		} catch (IOException e) {
			throw createParseException(e, "IOException", true);
		}
	}

	/**
	 * Advance one character ahead, or return {@code EOF} on end of input.
	 */
	private int advanceChar() throws JsonParserException {
		if (eof)
			return -1;

		int c = buffer[index];
		if (c == '\n') {
			linePos++;
			rowPos = index + 1 + charOffset;
			utf8adjust = 0;
		}

		index++;

		// Prepare for next read
		if (index >= bufferLength)
			eof = refillBuffer();

		return c;
	}
	
	int advanceCharFast() {
		int c = buffer[index];
		if (c == '\n') {
			linePos++;
			rowPos = index + 1 + charOffset;
			utf8adjust = 0;
		}

		index++;
		return c;
	}
	
	private void consumeWhitespace() throws JsonParserException {
		int n;
		do {
			n = ensureBuffer(BUFFER_ROOM);
			for (int i = 0; i < n; i++) {
				char c = buffer[index];
				if (!isWhitespace(c)) {
					fixupAfterRawBufferRead();
					return;
				}
				if (c == '\n') {
					linePos++;
					rowPos = index + 1 + charOffset;
					utf8adjust = 0;
				}
				index++;
			}
		} while (n > 0);
		eof = true;
	}
	
	/**
	 * Consumes a token, first eating up any whitespace ahead of it. Note that number tokens are not necessarily valid
	 * numbers.
	 */
	int advanceToToken() throws JsonParserException {
		int c = advanceChar();
		while (isWhitespace(c))
			c = advanceChar();

		tokenCharPos = index + charOffset - rowPos - utf8adjust;
		tokenCharOffset = charOffset + index;
		
		int token;
		switch (c) {
		case -1:
			return TOKEN_EOF;
		case '[':
			token = TOKEN_ARRAY_START;
			break;
		case ']':
			token = TOKEN_ARRAY_END;
			break;
		case ',':
			token = TOKEN_COMMA;
			break;
		case ':':
			token = TOKEN_COLON;
			break;
		case '{':
			token = TOKEN_OBJECT_START;
			break;
		case '}':
			token = TOKEN_OBJECT_END;
			break;
		case 't':
			consumeKeyword((char)c, JsonTokener.TRUE);
			token = TOKEN_TRUE;
			break;
		case 'f':
			consumeKeyword((char)c, JsonTokener.FALSE);
			token = TOKEN_FALSE;
			break;
		case 'n':
			consumeKeyword((char)c, JsonTokener.NULL);
			token = TOKEN_NULL;
			break;
		case '\"':
			consumeTokenString();
			token = TOKEN_STRING;
			break;
		case '-':
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			consumeTokenNumber((char)c);
			token = TOKEN_NUMBER;
			break;
		case '+':
		case '.':
			throw createParseException(null, "Numbers may not start with '" + (char)c + "'", true);
		default:
			if (isAsciiLetter(c))
				throw createHelpfulException((char)c, null, 0);

			throw createParseException(null, "Unexpected character: " + (char)c, true);
		}
		
//		consumeWhitespace();
		return token;
	}

	int tokenChar() throws JsonParserException {
		int c = advanceChar();
		while (isWhitespace(c))
			c = advanceChar();
		return c;
	}

	/**
	 * Helper function to fixup eof after reading buffer directly.
	 */
	void fixupAfterRawBufferRead() throws JsonParserException {
		if (index >= bufferLength)
			eof = refillBuffer();
	}

	/**
	 * Throws a helpful exception based on the current alphanumeric token.
	 */
	JsonParserException createHelpfulException(char first, char[] expected, int failurePosition)
			throws JsonParserException {
		// Build the first part of the token
		StringBuilder errorToken = new StringBuilder(first
				+ (expected == null ? "" : new String(expected, 0, failurePosition)));

		// Consume the whole pseudo-token to make a better error message
		while (isAsciiLetter(peekChar()) && errorToken.length() < 15)
			errorToken.append((char)advanceChar());

		return createParseException(null, "Unexpected token '" + errorToken + "'"
				+ (expected == null ? "" : ". Did you mean '" + first + new String(expected) + "'?"), true);
	}

	/**
	 * Creates a {@link JsonParserException} and fills it from the current line and char position.
	 */
	JsonParserException createParseException(Exception e, String message, boolean tokenPos) {
		if (tokenPos)
			return new JsonParserException(e, message + " on line " + linePos + ", char " + tokenCharPos,
					linePos, tokenCharPos, tokenCharOffset);
		else {
			int charPos = Math.max(1, index + charOffset - rowPos - utf8adjust);
			return new JsonParserException(e, message + " on line " + linePos + ", char " + charPos, linePos, charPos,
					index + charOffset);
		}
	}
}
