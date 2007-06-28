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

package org.carrot2.input.aggregator;

import java.util.*;

import junit.framework.TestCase;

import org.carrot2.core.*;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.clustering.RawDocumentSnippet;
import org.carrot2.core.impl.ArrayInputComponent;
import org.carrot2.core.impl.ArrayOutputComponent;

/**
 * @author Stanislaw Osinski
 */
public class AggregatorInputComponentTest extends TestCase
{
    private LocalController controller;

    private LocalComponentFactory inputNoDelay = new LocalComponentFactory()
    {
        public LocalComponent getInstance()
        {
            return new DelayedInputComponent(0, "no-delay");
        }
    };

    private LocalComponentFactory inputDelay500 = new LocalComponentFactory()
    {
        public LocalComponent getInstance()
        {
            return new DelayedInputComponent(500, "delay-500");
        }
    };

    private LocalComponentFactory inputDelay2000 = new LocalComponentFactory()
    {
        public LocalComponent getInstance()
        {
            return new DelayedInputComponent(2000, "delay-2000");
        }
    };

    public AggregatorInputComponentTest(String name) throws Exception
    {
        super(name);
        controller = new LocalControllerBase();

        controller.addLocalComponentFactory("input-aggregator-1",
            new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    return new AggregatorInputComponent(new AggregatorInput []
                    {
                        new AggregatorInput("no-delay", inputNoDelay, 0.0),
                        new AggregatorInput("delay-500", inputDelay500, 0.0)
                    }, 1500);
                }
            });

        controller.addLocalComponentFactory("input-aggregator-2",
            new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    return new AggregatorInputComponent(new AggregatorInput []
                    {
                        new AggregatorInput("no-delay", inputNoDelay, 0.0),
                        new AggregatorInput("delay-500", inputDelay500, 0.0),
                        new AggregatorInput("delay-2000", inputDelay2000, 0.0)
                    }, 1500);
                }
            });

        controller.addLocalComponentFactory("output-array",
            new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    return new ArrayOutputComponent();
                }
            });

        controller.addProcess("aggregator-1", new LocalProcessBase(
            "input-aggregator-1", "output-array", new String [0]));

        controller.addProcess("aggregator-2", new LocalProcessBase(
            "input-aggregator-2", "output-array", new String [0]));
    }

    /**
     * @throws Exception
     */
    public void testNoTimeout() throws Exception
    {
        long start = System.currentTimeMillis();
        ProcessingResult result = controller.query("aggregator-1",
            "does not matter", createRequestParameters());
        long stop = System.currentTimeMillis();

        List docs = ((ArrayOutputComponent.Result) result.getQueryResult()).documents;
        Set inputIds = collectSourceInputIds(docs);

        assertEquals(2, docs.size());
        assertTrue(inputIds.contains("no-delay"));
        assertTrue(inputIds.contains("delay-500"));
        assertTrue("Fetching time", stop - start < 1000);
    }

    /**
     * @throws Exception
     */
    public void testTimeout() throws Exception
    {
        long start = System.currentTimeMillis();
        ProcessingResult result = controller.query("aggregator-2",
            "does not matter", createRequestParameters());
        long stop = System.currentTimeMillis();

        List docs = ((ArrayOutputComponent.Result) result.getQueryResult()).documents;
        Set inputIds = collectSourceInputIds(docs);

        assertEquals(2, docs.size());
        assertTrue(inputIds.contains("no-delay"));
        assertTrue(inputIds.contains("delay-500"));
        assertFalse(inputIds.contains("delay-2000"));
        assertTrue("Fetching time", stop - start < 1800);
    }

    private Map createRequestParameters()
    {
        Map params = new HashMap();
        List docs = new ArrayList();

        docs.add(new RawDocumentSnippet(new Integer(1), "Test", "Test", "test",
            1.0f));

        params.put(
            ArrayInputComponent.PARAM_SOURCE_RAW_DOCUMENTS,
            docs);
        return params;
    }

    private Set collectSourceInputIds(List docs)
    {
        Set result = new HashSet();

        for (Iterator it = docs.iterator(); it.hasNext();)
        {
            RawDocument doc = (RawDocument) it.next();
            final String [] sourceIds = (String []) doc
                .getProperty(RawDocument.PROPERTY_SOURCES);
            for (int i = 0; i < sourceIds.length; i++)
            {
                result.add(sourceIds[i]);
            }
        }

        return result;
    }
}
