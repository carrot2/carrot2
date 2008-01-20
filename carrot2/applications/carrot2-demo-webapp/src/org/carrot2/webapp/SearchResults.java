
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

package org.carrot2.webapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing serialized search/ clustering results in {@link net.sf.ehcache.Cache}.
 * 
 * @author Dawid Weiss
 */
final class SearchResults implements Serializable {

    private final ArrayList documents;

    public SearchResults(List documents) {
        this.documents = new ArrayList(documents);
    }

    public ArrayList getDocuments() {
        return documents;
    }
}
