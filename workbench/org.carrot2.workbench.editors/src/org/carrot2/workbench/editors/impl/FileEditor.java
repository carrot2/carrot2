package org.carrot2.workbench.editors.impl;

import java.io.File;

import org.carrot2.util.attribute.constraint.IsDirectory;
import org.carrot2.util.attribute.constraint.IsFile;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * Attribute editor for files and directories. By default, the editor allows
 * file selection ({@link IsFile}). {@link IsDirectory} constraint can be used to
 * limit selection to directories only.
 * 
 * TODO: fix event propagation, add support for constraints.
 */
public class FileEditor extends AttributeEditorAdapter
{
    private final class SelectionListener implements Listener
    {
        public void handleEvent(Event event)
        {
            Display.getDefault().asyncExec(new Runnable()
            {
                public void run()
                {
                    String newPath = openFileDialog();
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

    /*
     * 
     */
    public FileEditor()
    {
        super(new AttributeEditorInfo(1, false));
    }

    /*
     * 
     */
    @Override
    public void createEditor(Composite parent, int gridColumns)
    {
        Composite holder = new Composite(parent, SWT.NONE);
        holder.setLayoutData(GUIFactory.editorGridData()
            .grab(true, false).span(gridColumns, 1).create());

        final GridLayout gl = new GridLayout(3, false);
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        gl.horizontalSpacing = 0;
        holder.setLayout(gl);

        pathText = new Text(holder, SWT.READ_ONLY | SWT.BORDER | SWT.SINGLE);
        final GridData gd1 = new GridData();
        gd1.horizontalAlignment = SWT.FILL;
        gd1.verticalAlignment = SWT.FILL;
        gd1.grabExcessHorizontalSpace = true;
        pathText.setLayoutData(gd1);

        createDialogButton(holder);
        createClearButton(holder);
    }

    /*
     * 
     */
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

    /*
     * 
     */
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

    /*
     * 
     */
    protected void doEvent()
    {
        AttributeChangedEvent event = new AttributeChangedEvent(this);
        fireAttributeChange(event);
    }

    /*
     * 
     */
    @Override
    public void setValue(Object currentValue)
    {
        setCurrentFile((File) currentValue);
    }

    /*
     * 
     */
    @Override
    public Object getValue()
    {
        return directory;
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        folderImage.dispose();
        clearImage.dispose();
    }

    /*
     * 
     */
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

    /*
     * 
     */
    protected String openFileDialog()
    {
        // new DirectoryDialog(Display.getDefault().getActiveShell()).open();
        return new FileDialog(Display.getDefault().getActiveShell()).open();
    }
}
