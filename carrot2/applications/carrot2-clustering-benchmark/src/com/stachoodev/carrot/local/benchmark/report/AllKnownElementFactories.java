
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

package com.stachoodev.carrot.local.benchmark.report;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.stachoodev.carrot.odp.*;

/**
 * Provides access to all known implementations of the
 * {@link com.stachoodev.carrot.local.benchmark.report.ElementFactory}
 * interface.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
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
        factories.put(Profile.class, new ProfileElementFactory());
        factories.put(ProfileEntry.class, new ProfileEntryElementFactory());
        factories.put(ExtendedToken.class, new ExtendedTokenElementFactory());
        factories.put(ExtendedTokenSequence.class,
            new ExtendedTokenSequenceElementFactory());
        factories.put(Topic.class, new TopicElementFactory());
        factories.put(DoubleMatrix2DWrapper.class,
            new DoubleMatrix2DWrapperElementFactory());

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
            for (Iterator classesIter = factories.keySet().iterator(); classesIter
                .hasNext();)
            {
                Class cl = (Class) classesIter.next();
                if (cl.isAssignableFrom(c))
                {
                    elementFactory = (ElementFactory) factories.get(cl);
                    break;
                }
            }
        }
        return elementFactory;
    }
}