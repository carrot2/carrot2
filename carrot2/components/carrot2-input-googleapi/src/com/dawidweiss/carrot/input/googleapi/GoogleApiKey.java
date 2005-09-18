package com.dawidweiss.carrot.input.googleapi;

/**
 * A google API key.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
class GoogleApiKey {
	private final String key;
	private final String name;

	private boolean invalid;

	/**
	 * Creates a google API key and maximum results this
	 * key may serve.
	 */
	public GoogleApiKey(String key) {
		this(key, "[gapi:" + key.substring(0, key.length()/2) + "..." + "]");
	}

	public GoogleApiKey(String key, String name) {
		this.key = key;
		this.name = name;
		this.invalid = false;
	}

	public final String getKey() {
		if (invalid) {
			throw new IllegalStateException("This key is currently disabled.");
		}
		return key;
	}
	
	/**
	 * Returns the name of this key.
	 */
	public String getName() {
		return name;
	}

	public void setInvalid(boolean flag) {
		this.invalid = flag;
	}

	public boolean isInvalid() {
		return invalid;
	}
}
