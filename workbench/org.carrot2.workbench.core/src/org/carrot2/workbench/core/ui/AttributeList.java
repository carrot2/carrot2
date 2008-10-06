package org.carrot2.workbench.core.ui;

import java.text.Collator;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.carrot2.workbench.editors.factory.EditorFactory;
import org.carrot2.workbench.editors.factory.EditorNotFoundException;
import org.eclipse.jface.fieldassist.*;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * An SWT composite displaying an alphabetically ordered list of {@link IAttributeEditor}s.
 */
public final class AttributeList extends Composite implements IAttributeEventProvider
{
    /**
     * Space before the editor label (if separate).
     */
    public final static int SPACE_BEFORE_LABEL = 5;

    /**
     * A list of {@link AttributeDescriptor}s, indexed by their keys.
     */
    private final Map<String, AttributeDescriptor> attributeDescriptors;

    /**
     * A map between attribute keys and {@link IAttributeEditor}s visible in this
     * component.
     */
    private Map<String, IAttributeEditor> editors = Maps.newHashMap();

    /**
     * Optional component class attribute descriptors come from.
     */
    private Class<? extends ProcessingComponent> componentClazz;

    /**
     * Attribute change listeners.
     */
    private final List<IAttributeListener> listeners = new CopyOnWriteArrayList<IAttributeListener>();

    /**
     * Forward events from editors to external listeners.
     */
    private final IAttributeListener forwardListener = new ForwardingAttributeListener(
        listeners);

    /** */
    private BindableDescriptor bindable;

    /** */
    private IAttributeEventProvider globalEventsProvider;

    /**
     * Create a new editor.
     */
    @SuppressWarnings("unchecked")
    public AttributeList(Composite parent, BindableDescriptor bindable,
        Map<String, AttributeDescriptor> attributeDescriptors,
        IAttributeEventProvider globalEventsProvider, Map<String, Object> currentValues)
    {
        super(parent, SWT.NONE);

        this.bindable = bindable;
        this.globalEventsProvider = globalEventsProvider;
        this.attributeDescriptors = attributeDescriptors;

        /*
         * Only store component clazz if it is assignable to {@link ProcessingComponent}.
         */
        Class<?> clazz = bindable.type;
        if (clazz != null && ProcessingComponent.class.isAssignableFrom(clazz))
        {
            this.componentClazz = (Class<? extends ProcessingComponent>) clazz;
        }

        createComponents(currentValues);
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
    public void addAttributeListener(IAttributeListener listener)
    {
        this.listeners.add(listener);
    }

    /*
     * 
     */
    public void removeAttributeListener(IAttributeListener listener)
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
            editor.removeAttributeListener(forwardListener);
        }

        super.dispose();
    }

    /**
     * Set the focus to the editor containing {@link AttributeNames#QUERY}, if possible.
     */
    @Override
    public boolean setFocus()
    {
        if (editors.containsKey(AttributeNames.QUERY))
        {
            editors.get(AttributeNames.QUERY).setFocus();
            return true;
        }
        else return false;
    }

    /**
     * Create internal GUI.
     */
    private void createComponents(Map<String, Object> currentValues)
    {
        /*
         * Sort alphabetically by label.
         */
        final Locale locale = Locale.getDefault();
        final Map<String, String> labels = Maps.newHashMap();
        for (Map.Entry<String, AttributeDescriptor> entry : attributeDescriptors
            .entrySet())
        {
            labels.put(entry.getKey(), getLabel(entry.getValue()).toLowerCase(locale));
        }

        final Collator collator = Collator.getInstance(locale);
        final List<String> sortedKeys = Lists.newArrayList(labels.keySet());
        Collections.sort(sortedKeys, new Comparator<String>()
        {
            public int compare(String a, String b)
            {
                return collator.compare(labels.get(a), labels.get(b));
            }
        });

        /*
         * Create editors and inquire about their layout needs.
         */
        editors = Maps.newHashMap();
        final Map<String, AttributeEditorInfo> editorInfos = Maps.newHashMap();

        int maxColumns = 1;
        for (String key : sortedKeys)
        {
            final AttributeDescriptor descriptor = attributeDescriptors.get(key);

            IAttributeEditor editor = null;
            try
            {
                editor = EditorFactory.getEditorFor(this.componentClazz, descriptor);
                final AttributeEditorInfo info = editor.init(bindable, descriptor,
                    globalEventsProvider, currentValues);

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
        final GridDataFactory labelFactory = GridDataFactory.fillDefaults().span(
            maxColumns, 1);

        boolean firstEditor = true;
        for (String key : sortedKeys)
        {
            final AttributeDescriptor descriptor = attributeDescriptors.get(key);
            final IAttributeEditor editor = editors.get(key);
            final AttributeEditorInfo editorInfo = editorInfos.get(key);

            if (editor == null)
            {
                // Skip attributes without the editor.
                continue;
            }

            // Add label to editors that do not have it.
            if (!editorInfo.displaysOwnLabel)
            {
                final Label label = new Label(this, SWT.LEAD);
                final GridData gd = labelFactory.create();
                if (!firstEditor)
                {
                    gd.verticalIndent = SPACE_BEFORE_LABEL;
                }
                label.setLayoutData(gd);

                label.setText(getLabel(descriptor)
                    + (descriptor.requiredAttribute ? " (required)" : ""));

                /*
                 * Add validation overlay.
                 */
                {
                    final ControlDecoration decoration = new ControlDecoration(label,
                        SWT.LEFT | SWT.BOTTOM);

                    FieldDecoration requiredDecoration = FieldDecorationRegistry
                        .getDefault().getFieldDecoration(
                            FieldDecorationRegistry.DEC_WARNING);

                    decoration.setImage(requiredDecoration.getImage());
                    decoration.setDescriptionText("Invalid value");
                    decoration.hide();

                    final IAttributeListener validationListener = 
                        new InvalidStateDecorationListener(decoration, descriptor);

                    globalEventsProvider.addAttributeListener(validationListener);
                    editor.addAttributeListener(validationListener);

                    label.addDisposeListener(new DisposeListener()
                    {
                        public void widgetDisposed(DisposeEvent e)
                        {
                            globalEventsProvider.removeAttributeListener(validationListener);
                            editor.removeAttributeListener(validationListener);
                            decoration.dispose();
                        }
                    });                    
                }

                AttributeInfoTooltip.attach(label, descriptor);
            }

            // Add the editor, if available.
            editor.createEditor(this, maxColumns);

            editor.setValue(attributeDescriptors.get(descriptor.key).defaultValue);
            editors.put(editor.getAttributeKey(), editor);

            /*
             * Forward events from this editor to all our listeners.
             */
            editor.addAttributeListener(forwardListener);

            firstEditor = false;
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
}
