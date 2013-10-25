
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2013, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.lucene;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.util.Version;
import org.carrot2.util.simplexml.ISimpleXmlWrapper;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

/**
 * Lucene's {@link org.apache.lucene.analysis.standard.StandardAnalyzer} with
 * a parameterless constructor defining the compatibility flag
 * to the version <code>Version#LUCENE_CURRENT</code> (no fixed Lucene dependency). 
 */
@Root(name = "analyzer")
public final class SimpleAnalyzerWrapper 
    implements ISimpleXmlWrapper<SimpleAnalyzer>

{
    private SimpleAnalyzer analyzer;

    public SimpleAnalyzerWrapper()
    {
    }

    public SimpleAnalyzer getValue()
    {
        return analyzer;
    }

    public void setValue(SimpleAnalyzer value)
    {
        analyzer = value;
    }

    @Persist
    void beforeSerialization()
    {
        // Check versionMatch/ other fields by reflection? Makes little sense for now.
    }

    @SuppressWarnings("deprecation")
    @Commit
    void afterDeserialization()
    {
        analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
    }    
}
