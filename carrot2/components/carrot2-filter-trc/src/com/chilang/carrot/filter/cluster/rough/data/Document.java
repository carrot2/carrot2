
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

package com.chilang.carrot.filter.cluster.rough.data;

import java.util.Set;

/**
 * Represent document in the information retrieval system
 */
public interface Document {


    Set getStrongTerms();

    void setStrongTerms(Set strongTerms);

    Set getTermSet();

    void setTermSet(Set termSet);

    int getInternalId();

    void setInternalId(int internalId);

}
