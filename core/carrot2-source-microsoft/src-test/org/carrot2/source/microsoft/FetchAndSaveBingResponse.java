
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

package org.carrot2.source.microsoft;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.CommonAttributesDescriptor;
import org.simpleframework.xml.core.Persister;

import org.carrot2.shaded.guava.common.base.Strings;

public class FetchAndSaveBingResponse
{
    public static void main(String [] args)
        throws Exception
    {
        final Controller controller = ControllerFactory.createSimple();
        try {
            String appid = System.getProperty(Bing3DocumentSource.SYSPROP_BING3_API);
            if (Strings.isNullOrEmpty(appid))
            {
                System.err.println("Provide Bing3 API key in property: " 
                    + Bing3DocumentSource.SYSPROP_BING3_API);
            }

            final Map<String, Object> attributes = new HashMap<String, Object>();
            CommonAttributesDescriptor.attributeBuilder(attributes)
                .query("डाटा माइनिंग")
                .results(200);

            /* Put your own API key here or in a system property! */
            Bing3WebDocumentSourceDescriptor.attributeBuilder(attributes)
                .appid(appid)
                .market((MarketOption) null);

            ProcessingResult result = controller.process(attributes, Bing3WebDocumentSource.class);
            Persister p = new Persister();
            p.write(result, new File("result.xml"));
        } finally {
            controller.dispose();
        }        
    }
}
