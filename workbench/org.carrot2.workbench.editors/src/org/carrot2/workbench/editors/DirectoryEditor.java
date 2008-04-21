package org.carrot2.workbench.editors;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

public class DirectoryEditor extends FileEditorBase
{

    @Override
    protected String getFilePath()
    {
        return new DirectoryDialog(Display.getDefault().getActiveShell()).open();
    }

}
