package org.carrot2.workbench.core.ui;

import java.util.Map;

import org.carrot2.workbench.core.ui.SearchEditor.PanelName;
import org.carrot2.workbench.core.ui.SearchEditor.PanelState;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

/**
 * Persistent state for {@link SearchInputView}.
 */
@Root
public final class SearchEditorMemento
{
    @ElementMap
    public Map<PanelName, PanelState> panels;
}
