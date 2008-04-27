package org.carrot2.workbench.core.ui.attributes;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Map;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.attribute.*;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.editors.*;
import org.carrot2.workbench.editors.factory.EditorFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.Page;

public class AttributesPage extends Page
{

    private BindableDescriptor descriptor;
    private Map<String, Object> attributes;
    private ProcessingComponent component;
    private Composite root;
    private Label descriptionText;
    private java.util.List<IAttributeEditor> editors = new ArrayList<IAttributeEditor>();

    public AttributesPage(ProcessingComponent component, Map<String, Object> attributes,
        Class<? extends Annotation>... annotationClasses)
    {
        this.component = component;
        this.descriptor =
            BindableDescriptorBuilder.buildDescriptor(component).only(annotationClasses)
                .flatten();
        this.attributes = attributes;
    }

    @Override
    public void createControl(Composite parent)
    {
        initLayout(parent);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        root.setLayout(layout);
        for (Map.Entry<String, AttributeDescriptor> entry : descriptor.attributeDescriptors
            .entrySet())
        {
            AttributeDescriptor attDescriptor = entry.getValue();
            if (!attDescriptor.key.equals(AttributeNames.DOCUMENTS))
            {
                IAttributeEditor editor = null;
                try
                {
                    editor = EditorFactory.getEditorFor(component, attDescriptor);
                    GridData data = new GridData();
                    // data.grabExcessHorizontalSpace = true;
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
        return root.getParent().getParent();
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
        descriptionText = new Label(holder, SWT.WRAP);
        Label Label_1 = new Label(holder, SWT.HORIZONTAL | SWT.SEPARATOR);
        root = new Composite(holder, SWT.NULL);

        FormData FormData_3 = new FormData();
        FormData FormData_2 = new FormData();
        FormData FormData_1 = new FormData();

        FormData_3.right = new FormAttachment(100, -5);
        FormData_3.height = 100;
        FormData_3.left = new FormAttachment(0, 5);
        FormData_3.bottom = new FormAttachment(100, -5);
        FormData_2.right = new FormAttachment(100, 0);
        FormData_2.height = -1;
        FormData_2.left = new FormAttachment(0, 0);
        FormData_2.bottom = new FormAttachment(descriptionText, 0, 0);
        FormData_1.right = new FormAttachment(100, 0);
        FormData_1.top = new FormAttachment(0, 0);
        FormData_1.left = new FormAttachment(0, 0);
        FormData_1.bottom = new FormAttachment(Label_1, 0, 0);

        descriptionText.setLayoutData(FormData_3);
        Label_1.setLayoutData(FormData_2);
        root.setLayoutData(FormData_1);
        Label_1.setVisible(true);
        holder.setLayout(new FormLayout());

        scroll.setLayout(new FillLayout());

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
            return descriptor.attributeDescriptors.get(key).defaultValue;
        }
    }

}
