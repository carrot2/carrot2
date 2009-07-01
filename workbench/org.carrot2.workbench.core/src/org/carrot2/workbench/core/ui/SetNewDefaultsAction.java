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
        // Collect @Input attributes.
        final AttributeValueSet defaults = SaveAttributesAction
            .getDefaultAttributeValueSet(searchInput.getAlgorithmId());

        final Map<String, Object> overrides = searchInput.getAttributeValueSet()
            .getAttributeValues();
        SaveAttributesAction.removeSpecialKeys(overrides);
        SaveAttributesAction.removeKeysWithDefaultValues(overrides, defaults);
        overrides.keySet().retainAll(defaults.getAttributeValues().keySet());

        // Find the SearchInputView and set the values as defaults.
        final SearchInputView searchView = SearchInputView.getView();
        
        for (Map.Entry<String, Object> e : overrides.entrySet())
        {
            searchView.setAttribute(e.getKey(), e.getValue());
        }
    }
}
