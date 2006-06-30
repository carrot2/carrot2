
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

/**
 * Provides all data for a single "clustering algorithm" option.
 *
 * @author Dawid Weiss
 */
public final class TabAlgorithm {
    private final String shortName;
    private final String description;

    public TabAlgorithm(String shortName, String description) {
        this.shortName = shortName;
        this.description = description;
    }
    
    public String getShortName() {
        return this.shortName;
    }

    public String getLongDescription() {
        return this.description;
    }
}
