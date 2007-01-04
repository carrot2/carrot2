
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.clustering;

import java.io.Serializable;

import org.carrot2.util.PropertyProviderBase;

/**
 * An abstract implementation of some of the basic methods of the {@link
 * RawDocument} interface.
 * 
 * Make sure all properties stored in subclasses of this class are serializable.
 * 
 * @author Dawid Weiss
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public abstract class RawDocumentBase extends PropertyProviderBase implements RawDocument, Serializable {
    /**
     * Creates a new raw document.
     * 
     * @param url
     * @param title
     * @param snippet
     */
    public RawDocumentBase(String url, String title, String snippet) {
	    setProperty(PROPERTY_URL, url);
	    setProperty(PROPERTY_TITLE, title);
	    setProperty(PROPERTY_SNIPPET, snippet);
    }
    
    /** 
     * Cloning constructor.
     */ 
    public RawDocumentBase(RawDocument r) {
	    if (r instanceof RawDocumentBase) {
            super.clonePropertiesFrom((RawDocumentBase) r);
	    } else {
		    setProperty(PROPERTY_URL, r.getProperty(PROPERTY_URL));
		    setProperty(PROPERTY_SNIPPET, r.getProperty(PROPERTY_SNIPPET));
		    setProperty(PROPERTY_TITLE, r.getProperty(PROPERTY_TITLE));
	    } 
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.RawDocument#getSnippet()
     */
    public String getSnippet()
    {
        return (String) getProperty(PROPERTY_SNIPPET);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.RawDocument#getUrl()
     */
    public String getUrl()
    {
        return (String) getProperty(PROPERTY_URL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.clustering.RawDocument#getTitle()
     */
    public String getTitle()
    {
        return (String) getProperty(PROPERTY_TITLE);
    }
	
    /**
     * @return Returns 'no score' constant.
     */
    public float getScore()
    {
        return -1;
    }

    /**
     * Returns a stringified version of this raw document.
     */
    public String toString()
    {
        return "[URL=" + getUrl() + ", title=" + getTitle() + ", snippet: " + getSnippet() + "]"; 
    }
}