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
    @ElementMap(required = false)
    public Map<PanelName, PanelState> panels;

    /**
     * Expansion state for sections inside {@link PanelName#ATTRIBUTES} panel.
     */
    @ElementMap
    public Map<String, Boolean> sectionsExpansionState;
}
