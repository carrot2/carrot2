
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

/**
 * Raw textual document representation for clustering purposes. Every document
 * is characterized by several properties (see {@link #getProperty(String)}),
 * and convenience access methods for the most common of them.
 * 
 * <p>
 * This interface should be considered deprecated. Refactored code should use
 * {@link TokenizedDocument} instead.
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public interface RawDocument {
    /**
     * Document property that, if available, yields a summarized version of the
     * document.
     * 
     * <p>
     * The value returned for this property is of type {@link
     * java.lang.String}.
     * </p>
     */
    public final static String PROPERTY_SNIPPET = "snippet";

    /**
     * Document property that, if available, yields a {@link java.io.Reader}
     * object to the full textual content of the document.
     * 
     * <p>
     * The value returned for this property is of type {@link java.io.Reader}.
     * </p>
     */
    public final static String PROPERTY_CONTENT_READER = "reader";

    /**
     * Document property that, if available, yields a {@link java.lang.String}
     * object with the title of the document.
     * 
     * <p>
     * The value returned for this property is of type {@link
     * java.lang.String}.
     * </p>
     */
    public final static String PROPERTY_TITLE = "title";

    /**
     * Document property that, if available, yields a {@link java.lang.String}
     * object with the Uniform Resource Locator (URL) of this document.
     * 
     * <p>
     * The value returned for this property is of type {@link
     * java.lang.String}.
     * </p>
     */
    public final static String PROPERTY_URL = "url";

    /**
     * Property that holds a two-letter ISO-639 code of the language of this document.
     * 
     * <p>
     * The value returned for this property is of type {@link java.lang.String}
     * </p>
     */
    public final static String PROPERTY_LANGUAGE = "lang";

    /**
     * Returns an array of idenfitiers of all sources that returned this
     * document.
     * 
     * <p>The value of this property is <code>String []</code>.</p>
     */
    public static final String PROPERTY_SOURCES = "source";
    
    /**
     * Returns an array of keywords the search engine attached to the document, if any.
     * 
     * <p>The value of this property is <code>String []</code>.</p>
     */
    public static final String PROPERTY_KEYWORDS = "keywords";
    
    /**
     * Returns a named property of the document.   Names of the available
     * properties depend on the component that produces
     * <code>RawDocument</code> objects. Some constants should also be
     * available in the definition of this interface.
     *
     * @param name An identifier specifying the name of the document property.
     *
     * @return Returns an Object with the value corresponding to named
     *         property, or <code>null</code> if such property is not
     *         available.
     */
    public Object getProperty(String name);

    /**
     * Sets a value for a named property in this document.
     * 
     * @param propertyName Name of the property to set.
     * @param value The new value of the property.
     *
     * @return Previous value of the property if it existed, or
     *         <code>null</code>.
     */
    public Object setProperty(String propertyName, Object value);

    /**
     * Returns a unique identifier of this document. The identifier's
     * uniqueness should be guaranteed by the component producing the document
     * reference.
     *
     * @return The unique identifier of this document.
     */
    public Object getId();

    /**
     * Convenience method for accessing {@link #PROPERTY_URL}.
     *
     * @return Returns the value corresponding to {@link #PROPERTY_URL}
     *         property.
     */
    public String getUrl();

    /**
     * Convenience method for accessing {@link #PROPERTY_SNIPPET}.
     *
     * @return Returns the value corresponding to {@link #PROPERTY_SNIPPET}
     *         property.
     */
    public String getSnippet();

    /**
	 * Convenience method for accessing {@link #PROPERTY_TITLE}.
	 *
     * @return Returns the value corresponding to {@link #PROPERTY_TITLE}
     *         property.
     */
    public String getTitle();
    
    /**
     * Returns the score of this document measured as its relevance to the
     * query. The score can be in the range of [0,1] (inclusive boundary).
     * 
     * <p>
     * Input components that are not capable of generating score values may
     * return a value of -1 for all documents. In such case, the order of
     * document's declaration constitutes its rank of  relevance to the query.
     * </p>
     * 
     * @return The score value in the range of [0,1], or -1 if the score value
     *         is not available.
     */
    public float getScore();
}
