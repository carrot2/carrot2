package com.dawidweiss.carrot.input.googleapi;

/**
 * A google API key.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
class GoogleApiKey {
	private final String key;
	private final int maxResults;

	private boolean invalid;

	/**
	 * Creates a google API key and maximum results this
	 * key may serve.
	 */
	public GoogleApiKey(String key, int maxResults) {
		this.key = key;
		this.maxResults = maxResults;
		this.invalid = false;
	}

	public final String getKey() {
		if (invalid) {
			throw new IllegalStateException("This key is currently disabled.");
		}
		return key;
	}

	public final int getMaxResults() {
		return maxResults;
	}

	public void setInvalid(boolean flag) {
		this.invalid = flag;
	}

	public boolean isInvalid() {
		return invalid;
	}
}
