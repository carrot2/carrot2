
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

package org.carrot2.workbench.editors;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.BindableDescriptor;
import org.eclipse.swt.widgets.Composite;

/**
 * Template implementation of {@link IAttributeEditor}.
 */
public abstract class AttributeEditorAdapter implements IAttributeEditor
{
    /**
     * Array of listeners interested in receiving change events from this editor.
     */
    private final List<IAttributeListener> listeners = new CopyOnWriteArrayList<IAttributeListener>();

    protected AttributeDescriptor descriptor;
    protected BindableDescriptor bindable;
    protected IAttributeEventProvider eventProvider;

    /**
     * Layout and visual info.
     */
    private AttributeEditorInfo attributeEditorInfo;

    /**
     * Store attribute descriptor in {@link #descriptor}.
     */
    public final AttributeEditorInfo init(BindableDescriptor bindable,
        AttributeDescriptor attribute, IAttributeEventProvider eventProvider, Map<String,Object> defaultValues)
    {
        this.descriptor = attribute;
        this.bindable = bindable;
        this.eventProvider = eventProvider;

        this.attributeEditorInfo = init(defaultValues);

        return attributeEditorInfo;
    }

    /**
     * @return This method is invoked to initialize the subclasses. Some values have been assigned
     * to protected fields already.
     */
    protected abstract AttributeEditorInfo init(Map<String,Object> defaultValues);

    /**
     * Returns attribute key from the attribute descriptor.
     */
    public String getAttributeKey()
    {
        return this.descriptor.key;
    }

    /*
     * Re-declare methods from {@link IAttributeEditor} to avoid @Override warnings.
     */
    public abstract void createEditor(Composite parent, int gridColumns);

    /**
     * Does nothing by default.
     */
    public void setFocus()
    {
        // Ignore.
    }

    /*
     * 
     */
    public abstract Object getValue();

    /*
     * 
     */
    public abstract void setValue(Object object);

    /*
     * 
     */
    public void addAttributeListener(IAttributeListener listener)
    {
        listeners.add(listener);
    }

    /*
     * 
     */
    public void removeAttributeListener(IAttributeListener listener)
    {
        listeners.remove(listener);
    }

    /*
     *
     */
    private boolean flag1;
    protected void fireAttributeChanged(AttributeEvent event)
    {
        if (flag1) return;

        flag1 = true;
        try
        {
            for (IAttributeListener listener : listeners)
            {
                listener.valueChanged(event);
            }
        }
        finally
        {
            flag1 = false;
        }
    }

    /*
     * 
     */
    private boolean flag2;
    protected void fireContentChanging(AttributeEvent event)
    {
        if (flag2) return;

        flag2 = true;
        try
        {
            for (IAttributeListener listener : listeners)
            {
                listener.valueChanging(event);
            }
        }
        finally
        {
            flag2 = false;
        }
    }

    /**
     * Clear listeners array and clean references.
     */
    public void dispose()
    {
        listeners.clear();
        descriptor = null;
    }
}
