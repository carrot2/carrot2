
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

import org.carrot2.core.IClusteringAlgorithm;
import org.carrot2.util.attribute.AttributeValueSet;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;

/**
 * Sets {@link IClusteringAlgorithm}'s default values used for subsequent requests.
 */
final class SetNewDefaultsAction extends Action
{
    private final SearchInput searchInput;

    public SetNewDefaultsAction(SearchInput searchInput)
    {
        super("Set as defaults for new queries");
        this.searchInput = searchInput;
    }

    @Override
    public void runWithEvent(Event event)
    {
        // Collect all default @Input attributes.
        final AttributeValueSet defaults = 
            SaveAttributesAction.getDefaultAttributeValueSet(searchInput.getAlgorithmId());

        final Map<String, Object> overrides = 
            searchInput.getAttributeValueSet().getAttributeValues();

        overrides.keySet().retainAll(defaults.getAttributeValues().keySet());

        // Find the SearchInputView and set the editor's attribute values as defaults.
        final SearchInputView searchView = SearchInputView.getView();
        for (Map.Entry<String, Object> e : overrides.entrySet())
        {
            searchView.setAttribute(e.getKey(), e.getValue());
        }
    }
}
