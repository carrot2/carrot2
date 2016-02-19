
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

package org.carrot2.core.benchmarks.memtime;

import java.util.ArrayList;

import org.carrot2.core.Document;
import org.junit.Test;

/**
 * Just evaluate preprocessing, nothing else.
 */
public class PreprocessingBenchmark extends MemTimeBenchmark
{
    /**
     * Initialize static data.
     */
    // @BeforeClass
    public static void initStaticData2() throws Exception
    {
        ArrayList<Document> smallDocs = MemTimeBenchmark.documents;
        ArrayList<Document> largeDocs = new ArrayList<Document>();

        final int MERGE_DOCS = 5;
        Document last = null;
        for (int i = 0; i < smallDocs.size(); i++)
        {
            if ((i % MERGE_DOCS) == 0)
            {
                largeDocs.add(last = new Document());
            }
            
            Document d = smallDocs.get(i);
            last.setTitle(join(last.getTitle(), d.getTitle()));
            last.setSummary(join(last.getSummary(), d.getSummary()));
            if (last.getContentUrl() == null)
                last.setContentUrl(d.getContentUrl());
        }
        
        MemTimeBenchmark.documents = largeDocs;
    }

    private static final StringBuilder b = new StringBuilder();
    private static String join(String... objects)
    {
        b.setLength(0);
        for (String s : objects)
        {
            if (s != null)
            {
                if (b.length() > 0) b.append(" . ");
                b.append(s);
            }
        }
        return b.toString();
    }

    @Test
    public void evalBasicPreprocessing()
    {
        evalShortDocs("basic-preprocessing", 
            BasicPreprocessing.class, MIN, MAX, STEP);
    }
}
