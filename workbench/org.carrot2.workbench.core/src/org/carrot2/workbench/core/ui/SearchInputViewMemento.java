package org.carrot2.workbench.core.ui;

import org.carrot2.util.attribute.AttributeValueSet;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

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
}
