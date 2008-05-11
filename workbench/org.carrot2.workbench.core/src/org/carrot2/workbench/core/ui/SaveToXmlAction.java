package org.carrot2.workbench.core.ui;

import java.io.*;

import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class SaveToXmlAction extends Action
{
    @Override
    public void run()
    {
        SaveToXmlDialog dialog =
            new SaveToXmlDialog(Display.getDefault().getActiveShell());
        int status = dialog.open();
        if (status == Window.CANCEL)
        {
            return;
        }
        ResultsEditor results =
            (ResultsEditor) CorePlugin.getDefault().getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        try
        {
            File destinationFile = new File(dialog.getFilePath());
            if (!destinationFile.exists())
            {
                destinationFile.createNewFile();
            }
            Writer writer = new FileWriter(destinationFile);
            results.getCurrentContent().serialize(writer, dialog.saveDocuments(),
                dialog.saveClusters());
            writer.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Save to XML action failed.", e);
        }
    }

    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return CorePlugin.imageDescriptorFromPlugin("org.eclipse.ui",
            "icons/full/etool16/save_edit.gif");
    }
}
