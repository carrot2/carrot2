package org.carrot2.workbench.core.ui.attributes;

import java.util.*;

import org.apache.commons.lang.NullArgumentException;
import org.carrot2.core.ProcessingComponent;
import org.carrot2.util.attribute.*;
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
    private ProcessingComponent component;
    private Composite root;
    private java.util.List<IAttributeEditor> editors = new ArrayList<IAttributeEditor>();
    private AttributesControlConfiguration conf;

    public AttributesPage(ProcessingComponent component,
        AttributesControlConfiguration configuration)
    {

        if (configuration == null)
        {
            throw new NullArgumentException("configuration");
        }
        if (component == null)
        {
            throw new NullArgumentException("component");
        }
        this.component = component;
        this.descriptor = BindableDescriptorBuilder.buildDescriptor(component);
        this.flatDescriptor = descriptor.flatten();
        this.conf = configuration;
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
        if (conf.filterAnnotations != null)
        {
            desc = desc.only(conf.getFilterAnnotationsArray());
        }
        Map<String, AttributeDescriptor> attDescriptors = null;
        desc = desc.flatten();
        if (conf.groupingMethod != null)
        {
            if (!desc.groupedBy.equals(conf.groupingMethod))
            {
                desc = desc.group(conf.groupingMethod);
            }
            if (desc.attributeGroups.containsKey(conf.filterGroupKey))
            {
                attDescriptors =
                    desc.group(conf.groupingMethod).attributeGroups
                        .get(conf.filterGroupKey);
            }
            else
            {
                Utils.logError("Group with key " + conf.filterGroupKey + " not found",
                    null, false);
            }
        }
        if (attDescriptors == null)
        {
            attDescriptors = desc.attributeDescriptors;
        }
        for (Map.Entry<String, AttributeDescriptor> entry : attDescriptors.entrySet())
        {
            AttributeDescriptor attDescriptor = entry.getValue();
            if (!conf.ignoredAttributes.contains(attDescriptor.key))
            {
                IAttributeEditor editor = null;
                try
                {
                    editor = EditorFactory.getEditorFor(component, attDescriptor);
                    GridData data =
                        new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
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
        holder.setLayout(new GridLayout());
        root.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));
        scroll.setContent(holder);
    }

    private Object getInitialValue(String key)
    {
        return flatDescriptor.attributeDescriptors.get(key).defaultValue;
    }

}
