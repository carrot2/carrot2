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
