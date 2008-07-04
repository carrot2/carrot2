package org.carrot2.workbench.core.ui.actions;

import java.io.*;

import org.carrot2.core.ProcessingResult;
import org.carrot2.util.CloseableUtils;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.core.ui.SearchEditor.SaveOptions;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;

/**
 * Saves the set of clusters and documents from the currently active editor in an XML
 * format.
 */
public final class SaveAsXMLActionDelegate extends Action
{
    private final ProcessingResult results;
    private final SaveOptions options;

    /*
     * 
     */
    public SaveAsXMLActionDelegate(ProcessingResult result, SearchEditor.SaveOptions options)
    {
        this.results = result;
        this.options = options;
    }

    /**
     * 
     */
    @Override
    public void run()
    {
        Writer writer = null;
        try
        {
            final File destinationFile = new File(options.getFullPath());
            if (!destinationFile.exists())
            {
                destinationFile.createNewFile();
            }

            writer = new OutputStreamWriter(new FileOutputStream(destinationFile),
                "UTF-8");

            results.serialize(writer, options.includeDocuments, options.includeClusters);
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
}
