
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

package org.carrot2.webapp;

import java.util.Map;


/**
 * Provides all data for a single "search input" tab.
 *
 * @author Dawid Weiss
 */
public final class TabSearchInput {
    private final String shortName;
    private final String description;
    private final Map otherProperties;

    public TabSearchInput(String shortName, String description, Map otherProperties) {
        this.shortName = shortName;
        this.description = description;

        if (this.shortName == null || this.description == null) {
            throw new IllegalArgumentException("Short name and description of a"
                    + " search input tab must not be null.");
        }
        
        this.otherProperties = otherProperties;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongDescription() {
        return description;
    }
    
    public Map getOtherProperties() {
        return otherProperties;
    }
}