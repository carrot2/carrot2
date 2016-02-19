
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui.actions;

import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.ProcessingResult;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.xslt.NopURIResolver;
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
    private final Transformer transformer;
    
    /*
     * 
     */
    public SaveAsXMLActionDelegate(ProcessingResult result, SearchEditor.SaveOptions options)
    {
        this.results = result;
        this.options = options;
        
        final TransformerFactory tFactory = TransformerFactory.newInstance();
        tFactory.setURIResolver(new NopURIResolver());
        
        final String xslt = System.getProperty("carrot2.workbench.save-as-xml.xslt");
        InputStream xsltStream = null;
        
        Transformer t = null;
        try
        {
            if (StringUtils.isNotBlank(xslt))
            {
                try
                {
                    xsltStream = new FileInputStream(xslt);
                    t = tFactory.newTransformer(new StreamSource(xsltStream));
                }
                catch (FileNotFoundException e)
                {
                    Utils.showError(new Status(Status.WARNING, WorkbenchCorePlugin.PLUGIN_ID,
                        "Could not XSLT stylesheet", e));
                    t = tFactory.newTransformer();
                }
                finally {
                    CloseableUtils.close(xsltStream);
                }
            }
            else
            {
                t = tFactory.newTransformer();
            }
        }
        catch (TransformerConfigurationException e1)
        {
            throw ExceptionUtils.wrapAsRuntimeException(e1);
        }
        
        transformer = t;
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

            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            
            os = new FileOutputStream(destinationFile);
            results.serialize(output, options.includeDocuments, options.includeClusters, options.includeAttributes);
            transformer.transform(new StreamSource(new ByteArrayInputStream(output
                .toByteArray())), new StreamResult(os));
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
