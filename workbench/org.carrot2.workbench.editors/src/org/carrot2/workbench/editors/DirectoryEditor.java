package org.carrot2.workbench.editors;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class DirectoryEditor extends AttributeEditorAdapter
{

    private Image folderImage;
    private Image clearImage;
    private Text pathText;
    private File directory = null;

    @Override
    public void createEditor(Composite parent, Object layoutData)
    {
        Composite holder = new Composite(parent, SWT.NONE);
        holder.setLayoutData(layoutData);
        GridLayout gl = new GridLayout(3, false);
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        gl.horizontalSpacing = 0;
        holder.setLayout(gl);

        pathText = new Text(holder, SWT.READ_ONLY | SWT.BORDER | SWT.SINGLE);
        GridData gd1 = new GridData();
        gd1.horizontalAlignment = SWT.FILL;
        gd1.verticalAlignment = SWT.FILL;
        gd1.grabExcessHorizontalSpace = true;
        pathText.setLayoutData(gd1);

        createDialogButton(holder);

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
                setCurrentDir(null);
                doEvent();
            }
        });
    }

    private void createDialogButton(Composite holder)
    {
        Button dialogButton = new Button(holder, SWT.PUSH | SWT.CENTER | SWT.FLAT);
        folderImage = EditorsPlugin.getImageDescriptor("icons/folder.gif").createImage();
        dialogButton.setImage(folderImage);
        GridData gd2 = new GridData();
        gd2.horizontalAlignment = SWT.FILL;
        gd2.verticalAlignment = SWT.FILL;
        dialogButton.setLayoutData(gd2);

        dialogButton.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event event)
            {
                Display.getDefault().asyncExec(new Runnable()
                {
                    public void run()
                    {
                        DirectoryDialog dialog =
                            new DirectoryDialog(Display.getDefault().getActiveShell());
                        String newPath = dialog.open();
                        if (newPath != null)
                        {
                            setCurrentDir(new File(newPath));
                            doEvent();
                        }
                    }
                });
            }
        });
    }

    //TODO: add subclass with method doEvent :/
    private void doEvent()
    {
        AttributeChangeEvent event = new AttributeChangeEvent(this);
        fireAttributeChange(event);
    }

    @Override
    public void setValue(Object currentValue)
    {
        setCurrentDir((File) currentValue);
    }

    @Override
    public Object getValue()
    {
        return directory;
    }

    @Override
    public void dispose()
    {
        folderImage.dispose();
        clearImage.dispose();
    }

    private void setCurrentDir(File currentDir)
    {
        if (currentDir != null)
        {
            pathText.setText(currentDir.getName());
            pathText.setToolTipText(currentDir.getAbsolutePath());
        }
        else
        {
            pathText.setText("");
            pathText.setToolTipText("");
        }
        directory = currentDir;
    }
}
