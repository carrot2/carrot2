package org.carrot2.workbench.editors;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

public class FileEditor extends FileEditorBase
{

    @Override
    protected String getFilePath()
    {
        return new FileDialog(Display.getDefault().getActiveShell()).open();
    }

}
