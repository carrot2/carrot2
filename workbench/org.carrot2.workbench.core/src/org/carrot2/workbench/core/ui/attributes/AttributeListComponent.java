package org.carrot2.workbench.core.ui.attributes;

import java.util.Map;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.editors.*;
import org.carrot2.workbench.editors.factory.EditorFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchSite;

public class AttributeListComponent
{
    private Composite root;
    private ProcessingJob processingJob;

    @SuppressWarnings("unchecked")
    public AttributeListComponent(IWorkbenchSite site, Composite parent, ProcessingJob job)
    {
        this.processingJob = job;
        AttributeChangeListener listener = new AttributeChangeListener()
        {
            public void attributeChange(AttributeChangeEvent event)
            {
                processingJob.attributes.put(event.key, event.value);
                processingJob.schedule();
            }
        };

        root = new Composite(parent, SWT.EMBEDDED | SWT.DOUBLE_BUFFERED);
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
                try
                {
                    IAttributeEditor editor =
                        EditorFactory.getEditorFor(job.algorithm, descriptor.getValue());
                    GridData data = new GridData();
                    data.grabExcessHorizontalSpace = true;
                    editor.init(descriptor.getValue());
                    if (editor.containsLabel())
                    {
                        data.horizontalSpan = 2;
                    }
                    else
                    {
                        Label l = new Label(root, SWT.NONE);
                        l.setText(descriptor.getValue().metadata.getLabel());
                        l.setLayoutData(new GridData());
                    }
                    editor.createEditor(root, data);
                    editor.setValue(descriptor.getValue().defaultValue);
                    editor.addAttributeChangeListener(listener);
                }
                catch (EditorNotFoundException ex)
                {
                    Label l = new Label(root, SWT.NONE);
                    l.setText(descriptor.getKey());
                    l.setLayoutData(new GridData());
                }
            }
        }
    }

    public Control getControl()
    {
        return root;
    }

}
