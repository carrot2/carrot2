package org.carrot2.workbench.core.ui;

import java.util.Map;

import org.carrot2.util.attribute.AttributeValueSet;
import org.simpleframework.xml.*;

/**
 * Persistent state for {@link SearchInputView}.
 */
@Root
public final class SearchInputViewMemento
{
    @Element(required = false)
    public String sourceId;

    @Element(required = false)
    public String algorithmId;

    @Element
    public AttributeValueSet attributes;

    @Element
    public boolean linkWithEditor;

    @ElementMap
    public Map<String, Boolean> sectionsExpansionState;
}
