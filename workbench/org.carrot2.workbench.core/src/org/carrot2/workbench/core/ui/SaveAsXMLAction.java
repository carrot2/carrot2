package org.carrot2.workbench.core.ui;

import java.io.*;

import org.carrot2.core.ProcessingResult;
import org.carrot2.util.CloseableUtils;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Saves the set of clusters and documents from the currently active editor in an XML
 * format.
 */
public final class SaveAsXMLAction extends Action
{
    /**
     * 
     */
    @Override
    public void run()
    {
        final IEditorPart editor = Utils.getActiveEditor();

        if (editor == null || !(editor instanceof SearchEditor))
        {
            return;
        }

        final SearchEditor searchResultsEditor = (SearchEditor) editor;
        final ProcessingResult results = searchResultsEditor.getSearchResult()
            .getProcessingResult();

        final SaveAsXMLDialog dialog = new SaveAsXMLDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
            searchResultsEditor.getPartName());

        int status = dialog.open();
        if (status == Window.CANCEL)
        {
            return;
        }

        Writer writer = null;
        try
        {
            final File destinationFile = new File(dialog.getFilePath());
            if (!destinationFile.exists())
            {
                destinationFile.createNewFile();
            }

            writer = new OutputStreamWriter(new FileOutputStream(destinationFile),
                "UTF-8");

            results.serialize(writer, dialog.saveDocuments(), dialog.saveClusters());
        }
        catch (Exception e)
        {
            Utils.showError(new Status(Status.ERROR, WorkbenchCorePlugin.PLUGIN_ID,
                "An error occurred while saving the result.", e));
        }
        finally
        {
            CloseableUtils.close(writer);
        }
    }

    @Override
    public ImageDescriptor getImageDescriptor()
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.ui",
            "icons/full/etool16/save_edit.gif");
    }
}
