package org.carrot2.workbench.editors;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

/**
 * Template implementation of {@link IAttributeEditor}.
 */
public abstract class AttributeEditorAdapter implements IAttributeEditor
{
    /*
     * 
     */
    private final List<IAttributeListener> listeners = 
        new CopyOnWriteArrayList<IAttributeListener>();

    /**
     * Attribute descriptor saved by {@link #init(AttributeDescriptor)}.
     */
    protected AttributeDescriptor descriptor;

    /**
     * Store attribute descriptor in {@link #descriptor}.
     */
    public void init(AttributeDescriptor descriptor)
    {
        this.descriptor = descriptor;
    }

    /*
     * 
     */
    public String getAttributeKey()
    {
        return this.descriptor.key;
    }

    /*
     * 
     */
    public void dispose()
    {
        listeners.clear();
    }

    public void saveState(IMemento memento)
    {
        // Do nothing.
    }

    public void restoreState(IMemento memento)
    {
        // Do nothing.
    }

    /**
     * 
     */
    public void addAttributeChangeListener(IAttributeListener listener)
    {
        listeners.add(listener);
    }

    /**
     * 
     */
    public void removeAttributeChangeListener(IAttributeListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Default implementation returns <code>false</code>.
     */
    public boolean containsLabel()
    {
        return false;
    }

    /*
     * Re-declare {@link IAttributeEditor} to avoid @Override warnings. 
     */

    public abstract void createEditor(Composite parent, Object layoutData);
    public abstract Object getValue();
    public abstract void setValue(Object object);

    /**
     * Unconditionally fire a change event.
     */
    protected final void fireAttributeChange(AttributeChangedEvent event)
    {
        for (IAttributeListener listener : listeners)
        {
            listener.attributeChange(event);
        }
    }
}
