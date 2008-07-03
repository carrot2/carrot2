package org.carrot2.workbench.core.ui;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.carrot2.core.Controller;
import org.carrot2.util.attribute.AttributeValueSet;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.editors.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;

/**
 * <p>
 * Instances of this class constitute the input to a search/clustering process (required
 * when opening a new editor).
 */
public class SearchInput implements IEditorInput, IPersistableElement, IAttributeChangeProvider
{
    /**
     * Document source identifier for a {@link Controller} instance.
     */
    private final String sourceId;

    /**
     * Algorithm identifier for a {@link Controller} instance.
     */
    private final String algorithmId;

    /**
     * An attribute set for this input request.
     */
    private final AttributeValueSet attributes;

    /**
     * Attribute change listeners.
     */
    private final List<IAttributeListener> listeners = 
        new CopyOnWriteArrayList<IAttributeListener>();

    /**
     * Create a new search input, defining source algorithm identifier, processing
     * algorithm and a default attribute set.
     * <p>
     * The reference to the default attribute set is stored inside this object (no copy is
     * made).
     */
    public SearchInput(String sourceId, String algorithmId, AttributeValueSet attributes)
    {
        this.sourceId = sourceId;
        this.algorithmId = algorithmId;
        this.attributes = attributes;
    }

    /*
     * 
     */
    public String getSourceId()
    {
        return sourceId;
    }

    /*
     * 
     */
    public String getAlgorithmId()
    {
        return algorithmId;
    }

    /**
     * Returns an {@link AttributeValueSet} used to drive the processing of this input.
     * This object should be considered read-only and no changes should be made on it
     * directly (events are not propagated if done so).
     * 
     * @see #setAttribute(String, Object)
     * @see #getAttribute(String)
     */
    public AttributeValueSet getAttributeValueSet()
    {
        return attributes;
    }

    /**
     * Sets the value of a given processing attribute, fires events to 
     * listeners. 
     */
    public void setAttribute(String key, Object value)
    {
        final Object prev = getAttribute(key);
        if (prev != null && prev.equals(value))
        {
            return;
        }
        
        if (prev == null && value == null)
        {
            return;
        }

        this.attributes.setAttributeValue(key, value);
        fireAttributeChanged(key, value);
    }

    /**
     * Shortcut for {@link #getAttributeValueSet()}.{@link #getAttribute(String)}.
     */
    public Object getAttribute(String key)
    {
        return getAttributeValueSet().getAttributeValue(key);
    }

    /**
     * Editor inputs always exist (in case a given component is no longer available, they
     * will throw an exception at runtime).
     */
    public boolean exists()
    {
        return true;
    }

    /*
     * 
     */
    public ImageDescriptor getImageDescriptor()
    {
        return WorkbenchCorePlugin.getDefault().getSources().getImplementation(sourceId).icon;
    }

    /*
     * 
     */
    public String getName()
    {
        return "Search [source: " + this.sourceId + ", algorithm: " + this.algorithmId
            + "]";
    }

    /*
     * 
     */
    public IPersistableElement getPersistable()
    {
        return this;
    }

    /*
     * 
     */
    public String getToolTipText()
    {
        return "Search input";
    }

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter)
    {
        if (adapter.isInstance(this))
        {
            return this;
        }

        return null;
    }

    /**
     * Factory for {@link IPersistableElement}.
     */
    public String getFactoryId()
    {
        return SearchInputFactory.ID;
    }

    /**
     * Save state to {@link IPersistableElement}.
     */
    public void saveState(IMemento memento)
    {
        SearchInputFactory.saveState(this, memento);
    }

    /*
     * 
     */
    public void addAttributeChangeListener(IAttributeListener listener)
    {
        this.listeners.add(listener);
    }
    
    /*
     * 
     */
    public void removeAttributeChangeListener(IAttributeListener listener)
    {
        this.listeners.remove(listener);
    }
    
    /*
     * 
     */
    public void fireAttributeChanged(String key, Object value)
    {
        final AttributeChangedEvent event = new AttributeChangedEvent(this, key, value);

        for (IAttributeListener listener : listeners)
        {
            listener.attributeChange(event);
        }
    }
}
