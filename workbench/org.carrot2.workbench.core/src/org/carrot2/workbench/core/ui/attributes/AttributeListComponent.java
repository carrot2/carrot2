package org.carrot2.workbench.core.ui.attributes;

import java.util.Map;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.editors.EditorNotFoundException;
import org.carrot2.workbench.editors.IAttributeEditor;
import org.carrot2.workbench.editors.factory.EditorFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchSite;

public class AttributeListComponent
{
    private Composite root;

    @SuppressWarnings("unchecked")
    public AttributeListComponent(IWorkbenchSite site, Composite parent, ProcessingJob job)
    {
        root = new Composite(parent, SWT.EMBEDDED | SWT.DOUBLE_BUFFERED);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
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
                    editor.init(descriptor.getValue());
                    editor.createEditor(root);
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
