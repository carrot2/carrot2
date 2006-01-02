
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.core.local;

/**
 * Output components act in response to the result of processing of a query.
 * 
 * <p>
 * Output filters will usually add specific  data-related methods to the main
 * body of this plain interface. Data exchange between the predecessor
 * component and an output component requires their mutual knowledge of these
 * data-related methods. This can be achieved by verifying {@linkplain
 * LocalComponent capabilities}
 * </p>
 * 
 * <p>
 * Output components should also expose some value to the {@link LocalProcess}
 * that initiated processing. This value is a very generic {@link
 * java.lang.Object} reference, so essentially components are free to return
 * any complex type.
 * </p>
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public interface LocalOutputComponent extends LocalComponent {
    /**
     * @return Returns the value, which is considered the "result" of query
     *         processing. This value is then returned from {@link
     *         LocalProcess#query(RequestContext, String)} method of {@link
     *         LocalProcess} interface that initiated query processing.
     */
    Object getResult();
}
