package org.carrot2.workbench.core.ui;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.editors.*;
import org.carrot2.workbench.editors.factory.EditorFactory;
import org.carrot2.workbench.editors.factory.EditorNotFoundException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.collect.Maps;


/**
 * An SWT composite displaying a set of attribute editors.
 */
public final class AttributeEditorList extends Composite
    implements IAttributeChangeProvider
{
    /**
     * A list of {@link AttributeDescriptor}s, indexed by their keys.
     */
    private final Map<String, AttributeDescriptor> attributeDescriptors;

    /**
     * A map between attribute keys and {@link IAttributeEditor}s 
     * visible in this component.
     */
    private Map<String, IAttributeEditor> editors = Maps.newHashMap();

    /**
     * Optional component class attribute descriptors come from.
     */
    private Class<? extends ProcessingComponent> componentClazz;

    /**
     * Attribute change listeners.
     */
    private final List<IAttributeListener> listeners = 
        new CopyOnWriteArrayList<IAttributeListener>();

    /**
     * Forward events from editors to external listeners.
     */
    private final IAttributeListener forwardListener = new IAttributeListener()
    {
        public void attributeChange(AttributeChangedEvent event)
        {
            for (IAttributeListener listener : listeners)
            {
                listener.attributeChange(event);
            }
        }
    };
    
    /**
     * Create a new editor list for a given set of attribute descriptors and
     * an (optional) component class.
     */
    @SuppressWarnings("unchecked")
    public AttributeEditorList(Composite parent, 
        Map<String, AttributeDescriptor> attributeDescriptors)
    {
        this(parent, attributeDescriptors, null);
    }

    /**
     * Create a new editor list for a given set of attribute descriptors and
     * an (optional) component class.
     */
    @SuppressWarnings("unchecked")
    public AttributeEditorList(Composite parent, 
        Map<String, AttributeDescriptor> attributeDescriptors, Class<?> componentClazz)
    {
        super(parent, SWT.NONE);

        this.attributeDescriptors = attributeDescriptors;

        /*
         * Only store component clazz if it is assignable to
         * {@link ProcessingComponent}.
         */
        if (componentClazz != null && ProcessingComponent.class.isAssignableFrom(componentClazz))
        {
            this.componentClazz = (Class<? extends ProcessingComponent>) componentClazz;
        }

        createComponents();
    }

    /**
     * Sets the <code>key</code> editor's current value to <code>value</code>.
     */
    public void setAttribute(String key, Object value)
    {
        final IAttributeEditor editor = editors.get(key);
        if (editor != null)
        {
            editor.setValue(value);
        }
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
    
    /**
     * @return Returns an immutable collection of {@link AttributeDescriptors} associated with 
     * this editor list.
     */
    public Collection<AttributeDescriptor> getAttributeDescriptors()
    {
        return Collections.unmodifiableCollection(this.attributeDescriptors.values());
    }

    /**
     * 
     */
    public void dispose()
    {
        /*
         * Unregister listeners.
         */
        for (IAttributeEditor editor : this.editors.values())
        {
            editor.removeAttributeChangeListener(forwardListener);
        }
    
        super.dispose();
    }

    /**
     * Create internal GUI.
     */
    private void createComponents()
    {
        /*
         * The layout of this control is in two columns: labels in the
         * first column, editors in the second column.
         */
        final GridLayout layout = new GridLayout();
        this.setLayout(layout);
    
        layout.numColumns = 2;
        layout.marginWidth = 0;
    
        for (Map.Entry<String, AttributeDescriptor> entry : attributeDescriptors.entrySet())
        {
            final AttributeDescriptor descriptor = entry.getValue();
    
            final GridData data = new GridData();
            data.horizontalAlignment = GridData.FILL;
            data.verticalAlignment = GridData.FILL;
            data.grabExcessHorizontalSpace = true;
            data.grabExcessVerticalSpace = false;
    
            IAttributeEditor editor = null;
            try
            {
                editor = EditorFactory.getEditorFor(this.componentClazz, descriptor);
                editor.init(descriptor);
            }
            catch (EditorNotFoundException ex)
            {
                /*
                 * No such editor -- log error.
                 */
                Utils.logError(ex, false);
                editor = null;
            }
    
            // Editors that do not have a label span over two columns.
            if (editor != null && editor.containsLabel())
            {
                data.horizontalSpan = 2;
            }
            else
            {
                final Label label = new Label(this, SWT.NONE);
                label.setText(getLabel(descriptor));
                label.setToolTipText(getToolTip(descriptor));
            }
    
            if (editor != null)
            {
                editor.createEditor(this, data);
                editor.setValue(attributeDescriptors.get(descriptor.key).defaultValue);
                editors.put(editor.getAttributeKey(), editor);
    
                /*
                 * Forward events from this editor to all our listeners.
                 */
                editor.addAttributeChangeListener(forwardListener);
            }
            else
            {
                final Label label = new Label(this, SWT.NONE);
                label.setText("No editor");
            }
        }
    }

    /*
     * 
     */
    private String getLabel(AttributeDescriptor descriptor)
    {
        String text = null;
    
        if (descriptor.metadata != null)
        {
            text = descriptor.metadata.getLabelOrTitle();
        }
    
        if (text == null)
        {
            text = "(no label available)";
        }
    
        return text;
    }

    /*
     * 
     */
    private String getToolTip(AttributeDescriptor descriptor)
    {
        String text = null;
    
        if (descriptor.metadata != null)
        {
            text = descriptor.metadata.getDescription();
        }
    
        return text;
    }
}
