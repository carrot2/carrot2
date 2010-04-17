
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

package org.carrot2.source.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.carrot2.util.simplexml.ISimpleXmlWrapper;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

/**
 * Lucene's {@link org.apache.lucene.analysis.standard.StandardAnalyzer} with
 * a parameterless constructor defining the compatibility flag
 * to the version ({@link Version#LUCENE_30}). 
 */
@Root(name = "analyzer")
public final class StandardAnalyzerWrapper 
    implements ISimpleXmlWrapper<StandardAnalyzer>
{
    private StandardAnalyzer analyzer;

    public StandardAnalyzerWrapper()
    {
    }

    public StandardAnalyzer getValue()
    {
        return analyzer;
    }

    public void setValue(StandardAnalyzer value)
    {
        analyzer = value;
    }

    @Persist
    void beforeSerialization()
    {
        // Check versionMatch/ other fields by reflection? Makes little sense for now.
    }

    @Commit
    void afterDeserialization()
    {
        analyzer = new StandardAnalyzer(Version.LUCENE_30);
    }    
}
