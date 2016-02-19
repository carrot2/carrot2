
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

package org.carrot2.source.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.store.FSDirectory;
import org.carrot2.util.simplexml.ISimpleXmlWrapper;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

/**
 * Wraps {@link FSDirectory} for serialization with SimpleXML.
 */
@Root(name = "fsdirectory")
public final class FSDirectoryWrapper implements ISimpleXmlWrapper<FSDirectory>
{
    private FSDirectory value;

    @Element
    private String indexPath;

    public FSDirectory getValue()
    {
        return value;
    }

    public void setValue(FSDirectory value)
    {
        this.value = value;
    }

    @Persist
    void beforeSerialization()
    {
        indexPath = value.getDirectory().toAbsolutePath().toString();
    }

    @Commit
    void afterDeserialization()
    {
        try
        {
            value = FSDirectory.open(Paths.get(indexPath));
        }
        catch (IOException e)
        {
            org.slf4j.LoggerFactory.getLogger(FSDirectoryWrapper.class).warn(
                "Could not deserialize index location.", e);
        }
    }    
}
