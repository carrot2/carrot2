/*
 * AllKnownElementFactories.java
 * 
 * Created on 2004-06-30
 */
package com.stachoodev.carrot.local.benchmark.report;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;

/**
 * @author stachoo
 */
public class AllKnownElementFactories
{
    /**
     * A mapping between Class objects (keys) and appropriate ElementFactory
     * implementations (values)
     */
    private static Map factories;

    /**
     * Put all known element factories to the map. If needed, this code can be
     * refatored to use Reflection API to instantiate all classes implementing
     * the ElementFactory interface automatically.
     */
    static
    {
        factories = new HashMap();
        
        factories.put(RawCluster.class, new RawClusterElementFactory());
        factories.put(RawDocument.class, new RawDocumentElementFactory());
    }

    /**
     * Returns a {@link ElementFactory}for given class or <code>null</code>
     * if no appropriate factory is available.
     * 
     * @param c
     * @return
     */
    public static ElementFactory getElementFactory(Class c)
    {
        ElementFactory elementFactory = (ElementFactory) factories.get(c);
        
        if (elementFactory == null)
        {
            // Try interface matches
            for (Iterator classesIter = factories.keySet().iterator(); classesIter.hasNext();)
            {
                Class cl = (Class) classesIter.next();
                if (cl.isAssignableFrom(c)){
                    elementFactory = (ElementFactory) factories.get(cl);
                    break;
                }
            }
        }
        return elementFactory;
    }
}