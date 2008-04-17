package org.carrot2.workbench.editors;

import static org.apache.commons.lang.StringUtils.abbreviate;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class DirectoryEditor extends AttributeEditorAdapter
{

    private Image folderImage;
    private Text pathText;

    @Override
    public void createEditor(Composite parent, Object layoutData)
    {
        Composite holder = new Composite(parent, SWT.NONE);
        holder.setLayoutData(layoutData);
        GridLayout gl = new GridLayout(2, false);
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        holder.setLayout(gl);

        pathText = new Text(holder, SWT.READ_ONLY | SWT.BORDER | SWT.SINGLE);
        pathText.setTabs(50);
        GridData gd1 = new GridData();
        gd1.horizontalAlignment = SWT.FILL;
        gd1.grabExcessHorizontalSpace = true;
        pathText.setLayoutData(gd1);

        Button dialogButton = new Button(holder, SWT.PUSH | SWT.CENTER);
        folderImage = EditorsPlugin.getImageDescriptor("icons/folder.gif").createImage();
        dialogButton.setImage(folderImage);
        dialogButton.setLayoutData(new GridData());

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
                        pathText.setText(abbreviate(newPath, newPath.length() - 37, 40));
                    }
                });
            }
        });

    }

    @Override
    public void setValue(Object currentValue)
    {
        if (currentValue != null)
        {
            File f = (File) currentValue;
            pathText.setText(abbreviate(f.getAbsolutePath(),
                f.getAbsolutePath().length() - 40, 40));
        }
    }
}
