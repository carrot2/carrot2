package org.carrot2.workbench.core.ui;

import java.text.Collator;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.carrot2.workbench.editors.factory.EditorFactory;
import org.carrot2.workbench.editors.factory.EditorNotFoundException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * An SWT composite displaying an alphabetically ordered list of 
 * {@link IAttributeEditor}s.
 */
public final class AttributeList extends Composite
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
        
        public void contentChanging(IAttributeEditor editor, Object value)
        {
            for (IAttributeListener listener : listeners)
            {
                listener.contentChanging(editor, value);
            }
        }
    };

    /**
     * Create a new editor list for a given set of attribute descriptors and
     * an (optional) component class.
     */
    @SuppressWarnings("unchecked")
    public AttributeList(Composite parent, 
        Map<String, AttributeDescriptor> attributeDescriptors)
    {
        this(parent, attributeDescriptors, null);
    }

    /**
     * Create a new editor list for a given set of attribute descriptors and
     * an (optional) component class.
     */
    @SuppressWarnings("unchecked")
    public AttributeList(Composite parent, 
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
         * Sort alphabetically by label.
         */
        final Locale locale = Locale.getDefault();
        final Map<String,String> labels = Maps.newHashMap();
        for (Map.Entry<String, AttributeDescriptor> entry : attributeDescriptors.entrySet())
        {
            labels.put(entry.getKey(), getLabel(entry.getValue()).toLowerCase(locale));
        }

        final Collator collator = Collator.getInstance(locale);
        final List<String> sortedKeys = Lists.newArrayList(labels.keySet());
        Collections.sort(sortedKeys, new Comparator<String>() {
            public int compare(String a, String b)
            {
                return collator.compare(labels.get(a), labels.get(b));
            }
        });

        /*
         * Create editors and inquire about their layout needs.
         */
        final Map<String, IAttributeEditor> editors = Maps.newHashMap();
        final Map<String, AttributeEditorInfo> editorInfos = Maps.newHashMap();

        int maxColumns = 1;
        for (String key : sortedKeys)
        {
            final AttributeDescriptor descriptor = attributeDescriptors.get(key);

            IAttributeEditor editor = null;
            try
            {
                editor = EditorFactory.getEditorFor(this.componentClazz, descriptor);
                final AttributeEditorInfo info = editor.init(descriptor);

                editorInfos.put(key, info);
                maxColumns = Math.max(maxColumns, info.columns);
            }
            catch (EditorNotFoundException ex)
            {
                /*
                 * Skip editor.
                 */
                editor = null;
            }

            editors.put(key, editor);
        }
        
        /*
         * Prepare the layout for this editor. 
         */
        final GridLayout layout = GUIFactory.zeroMarginGridLayout();
        layout.makeColumnsEqualWidth = false;
        layout.numColumns = maxColumns;
        this.setLayout(layout);

        /*
         * Create visual components for editors.
         */
        final GridDataFactory labelFactory = GridDataFactory.fillDefaults()
            .span(maxColumns, 1);

        for (String key : sortedKeys)
        {
            final AttributeDescriptor descriptor = attributeDescriptors.get(key);
            final IAttributeEditor editor = editors.get(key);
            final AttributeEditorInfo editorInfo = editorInfos.get(key);

            // Add label to editors that do not have it.
            if (editor == null || editorInfo.displaysOwnLabel == false)
            {
                final Label label = new Label(this, SWT.LEAD);
                label.setText(getLabel(descriptor));
                label.setToolTipText(getToolTip(descriptor));

                label.setLayoutData(labelFactory.create());
            }

            // Add the editor, if available.
            if (editor != null)
            {
                editor.createEditor(this, maxColumns);

                editor.setValue(attributeDescriptors.get(descriptor.key).defaultValue);
                editors.put(editor.getAttributeKey(), editor);

                /*
                 * Forward events from this editor to all our listeners.
                 */
                editor.addAttributeChangeListener(forwardListener);
            }
            else
            {
                final Label label = new Label(this, SWT.BORDER);
                label.setText("No suitable editor");
                label.setEnabled(false);

                label.setLayoutData(labelFactory.create());
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
