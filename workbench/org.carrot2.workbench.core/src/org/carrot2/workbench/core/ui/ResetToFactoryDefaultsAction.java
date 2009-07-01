package org.carrot2.workbench.core.ui;

import java.util.Map;

import org.carrot2.util.attribute.AttributeValueSet;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;

/**
 * Reset to factory-defaults for a given algorithm.
 */
final class ResetToFactoryDefaultsAction extends Action
{
    private final SearchInput input;

    public ResetToFactoryDefaultsAction(SearchInput input)
    {
        super("Reset to factory defaults");
        this.input = input;
    }

    @Override
    public void runWithEvent(Event event)
    {
        final AttributeValueSet avs = SaveAttributesAction
            .getDefaultAttributeValueSet(input.getAlgorithmId());

        for (Map.Entry<String, Object> e : avs.getAttributeValues().entrySet())
        {
            input.setAttribute(e.getKey(), e.getValue());
        }
    }
}
