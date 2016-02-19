
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui;

import java.util.Map;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

/**
 * Persistent state for {@link AttributeView}.
 */
@Root
public final class AttributeViewMemento
{
    /**
     * Expansion state for sections inside the
     * {@link AttributeGroups} component of each {@link AttributeViewPage}.
     */
    @ElementMap
    public Map<String, Boolean> sectionsExpansionState;
}
