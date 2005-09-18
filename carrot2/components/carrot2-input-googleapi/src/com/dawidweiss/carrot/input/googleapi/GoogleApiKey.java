package com.dawidweiss.carrot.input.googleapi;

/**
 * A google API key.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
class GoogleApiKey {
	private final String key;

	private boolean invalid;

	/**
	 * Creates a google API key and maximum results this
	 * key may serve.
	 */
	public GoogleApiKey(String key) {
		this.key = key;
		this.invalid = false;
	}

	public final String getKey() {
		if (invalid) {
			throw new IllegalStateException("This key is currently disabled.");
		}
		return key;
	}

	public void setInvalid(boolean flag) {
		this.invalid = flag;
	}

	public boolean isInvalid() {
		return invalid;
	}
}
