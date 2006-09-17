
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.googleapi;

/**
 * A google API key.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
final class GoogleApiKey {
    /** 12 hours */
	public static final int WAIT_TIME_LIMIT_EXCEEDED = 12 * 60 * 60 * 1000;

    /** 15 minutes */
    public static final int WAIT_TIME_UNKNOWN_PROBLEM = 15 * 60 * 1000;

    private final String key;
	private final String name;

	private int invalidatePeriod = 0;

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
	}

	public final String getKey() {
		if (isInvalid()) {
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

	public boolean isInvalid() {
		return invalidatePeriod > 0;
	}

    public void setInvalid(boolean flag, int invalidatePeriod) {
        if (flag) {
            this.invalidatePeriod = invalidatePeriod;
        } else {
            this.invalidatePeriod = 0;
        }
    }

    int getInvalidPeriod() {
        if (!isInvalid()) {
            throw new RuntimeException("Key is valid.");
        }
        return this.invalidatePeriod;
    }

    void setValid() {
        this.invalidatePeriod = 0;
    }
}
