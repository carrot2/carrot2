
/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.core.local.clustering;

import java.io.Reader;

import java.util.HashMap;


/**
 * An abstract implementation of some of the basic methods of the {@link
 * RawDocument} interface.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public abstract class RawDocumentBase implements RawDocument {
    /**
     * Lazily initialized hashmap with properties of this document.
     */
    private HashMap properties;
    
    /**
     * Provides a template of implementation of the named property getter.
     */
    public Object getProperty(String name) {
        // the synchronized block should be before 'properties != null'
        // for 100% correct concurrent solution... but it seems unlikely
        // that pointer access would cause us much trouble here.
        if (properties != null) {
            synchronized (this) {
                Object value = properties.get(name);

                if (value != null) {
                    return value;
                }
            }
        }

        if (RawDocument.PROPERTY_URL.equals(name)) {
            return getUrl();
        } else if (RawDocument.PROPERTY_SNIPPET.equals(name)) {
            return getSnippet();
        } else if (RawDocument.PROPERTY_CONTENT_READER.equals(name)) {
        	return getContent();
        } else {
            return null;
        }
    }

    /**
     * @return Should return a reader to the contents of this document. The
     *         default implementation returns <code>null</code>.
	 */
	private Reader getContent() {
		return null;
	}

	/**
	 * @return Should return the snippet of this document. The default
     * implementation returns <code>null</code>.
	 */
	protected String getSnippet() {
		return null;
	}

	/**
     * Sets a value for a named property in this document.
     */
    public Object setProperty(String propertyName, Object value) {
        synchronized (this) {
            if (properties == null) {
                properties = new HashMap();
            }

            return properties.put(propertyName, value);
        }
    }
    
    /**
     * @return Returns 'no score' constant. 
     */
    public float getScore() {
        return -1;
    }
}
