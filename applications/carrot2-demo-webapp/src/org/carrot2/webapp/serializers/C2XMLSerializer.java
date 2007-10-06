/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp.serializers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.carrot2.core.*;
import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.impl.*;
import org.carrot2.webapp.*;

/**
 * An implementation of {@link RawClustersSerializer} that produces the standard Carrot2
 * XML stream with documents and clusters using the {@link SaveXmlFilterComponent}. This
 * serializer is not available through any factory, it is created directly by the
 * {@link QueryProcessorServlet}.
 * 
 * @author Stanislaw Osinski
 */
public class C2XMLSerializer implements RawClustersSerializer
{
    private LocalController controller;
    private Map params;
    private List clusters;
    private String query;

    public C2XMLSerializer()
    {
        initializeController();
    }

    private void initializeController()
    {
        controller = new LocalControllerBase();
        try
        {
            controller.addLocalComponentFactory("input", new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    return new ArrayInputComponent();
                }
            });

            controller.addLocalComponentFactory("save", new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    return new SaveXmlFilterComponent();
                }
            });

            controller.addLocalComponentFactory("output", new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    return NullOutputComponent.INSTANCE;
                }
            });

            controller.addProcess("save", new LocalProcessBase("input", "output",
                new String []
                {
                    "save"
                }));
        }
        catch (DuplicatedKeyException e)
        {
            // Ignored -- can't happen as the local controller is private
        }
        catch (InitializationException e)
        {
            // Ignored -- can't happen as the local controller is private
        }
        catch (MissingComponentException e)
        {
            // Ignored -- can't happen as the local controller is private
        }
    }

    public void startResult(OutputStream os, List rawDocumentsList,
        HttpServletRequest request, String query) throws IOException
    {
        clusters = new ArrayList();
        
        params = new HashMap();
        params.put(SaveFilterComponentBase.PARAM_OUTPUT_STREAM, os);
        params.put(SaveFilterComponentBase.PARAM_SAVE_CLUSTERS, Boolean.TRUE);
        params.put(ArrayInputComponent.PARAM_SOURCE_RAW_DOCUMENTS, rawDocumentsList);
        params.put(ArrayInputComponent.PARAM_SOURCE_RAW_CLUSTERS, clusters);
        
        this.query = query;
    }

    public void write(RawCluster cluster) throws IOException
    {
        clusters.add(cluster);
    }

    public void endResult(long clusteringTime) throws IOException
    {
        try
        {
            // No need to save the result, we have an interceptor filter component
            controller.query("save", query, params);
        }
        catch (MissingProcessException e)
        {
            // ignored -- can't happen
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getContentType()
    {
        return Constants.MIME_XML_CHARSET_UTF;
    }

    public void processingError(Throwable cause) throws IOException
    {
        // TODO: do we do anything special in case of errors?
    }
}
