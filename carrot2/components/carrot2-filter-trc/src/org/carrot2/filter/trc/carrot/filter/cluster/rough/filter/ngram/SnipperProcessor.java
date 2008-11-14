
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.filter.cluster.rough.filter.ngram;

import org.carrot2.filter.trc.carrot.filter.cluster.rough.Snippet;


/**
 * Interface for general processor of snippet
 */
public interface SnipperProcessor {

    /**
     * Do some processing with given snippet
     * @param snippet
     */
    public void process(Snippet snippet);

    
}
