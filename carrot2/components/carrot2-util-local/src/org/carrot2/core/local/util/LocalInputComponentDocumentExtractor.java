
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.local.util;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.impl.*;

/**
 * @author Stanislaw Osinski
 */
public class LocalInputComponentDocumentExtractor
{
    /** */
    private LocalController localController;

    public LocalInputComponentDocumentExtractor(
        LocalComponentFactory localInputComponentFactory)
    {
        // Prepare the controller
        localController = new LocalControllerBase();

        // Input component factory
        localController.addLocalComponentFactory("input",
            localInputComponentFactory);

        // Cluster consumer output component
        LocalComponentFactory documentConsumerOutputFactory = new LocalComponentFactoryBase()
        {
            public LocalComponent getInstance()
            {
                return new DocumentsConsumerOutputComponent();
            }
        };
        localController.addLocalComponentFactory("output",
            documentConsumerOutputFactory);

        // Add process
        try
        {
            LocalProcessBase documentExtractor = new LocalProcessBase("input",
                "output", new String [] {}, "Document extractor", "");
            localController.addProcess("document-extractor", documentExtractor);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Cannot initialize local process", e);
        }
    }

    /**
     * @param query
     */
    public List getDocuments(String query)
    {
        return getDocuments(query, new HashMap());
    }
    
    /**
     * @param query
     * @param parameters
     */
    public List getDocuments(String query, Map parameters)
    {
        ProcessingResult result;
        try
        {
            result = localController.query("document-extractor", query,
                parameters);
        }
        catch (MissingProcessException e)
        {
            throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        List documents = (List) result.getQueryResult();

        return documents;
    }
}
