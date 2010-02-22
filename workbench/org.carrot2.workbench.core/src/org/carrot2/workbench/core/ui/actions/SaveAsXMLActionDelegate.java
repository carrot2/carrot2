
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui.actions;

import java.io.*;

import org.carrot2.core.ProcessingResult;
import org.carrot2.util.CloseableUtils;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.core.ui.SearchEditor.SaveOptions;
import org.carrot2.workbench.core.ui.SearchEditor.SaveOptions.SaveFormat;
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
        OutputStream os = null;
        try
        {
            final File destinationFile = new File(options.getFullPath());
            if (!destinationFile.exists())
            {
                destinationFile.createNewFile();
            }

            os = new FileOutputStream(destinationFile);
            if (options.format == SaveFormat.C2XML) {
                results.serialize(os, options.includeDocuments, options.includeClusters);
            } else {
                results.serializeRss(os, options.includeClusters);
            }
        }
        catch (Exception e)
        {
            Utils.showError(new Status(Status.ERROR, WorkbenchCorePlugin.PLUGIN_ID,
                "An error occurred while saving the result.", e));
        }
        finally
        {
            CloseableUtils.close(os);
        }
    }
}
