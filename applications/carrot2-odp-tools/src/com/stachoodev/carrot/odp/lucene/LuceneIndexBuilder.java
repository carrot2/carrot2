
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

package com.stachoodev.carrot.odp.lucene;

import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.index.*;

import com.carrot.input.lucene.PorterAnalyzerFactory;
import com.stachoodev.carrot.odp.*;
import com.stachoodev.carrot.odp.common.*;

/**
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class LuceneIndexBuilder extends ODPAbstractSaxHandler
{
    /** */
    private IndexWriter indexWriter;

    /**
     *  
     */
    public LuceneIndexBuilder()
    {
        super();
    }

    /**
     * @param rdfInputStream
     */
    public void index(InputStream rdfInputStream, String indexPath)
        throws IOException
    {
        // Create Porter analyzer
        Analyzer analyzer = PorterAnalyzerFactory.INSTANCE.getInstance();
        
        // Initialize Lucene index first
        indexWriter = new IndexWriter(indexPath, analyzer, true);
        indexWriter.mergeFactor = 100;
        
        // Now go with parsing
        initalizeParser(rdfInputStream);
    }

    /**
     * @throws IOException
     * 
     */
    public void close() throws IOException
    {
        indexWriter.optimize(); 
        indexWriter.close();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.carrot.odp.common.ODPAbstractSaxHandler#index(com.stachoodev.carrot.odp.Topic)
     */
    protected void index(Topic topic) throws IOException
    {
        for (Iterator iter = topic.getExternalPages().iterator(); iter.hasNext();)
        {
            ExternalPage externalPage = (ExternalPage) iter.next();
            indexWriter.addDocument(ExternalPageDocument.Document(externalPage));
        }
        
        fireTopicIndexed();
    }
}