
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

package org.carrot2.filter.trc.carrot.tokenizer;

/**
 * Resolve HTML Entity to its text form (&nbsp -> " ", &amp; -> "&", etc)
 */
public class CommonEntityResolver implements HTMLEntityResolver {

    protected static final String[] ENTITIES =
            {"&nbsp;", "&amp;", "&quot;", "&lt;", "&gt;"};
    protected static final String[] TEXT =
            {" ", "&", "\"", "<", ">"};

    /**
     * Resolve most common entities to its form
     * @param entity
     */
    public String resolve(String entity) {
        for (int i=0; i<ENTITIES.length; i++) {
            if (entity.equals(ENTITIES[i]))
                return TEXT[i];
        }
        //ignore other entities
        return ".";
    }
}
