
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

package org.carrot2.core;

/**
 * Local filters transform or enrich the data stream and are placed in between
 * input and output components in a processing chain.
 * 
 * <p>
 * A local filter component has an additional method {@link
 * #setNext(LocalComponent)}, which is used by the {@link LocalProcess} class
 * to link a successor component to an instance of this interface at the
 * query-processing time.
 * </p>
 * 
 * <p>
 * Local filters will usually add specific  data-related methods to the main
 * body of this plain interface. Data exchange between the predecessor
 * component and a filter component requires their mutual knowledge of these
 * data-related methods. This can be achieved by verifying {@linkplain
 * LocalComponent capabilities} of the component.
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public interface LocalFilterComponent extends LocalComponent {
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
