package org.carrot2.source.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.store.FSDirectory;
import org.carrot2.util.simplexml.SimpleXmlWrapper;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.load.Commit;
import org.simpleframework.xml.load.Persist;

/**
 * Wraps {@link FSDirectory} for serialization with SimpleXML.
 */
@Root(name = "fsdirectory")
public final class FSDirectoryWrapper implements SimpleXmlWrapper<FSDirectory>
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
        indexPath = value.getFile().getAbsolutePath();
    }

    @Commit
    void afterDeserialization()
    {
        try
        {
            value = FSDirectory.getDirectory(new File(indexPath));
        }
        catch (IOException e)
        {
            Logger.getLogger(FSDirectoryWrapper.class).warn(
                "Could not deserialize index location.", e);
        }
    }    
}
