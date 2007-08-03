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

package org.carrot2.dcs;

import java.io.*;
import java.util.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.*;
import org.carrot2.util.PerformanceLogger;
import org.carrot2.util.StringUtils;

/**
 * Utility method for processing Carrot2 XML through a sequence of three processes:
 * <ul>
 * <li>conversion of the XML to an in-memory array of {@link RawDocument}s,</li>
 * <li>actual processing of the {@link RawDocument}s using the selected algorithm,
 * collecting {@link RawCluster}s in the output,
 * <li>
 * <li>conversion of {@link RawDocument}s and {@link RawCluster}s to the output format
 * (XML, JSON, possibly other).</li>
 * </ul>
 */
public final class ProcessingUtils
{
    /**
     * No instances of this class.
     */
    private ProcessingUtils()
    {
        // no instances.
    }

    /**
     * Runs clustering for the input stream, redirecting the output to the output stream.
     */
    public static ArrayOutputComponent.Result cluster(LocalController controller,
        Logger logger, InputStream inputXML, OutputStream outputStream,
        Map processingOptions) throws Exception
    {
        final String outputFormat = (String) processingOptions
            .get(ProcessingOptionNames.ATTR_OUTPUT_FORMAT);
        final String outputProcessName = ControllerContext
            .getOutputProcessId(outputFormat);

        boolean saveDocuments = true;
        final String clustersOnly = (String) processingOptions
            .get(ProcessingOptionNames.ATTR_CLUSTERS_ONLY);
        if (clustersOnly != null && Boolean.valueOf(clustersOnly).booleanValue())
        {
            saveDocuments = false;
        }

        final PerformanceLogger plogger = new PerformanceLogger(Level.DEBUG, logger);
        ArrayOutputComponent.Result result;
        try
        {
            plogger.start("Processing.");

            // Phase 1 -- read the XML
            plogger.start("Reading XML");
            final HashMap requestProperties = new HashMap();
            requestProperties.put(XmlStreamInputComponent.XML_STREAM, inputXML);
            result = (ArrayOutputComponent.Result) controller.query(
                ControllerContext.STREAM_TO_RAWDOCS, "n/a", requestProperties)
                .getQueryResult();

            final List documents = result.documents;
            final String query = (String) requestProperties
                .get(LocalInputComponent.PARAM_QUERY);
            plogger.end(); // Reading XML

            // Phase 2 -- cluster documents
            requestProperties.clear();
            requestProperties.putAll(processingOptions);
            final List clusters = clusterRawDocuments(plogger, controller,
                processingOptions, query, documents, requestProperties).clusters;

            // Skip serialization if output stream is not given.
            if (outputStream == null)
            {
                return result;
            }

            // Phase 3 -- save the result or emit it somehow.
            plogger.start("Saving result");
            requestProperties.clear();
            requestProperties.put(ArrayInputComponent.PARAM_SOURCE_RAW_DOCUMENTS,
                documents);
            requestProperties
                .put(ArrayInputComponent.PARAM_SOURCE_RAW_CLUSTERS, clusters);
            requestProperties.put(SaveFilterComponentBase.PARAM_OUTPUT_STREAM,
                outputStream);
            requestProperties.put(SaveFilterComponentBase.PARAM_SAVE_CLUSTERS,
                Boolean.TRUE);
            requestProperties.put(SaveFilterComponentBase.PARAM_SAVE_DOCUMENTS, Boolean
                .valueOf(saveDocuments));
            result = (ArrayOutputComponent.Result) controller.query(outputProcessName,
                query, requestProperties).getQueryResult();
            plogger.end("Output: " + outputFormat); // Saving result

            // Finish processing with a logging message.
            plogger.end(Level.INFO, "total");

            return result;
        }
        catch (IOException e)
        {
            logger.warn("Processing failed: " + StringUtils.chainExceptionMessages(e));
            logger.debug("Processing failed (full stack): ", e);

            return null;
        }
        finally
        {
            plogger.reset();
        }
    }

    /**
     * Run an in-memory clustering for an array of {@link RawDocument}s.
     * 
     * @param processingOptions Processing options with keys defined in
     *            {@link ProcessingOptionNames}.
     * @param requestProperties Properties passed to the clustering process (the third
     *            parameter to {@link LocalController#query(String, String, Map)}).
     */
    public static ArrayOutputComponent.Result clusterRawDocuments(
        PerformanceLogger plogger, LocalController controller, Map processingOptions,
        String query, List documents, Map requestProperties)
        throws MissingProcessException, Exception
    {
        final String processName = (String) processingOptions
            .get(ProcessingOptionNames.ATTR_PROCESSID);

        plogger.start("Clustering");

        requestProperties.put(ArrayInputComponent.PARAM_SOURCE_RAW_DOCUMENTS, documents);
        requestProperties.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, 
            Integer.toString(documents.size()));

        final ArrayOutputComponent.Result result = (ArrayOutputComponent.Result) controller
            .query(processName, query, requestProperties).getQueryResult();

        plogger.end(Level.INFO, "algorithm: " + processName + ", documents: "
            + documents.size() + ", query: " + query);

        return result;
    }
}
