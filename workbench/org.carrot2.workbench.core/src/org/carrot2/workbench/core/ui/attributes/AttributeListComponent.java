package org.carrot2.workbench.core.ui.attributes;

import java.util.ArrayList;
import java.util.Map;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.ui.IProcessingResultPart;
import org.carrot2.workbench.editors.*;
import org.carrot2.workbench.editors.factory.EditorFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchSite;

//TODO: add common interface for all components, with dispose method
public class AttributeListComponent implements IProcessingResultPart
{
    private Composite root;
    private ProcessingJob processingJob;
    private Label descriptionText;
    private java.util.List<IAttributeEditor> editors = new ArrayList<IAttributeEditor>();

    @SuppressWarnings("unchecked")
    public void init(IWorkbenchSite site, Composite parent, ProcessingJob job)
    {
        initLayout(parent);
        this.processingJob = job;
        AttributeChangeListener listener = new AttributeChangeListener()
        {
            public void attributeChange(AttributeChangeEvent event)
            {
                processingJob.attributes.put(event.key, event.value);
                processingJob.schedule();
            }
        };

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        root.setLayout(layout);
        BindableDescriptor desc =
            BindableDescriptorBuilder.buildDescriptor(job.algorithm, true);
        desc = desc.only(Input.class, Processing.class).flatten();
        for (Map.Entry<String, AttributeDescriptor> descriptor : desc.attributeDescriptors
            .entrySet())
        {
            if (!descriptor.getValue().key.equals(AttributeNames.DOCUMENTS))
            {
                IAttributeEditor editor = null;
                try
                {
                    editor =
                        EditorFactory.getEditorFor(job.algorithm, descriptor.getValue());
                    GridData data = new GridData();
                    // data.grabExcessHorizontalSpace = true;
                    editor.init(descriptor.getValue());
                    if (editor.containsLabel())
                    {
                        data.horizontalSpan = 2;
                    }
                    else
                    {
                        Label l = new Label(root, SWT.NONE);
                        String text = getLabelForAttribute(descriptor.getValue());
                        l.setText(text);
                        l.setLayoutData(new GridData());
                        data.horizontalAlignment = SWT.FILL;
                    }
                    editor.createEditor(root, data);
                    editor.setValue(descriptor.getValue().defaultValue);
                    editor.addAttributeChangeListener(listener);
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
                    l.setText(getLabelForAttribute(descriptor.getValue()));
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

    public Control getControl()
    {
        return root.getParent().getParent();
    }

    public void initLayout(Composite parent)
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

    public void dispose()
    {
        for (IAttributeEditor editor : editors)
        {
            editor.dispose();
        }
    }
}
