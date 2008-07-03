package org.carrot2.workbench.core;

import org.carrot2.core.ProcessingComponent;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Loaded and verified implementations of a given extension point.
 */
public class ExtensionImpl
{
    public final String id;
    public final String label;
    public final ImageDescriptor icon;

    public final String providerId;

    public final Class<? extends ProcessingComponent> clazz;

    public ExtensionImpl(String id, String label, ImageDescriptor icon,
        Class<? extends ProcessingComponent> clazz, String providerId)
    {
        this.id = id;
        this.label = label;
        this.icon = icon;
        this.providerId = providerId;
        this.clazz = clazz;
    }

    public ProcessingComponent getInstance()
    {
        try
        {
            return clazz.newInstance();
        }
        catch (Exception e)
        {
            /* Very unlikely, since all implementations are verified at load-time. */
            throw new RuntimeException(e);
        }
    }
}