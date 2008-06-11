package org.carrot2.workbench.core.ui.attributes;

import org.carrot2.workbench.editors.AttributeChangeEvent;
import org.carrot2.workbench.editors.AttributeChangeListener;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * Mediator-like class, which is responsible for controlling synchronization between two
 * {@link AttributesProvider}s (usually editor and a view).
 */
public class AttributesSynchronizer
{
    public static void synchronize(final AttributeListComponent editorProvider,
        final AttributeListComponent viewProvider)
    {
        synchronizeOneWay(viewProvider, editorProvider);
        synchronizeOneWay(editorProvider, viewProvider);
    }

    private static void synchronizeOneWay(final AttributeListComponent component1,
        final AttributeListComponent component2)
    {
        component2.addAttributeChangeListener(new AttributeChangeListener()
        {
            public void attributeChange(AttributeChangeEvent event)
            {
                component1.setAttributeValue(event.key, event.value);
            }
        });
        component2.addPropertyChangeListener(new IPropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                if (event.getProperty().equals(AttributeListComponent.LIVE_UPDATE))
                {
                    component1.setLiveUpdate((Boolean) event.getNewValue());
                }
            }
        });
    }
}
