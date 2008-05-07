package org.carrot2.workbench.core.ui.attributes;

import java.lang.annotation.Annotation;
import java.util.*;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.editors.*;
import org.carrot2.workbench.editors.factory.EditorFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableEditor;
import org.eclipse.ui.part.Page;

/**
 * 
 */
public class AttributesPage extends Page implements IPersistableEditor
{

    private BindableDescriptor descriptor;
    private BindableDescriptor flatDescriptor;
    private Map<String, Object> attributes;
    private ProcessingComponent component;
    private Composite root;
    private java.util.List<IAttributeEditor> editors = new ArrayList<IAttributeEditor>();
    private java.util.List<String> ignoredAttributes = new ArrayList<String>();
    private Class<? extends Annotation> [] filterAnnotations;
    private Object filterGroupKey;
    private GroupingMethod groupingMethod;

    public AttributesPage(ProcessingComponent component, Map<String, Object> attributes)
    {
        this.component = component;
        this.descriptor = BindableDescriptorBuilder.buildDescriptor(component);
        this.flatDescriptor = descriptor.flatten();
        this.attributes = attributes;
    }

    /**
     * Creates editors for attributes of component given in constructor. You can filter
     * attributes using {@link AttributesPage#filterAttributes(Class...)}. If you want
     * certain attributes not to be shown, you can also use
     * {@link AttributesPage#ignoreAttributes(String...)} method.
     */
    @Override
    public void createControl(Composite parent)
    {
        initLayout(parent);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        root.setLayout(layout);
        BindableDescriptor desc = descriptor;
        if (filterAnnotations != null)
        {
            desc = desc.only(filterAnnotations);
        }
        Map<String, AttributeDescriptor> attDescriptors = null;
        desc = desc.flatten();
        if (groupingMethod != null)
        {
            desc = desc.group(groupingMethod);
            if (desc.attributeGroups.containsKey(filterGroupKey))
            {
                attDescriptors =
                    desc.group(groupingMethod).attributeGroups.get(filterGroupKey);
            }
            else
            {
                Utils.logError("Group with key " + filterGroupKey + " not found", null,
                    false);
            }
        }
        if (attDescriptors == null)
        {
            attDescriptors = desc.attributeDescriptors;
        }
        for (Map.Entry<String, AttributeDescriptor> entry : attDescriptors.entrySet())
        {
            AttributeDescriptor attDescriptor = entry.getValue();
            if (!ignoredAttributes.contains(attDescriptor.key))
            {
                IAttributeEditor editor = null;
                try
                {
                    editor = EditorFactory.getEditorFor(component, attDescriptor);
                    GridData data = new GridData();
                    data.grabExcessHorizontalSpace = true;
                    editor.init(attDescriptor);
                    if (editor.containsLabel())
                    {
                        data.horizontalSpan = 2;
                    }
                    else
                    {
                        Label l = new Label(root, SWT.NONE);
                        String text = getLabelForAttribute(attDescriptor);
                        l.setText(text);
                        l.setLayoutData(new GridData());
                        data.horizontalAlignment = SWT.FILL;
                    }
                    editor.createEditor(root, data);
                    editor.setValue(getInitialValue(attDescriptor.key));
                    //editor.addAttributeChangeListener(listener);
                    editors.add(editor);
                    // if (descriptor.getValue().metadata.getDescription() != null)
                    // {
                    // descriptionText.setText(descriptor.getValue().metadata
                    // .getDescription());
                    // }
                }
                catch (EditorNotFoundException ex)
                {
                    Utils.logError(ex, false);
                    Label l = new Label(root, SWT.NONE);
                    l.setText(getLabelForAttribute(attDescriptor));
                    GridData gd = new GridData();
                    gd.horizontalSpan = 2;
                    l.setLayoutData(gd);
                    if (editor != null)
                    {
                        editor.dispose();
                    }
                }
            }
        }
        root.getParent().setSize(root.getParent().computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    @Override
    public Control getControl()
    {
        //BugFixed(CARROT-210)
        if (!root.isDisposed() && !root.getParent().isDisposed()
            && !root.getParent().getParent().isDisposed())
        {
            return root.getParent().getParent();
        }
        else
        {
            return null;
        }
    }

    @Override
    public void setFocus()
    {
    }

    @Override
    public void dispose()
    {
        for (IAttributeEditor editor : editors)
        {
            editor.dispose();
        }
        super.dispose();
    }

    public void restoreState(IMemento memento)
    {
        for (IAttributeEditor editor : editors)
        {
            IMemento editorMemento = memento.getChild(editor.getAttributeKey());
            if (editorMemento != null)
            {
                editor.restoreState(editorMemento);
            }
        }
    }

    public void saveState(IMemento memento)
    {
        for (IAttributeEditor editor : editors)
        {
            IMemento editorMemento = memento.createChild(editor.getAttributeKey());
            editor.saveState(editorMemento);
        }
    }

    /**
     * Attributes with the given key will not be shown in the page.
     * <p>
     * Subsequent calls of this method override previous ones.
     * </p>
     * <p>
     * Those {@code keys} are used inside {@link AttributesPage#createControl(Composite)}
     * method. Setting them after calling this method has no effect.
     * </p>
     * 
     * @param keys
     */
    public void ignoreAttributes(String... keys)
    {
        ignoredAttributes.clear();
        for (int i = 0; i < keys.length; i++)
        {
            String key = keys[i];
            ignoredAttributes.add(key);
        }
    }

    /**
     * Only attributes with the given annotations will be shown in the page.
     * <p>
     * Subsequent calls of this method override previous ones.
     * </p>
     * <p>
     * This filter is used inside {@link AttributesPage#createControl(Composite)} method.
     * Setting the filter after calling this method has no effect.
     * </p>
     * 
     * @param annotationClasses
     * @see BindableDescriptor#only(Class...)
     */
    public void filterAttributes(Class<? extends Annotation>... annotationClasses)
    {
        filterAnnotations = annotationClasses;
    }

    /**
     * Only attributes inside given groups will be shown in the page.
     * <p>
     * Subsequent calls of this method override previous ones.
     * </p>
     * <p>
     * This filter is used inside {@link AttributesPage#createControl(Composite)} method.
     * Setting the filter after calling this method has no effect.
     * </p>
     * 
     * @param method grouping method
     * @param keys keys of the groups, that should be shown
     * @see GroupingMethod
     * @see BindableDescriptor#group(GroupingMethod)
     */
    public void filterGroup(GroupingMethod method, Object key)
    {
        this.filterGroupKey = key;
        this.groupingMethod = method;
    }

    public void addAttributeChangeListener(AttributeChangeListener listener)
    {
        for (IAttributeEditor editor : editors)
        {
            editor.addAttributeChangeListener(listener);
        }
    }

    public void removeAttributeChangeListener(AttributeChangeListener listener)
    {
        for (IAttributeEditor editor : editors)
        {
            editor.removeAttributeChangeListener(listener);
        }
    }

    public Map<String, Object> getAttributeValues()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        for (IAttributeEditor editor : editors)
        {
            map.put(editor.getAttributeKey(), editor.getValue());
        }
        return Collections.unmodifiableMap(map);
    }

    private String getLabelForAttribute(AttributeDescriptor descriptor)
    {
        String text = null;
        if (descriptor.metadata != null)
        {
            text = descriptor.metadata.getLabelOrTitle();
        }
        text = text != null ? text : "Attribute without label nor title :/";
        return text;
    }

    private void initLayout(Composite parent)
    {
        final ScrolledComposite scroll =
            new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        final Composite holder = new Composite(scroll, SWT.NULL);
        root = new Composite(holder, SWT.NULL);
        scroll.setLayout(new FillLayout());
        holder.setLayout(new FillLayout());
        scroll.setContent(holder);
    }

    private Object getInitialValue(String key)
    {
        if (attributes.containsKey(key))
        {
            return attributes.get(key);
        }
        else
        {
            return flatDescriptor.attributeDescriptors.get(key).defaultValue;
        }
    }

}
