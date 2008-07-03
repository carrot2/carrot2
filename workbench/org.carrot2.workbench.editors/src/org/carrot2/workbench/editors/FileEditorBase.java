package org.carrot2.workbench.editors;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public abstract class FileEditorBase extends AttributeEditorAdapter
{

    private final class SelectionListener implements Listener
    {
        public void handleEvent(Event event)
        {
            Display.getDefault().asyncExec(new Runnable()
            {
                public void run()
                {
                    String newPath = FileEditorBase.this.getFilePath();
                    if (newPath != null)
                    {
                        setCurrentFile(new File(newPath));
                        doEvent();
                    }
                }
            });
        }
    }

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

    /**
     * Subclasses should open appropriate dialog within this method and return chosen file
     * path. It is guaranteed, that this method will be called from within GUI thread.
     * 
     * @return abstract path of a chosen file or null
     * @see DirectoryDialog#open()
     * @see FileDialog#open()
     */
    protected abstract String getFilePath();

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
                setCurrentFile(null);
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

        dialogButton.addListener(SWT.Selection, new SelectionListener());
    }

    protected void doEvent()
    {
        AttributeChangedEvent event = new AttributeChangedEvent(this);
        fireAttributeChange(event);
    }

    @Override
    public void setValue(Object currentValue)
    {
        setCurrentFile((File) currentValue);
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

    protected void setCurrentFile(File currentFile)
    {
        if (currentFile != null)
        {
            pathText.setText(currentFile.getName());
            pathText.setToolTipText(currentFile.getAbsolutePath());
        }
        else
        {
            pathText.setText("");
            pathText.setToolTipText("");
        }
        directory = currentFile;
    }

}
