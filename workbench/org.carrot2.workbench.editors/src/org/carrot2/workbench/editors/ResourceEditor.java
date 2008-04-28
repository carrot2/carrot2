package org.carrot2.workbench.editors;

import java.io.File;

import org.carrot2.util.resource.FileResource;
import org.carrot2.util.resource.Resource;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class ResourceEditor extends AttributeEditorAdapter
{

    private Image fileImage;
    private Image urlImage;
    private Image clearImage;
    private Text resourceText;
    private Resource resource = null;

    @Override
    public void createEditor(Composite parent, Object layoutData)
    {
        Composite holder = new Composite(parent, SWT.NONE);
        holder.setLayoutData(layoutData);
        GridLayout gl = new GridLayout(4, false);
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        gl.horizontalSpacing = 0;
        holder.setLayout(gl);

        resourceText = new Text(holder, SWT.READ_ONLY | SWT.BORDER | SWT.SINGLE);
        GridData gd1 = new GridData();
        gd1.horizontalAlignment = SWT.FILL;
        gd1.verticalAlignment = SWT.FILL;
        gd1.grabExcessHorizontalSpace = true;
        resourceText.setLayoutData(gd1);

        createFileButton(holder);

        createUrlButton(holder);

        createClearButton(holder);

    }

    private void createClearButton(Composite holder)
    {
        Button clearButton = new Button(holder, SWT.PUSH | SWT.CENTER | SWT.FLAT);
        clearImage =
            EditorsPlugin.getImageDescriptor("icons/delete_edit.gif").createImage();
        clearButton.setImage(clearImage);
        GridData gd2 = new GridData();
        gd2.horizontalAlignment = SWT.FILL;
        gd2.verticalAlignment = SWT.FILL;
        clearButton.setLayoutData(gd2);

        clearButton.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event event)
            {
                setCurrentResource(null);
                doEvent();
            }
        });
    }

    private void createFileButton(Composite holder)
    {
        Button dialogButton = new Button(holder, SWT.PUSH | SWT.CENTER | SWT.FLAT);
        fileImage = EditorsPlugin.getImageDescriptor("icons/folder.gif").createImage();
        dialogButton.setImage(fileImage);
        GridData gd2 = new GridData();
        gd2.horizontalAlignment = SWT.FILL;
        gd2.verticalAlignment = SWT.FILL;
        dialogButton.setLayoutData(gd2);

        dialogButton.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event event)
            {
                Utils.asyncExec(new Runnable()
                {
                    public void run()
                    {
                        FileDialog dialog =
                            new FileDialog(Display.getCurrent().getActiveShell());
                        dialog.setFilterExtensions(new String []
                        {
                            "*.xml", "*.*"
                        });
                        dialog.setFilterNames(new String []
                        {
                            "XML files", "All"
                        });
                        String chosenPath = dialog.open();
                        if (chosenPath != null)
                        {
                            FileResource res = new FileResource(new File(chosenPath));
                            setCurrentResource(res);
                        }
                    }
                });
            }
        });
    }

    private void createUrlButton(Composite holder)
    {
        Button dialogButton = new Button(holder, SWT.PUSH | SWT.CENTER | SWT.FLAT);
        urlImage = EditorsPlugin.getImageDescriptor("icons/web.gif").createImage();
        dialogButton.setImage(urlImage);
        GridData gd2 = new GridData();
        gd2.horizontalAlignment = SWT.FILL;
        gd2.verticalAlignment = SWT.FILL;
        dialogButton.setLayoutData(gd2);

        dialogButton.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event event)
            {

            }
        });
    }

    protected void doEvent()
    {
        AttributeChangeEvent event = new AttributeChangeEvent(this);
        fireAttributeChange(event);
    }

    @Override
    public void setValue(Object currentValue)
    {
        setCurrentResource((Resource) currentValue);
    }

    @Override
    public Object getValue()
    {
        return resource;
    }

    @Override
    public void dispose()
    {
        fileImage.dispose();
        clearImage.dispose();
    }

    protected void setCurrentResource(Resource currentRes)
    {
        if (currentRes != null)
        {
            resourceText.setText(currentRes.toString());
        }
        else
        {
            resourceText.setText("");
        }
        resource = currentRes;
    }

}
