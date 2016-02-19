
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

import org.carrot2.workbench.core.ui.SearchEditor.*;
import org.simpleframework.xml.*;

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

    /**
     * Per-editor save options.
     */
    @Element(required = false)
    public SaveOptions saveOptions;
}
