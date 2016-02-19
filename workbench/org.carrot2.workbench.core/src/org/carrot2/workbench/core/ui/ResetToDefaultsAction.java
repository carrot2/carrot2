
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

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;

/**
 * Reset to most recently set defaults for a given algorithm.
 */
final class ResetToDefaultsAction extends Action
{
    private final SearchInput input;

    public ResetToDefaultsAction(SearchInput input)
    {
        super("Reset to defaults");
        this.input = input;
    }

    @Override
    public void runWithEvent(Event event)
    {
        final SearchInputView searchInput = SearchInputView.getView();
        for (Map.Entry<String, Object> e : 
            searchInput.filterAttributesOf(input.getAlgorithmId()).entrySet())
        {
            input.setAttribute(e.getKey(), e.getValue());
        }
    }
}
