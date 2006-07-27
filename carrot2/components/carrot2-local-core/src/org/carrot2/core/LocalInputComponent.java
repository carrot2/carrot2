
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

package org.carrot2.core;

/**
 * Local input components generate the initial data-related method calls to
 * successive components in a processing chain in response to a query, passed
 * from a {@link LocalProcess} object.
 * 
 * <p>
 * A local input component has an additional method {@link
 * #setNext(LocalComponent)}, which is used by the {@link LocalProcess} class
 * to link a successor component to an instance of this interface at the
 * query-processing time.
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public interface LocalInputComponent extends LocalComponent {
    /**
     * This is a named parameter stored in the RequestContext. Input components
     * may, but do not have to pass it.  The value of this object is  of type
     * <code>java.lang.String</code>.
     */
    public static final String PARAM_QUERY = "query";

    /**
     * A named request context's parameter specifying the number of results
     * that the input component should return for a query.  The type of this
     * parameter is a <code>java.lang.String</code>, but it should be possible
     * to convert the string literal to an integer (<code>int</code> type).
     */
    public final static String PARAM_REQUESTED_RESULTS = "requested-results";

    /**
     * A named request context's parameter specifying the start of the returned
     * results window. The first result is indexed as zero.  The value of this
     * parameter is a <code>java.lang.String</code> type object, within the
     * range of <code>int</code>.
     */
    public final static String PARAM_START_AT = "start-at";

    /**
     * A named request context's parameter specifying the total number of
     * documents matching a query.  This parameter is usually set by the input
     * component and is read-only.  The value of this parameter is of type
     * <code>java.lang.Long</code> type object.
     */
    public final static String PARAM_TOTAL_MATCHING_DOCUMENTS = "total-matching-documents";

    /**
     * Sets the query for the current request. The format of the query depends
     * on the component used.
     *
     * @param query A <code>String</code> with the query.
     */
    public void setQuery(String query);

    /**
     * Sets the successor component for the time of processing of a single
     * query. The successor's component reference  must be released (cleared)
     * from any local fields in {@link LocalComponent#flushResources()}
     * method.
     * 
     * <p>
     * {@link LocalProcess} may invoke this method more than once  before the
     * processing begins (see {@link
     * LocalComponent#startProcessing(RequestContext)}). After that, the
     * successor component is considered frozen and must not be changed.
     * </p>
     * 
     * <p>
     * The implementation of this method may cast <code>next</code> to a more
     * specific type it expects based on the required capabilities (see {@link
     * LocalComponent#getRequiredSuccessorCapabilities()}).
     * </p>
     *
     * @param next A reference to an instance of {@link LocalComponent} that is
     *        the successor component in a processing chain assembled for the
     *        execution of a single query.
     */
    public void setNext(LocalComponent next);
}
