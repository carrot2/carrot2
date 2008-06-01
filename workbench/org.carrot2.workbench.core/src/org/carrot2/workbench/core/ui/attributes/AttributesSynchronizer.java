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
        editorProvider.addAttributeChangeListener(new AttributeChangeListener()
        {
            public void attributeChange(AttributeChangeEvent event)
            {
                viewProvider.setAttributeValue(event.key, event.value, false);
            }
        });
        editorProvider.addPropertyChangeListener(new IPropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                if (event.getProperty().equals(AttributeListComponent.LIVE_UPDATE))
                {
                    viewProvider.setLiveUpdate((Boolean) event.getNewValue(), false);
                }
            }
        });
        viewProvider.addAttributeChangeListener(new AttributeChangeListener()
        {
            public void attributeChange(AttributeChangeEvent event)
            {
                editorProvider.setAttributeValue(event.key, event.value);
            }
        });
        viewProvider.addPropertyChangeListener(new IPropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                if (event.getProperty().equals(AttributeListComponent.LIVE_UPDATE))
                {
                    editorProvider.setLiveUpdate((Boolean) event.getNewValue());
                }
            }
        });
    }
}
