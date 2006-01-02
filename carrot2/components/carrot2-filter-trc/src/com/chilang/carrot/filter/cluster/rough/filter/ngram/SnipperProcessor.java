
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
package com.chilang.carrot.filter.cluster.rough.filter.ngram;

import com.chilang.carrot.filter.cluster.rough.Snippet;


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
