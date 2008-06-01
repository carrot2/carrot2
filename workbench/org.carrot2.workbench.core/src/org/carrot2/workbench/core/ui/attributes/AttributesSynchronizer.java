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
    public static void synchronize(final AttributesProvider provider1,
        final AttributesProvider provider2)
    {
        synchronizeOneWay(provider1, provider2);
        synchronizeOneWay(provider2, provider1);
    }

    private static void synchronizeOneWay(final AttributesProvider provider1,
        final AttributesProvider provider2)
    {
        provider1.addAttributeChangeListener(new AttributeChangeListener()
        {
            public void attributeChange(AttributeChangeEvent event)
            {
                provider2.setAttributeValue(event.key, event.value);
            }
        });
        provider1.addPropertyChangeListener(new IPropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                provider2.setPropertyValue(event.getProperty(), event.getNewValue());
            }
        });
    }
}
