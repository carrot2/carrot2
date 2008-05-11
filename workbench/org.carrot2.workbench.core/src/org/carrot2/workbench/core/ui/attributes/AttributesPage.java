package org.carrot2.workbench.core.ui.attributes;

import java.util.*;

import org.apache.commons.lang.NullArgumentException;
import org.carrot2.core.ProcessingComponent;
import org.carrot2.util.attribute.AttributeDescriptor;
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
    private final Map<String, AttributeDescriptor> attributeDescriptors;
    private Class<? extends ProcessingComponent> clazz;
    private Composite root;
    private java.util.List<IAttributeEditor> editors = new ArrayList<IAttributeEditor>();

    public AttributesPage(Class<? extends ProcessingComponent> componentClass,
        Map<String, AttributeDescriptor> attributeDescriptors)
    {
        if (componentClass == null)
        {
            throw new NullArgumentException("component");
        }
        this.clazz = componentClass;
        this.attributeDescriptors = attributeDescriptors;
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
        layout.marginWidth = 0;
        root.setLayout(layout);
        for (Map.Entry<String, AttributeDescriptor> entry : attributeDescriptors
            .entrySet())
        {
            AttributeDescriptor attDescriptor = entry.getValue();
            IAttributeEditor editor = null;
            try
            {
                editor = EditorFactory.getEditorFor(clazz, attDescriptor);
                GridData data = new GridData(GridData.FILL, GridData.FILL, true, false);
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
        root.setSize(root.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    @Override
    public Control getControl()
    {
        //BugFixed(CARROT-210)
        if (!root.isDisposed() && !root.getParent().isDisposed())
        //&& !root.getParent().getParent().isDisposed())
        {
            return root;
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
        root = new Composite(parent, SWT.NONE);
    }

    private Object getInitialValue(String key)
    {
        return attributeDescriptors.get(key).defaultValue;
    }

}
