
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp.model;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.carrot2.core.ProcessingComponentSuite;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceUtilsFactory;
import org.simpleframework.xml.*;
import org.simpleframework.xml.load.Persister;

/**
 * The application-wide configuration.
 */
public class WebappConfig
{
    private final static Logger log = Logger.getLogger(WebappConfig.class);

    @Element(required = false)
    public ProcessingComponentSuite components;

    @ElementList(entry = "skin")
    public ArrayList<SkinModel> skins;

    @ElementList(entry = "size")
    public ArrayList<ResultsSizeModel> sizes;

    @ElementList(entry = "view")
    public ArrayList<ResultsViewModel> views;

    @Attribute(name = "skins-folder")
    public String skinsFolder;

    @Attribute(name = "component-suite")
    public String componentSuite = "carrot2-default/suite-webapp.xml";

    @Attribute(name = "search-url", required = false)
    public final String searchUrl = "search";

    @Attribute(name = "xml-url", required = false)
    public final String xmlUrl = "xml";

    @Attribute(name = "query-param", required = false)
    public final static String QUERY_PARAM = AttributeNames.QUERY;
    public final static String QUERY_PARAM_ALIAS = "q";

    @Attribute(name = "results-param", required = false)
    public final static String RESULTS_PARAM = AttributeNames.RESULTS;

    @Attribute(name = "source-param", required = false)
    public final static String SOURCE_PARAM = "source";

    @Attribute(name = "algorithm-param", required = false)
    public final static String ALGORITHM_PARAM = "algorithm";

    @Attribute(name = "type-param", required = false)
    public final static String TYPE_PARAM = "type";

    @Attribute(name = "view-param", required = false)
    public final static String VIEW_PARAM = "view";

    @Attribute(name = "skin-param", required = false)
    public final static String SKIN_PARAM = "skin";

    /** Application-wide instance of the configuration */
    public final static WebappConfig INSTANCE;

    static
    {
        try
        {
            INSTANCE = deserialize(ResourceUtilsFactory.getDefaultResourceUtils()
                .getFirst("config.xml"));

            if (INSTANCE.skins.size() == 0)
            {
                throw new RuntimeException("Configuration must contain at leas one skin");
            }
            if (INSTANCE.views.size() == 0)
            {
                throw new RuntimeException("Configuration must contain at leas one view");
            }
            if (INSTANCE.sizes.size() == 0)
            {
                throw new RuntimeException(
                    "Configuration must contain at leas one result list size");
            }

            INSTANCE.components = ProcessingComponentSuite
                .deserialize(ResourceUtilsFactory.getDefaultResourceUtils().getFirst(
                    INSTANCE.componentSuite));
            log.info("Loaded " + INSTANCE.components.getSources().size()
                + " sources and " + INSTANCE.components.getAlgorithms().size()
                + " algorithms");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not load application config", e);
        }
    }

    public static String getContextRelativeSkinStylesheet(String skinName)
    {
        return "/" + INSTANCE.skinsFolder + "/" + skinName + "/page.xsl";
    }

    public SkinModel getSkinById(String skinId)
    {
        // Short list, we can afford linear search
        for (SkinModel skin : skins)
        {
            if (skin.id.equals(skinId))
            {
                return skin;
            }
        }

        return null;
    }

    public RequestModel setDefaults(RequestModel requestModel)
    {
        requestModel.skin = ModelWithDefault.getDefault(skins).id;
        requestModel.results = ModelWithDefault.getDefault(sizes).size;
        requestModel.view = ModelWithDefault.getDefault(views).id;

        return requestModel;
    }

    private static WebappConfig deserialize(IResource resource) throws Exception
    {
        final InputStream inputStream = resource.open();
        final WebappConfig loaded;
        try
        {
            loaded = new Persister().read(WebappConfig.class, inputStream);
        }
        finally
        {
            CloseableUtils.close(inputStream);
        }

        return loaded;
    }
}
