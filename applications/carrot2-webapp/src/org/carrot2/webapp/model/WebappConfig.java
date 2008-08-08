package org.carrot2.webapp.model;

import java.util.List;

import org.apache.log4j.Logger;
import org.carrot2.core.ProcessingComponentSuite;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.util.resource.ResourceUtilsFactory;
import org.simpleframework.xml.*;

import com.google.common.collect.Lists;

/**
 *
 */
public class WebappConfig
{
    private final static Logger log = Logger.getLogger(WebappConfig.class);

    final static List<Integer> SIZES = Lists.immutableList(50, 100, 150, 200);

    final static List<SkinModel> SKINS = Lists.immutableList(new SkinModel("fancy-large",
        RequestType.PAGE), new SkinModel("fancy-compact", RequestType.PAGE),
        new SkinModel("simple", RequestType.PAGE));

    final static List<ResultsViewModel> VIEWS = Lists.immutableList(new ResultsViewModel(
        "tree", "Tree"), new ResultsViewModel("visu", "Visualization"));

    public final static WebappConfig INSTANCE = new WebappConfig(SIZES, SKINS, VIEWS);

    public static final String SKINS_FOLDER = "/skins";

    @Element
    public ProcessingComponentSuite components;

    @ElementList(entry = "skin")
    public final List<SkinModel> skins;

    @ElementList(entry = "size")
    public final List<Integer> sizes;

    @ElementList(entry = "view")
    public final List<ResultsViewModel> views;

    @Attribute(name = "search-url")
    public final String searchUrl = "search";

    @Attribute(name = "xml-url")
    public final String xmlUrl = "xml";

    @Attribute(name = "query-param")
    public final static String QUERY_PARAM = AttributeNames.QUERY;

    @Attribute(name = "results-param")
    public final static String RESULTS_PARAM = AttributeNames.RESULTS;

    @Attribute(name = "source-param")
    public final static String SOURCE_PARAM = "source";

    @Attribute(name = "algorithm-param")
    public final static String ALGORITHM_PARAM = "algorithm";

    @Attribute(name = "type-param")
    public final static String TYPE_PARAM = "type";

    @Attribute(name = "view-param")
    public final static String VIEW_PARAM = "view";

    @Attribute(name = "skin-param")
    public final static String SKIN_PARAM = "skin";

    public WebappConfig(List<Integer> sizes, List<SkinModel> skins,
        List<ResultsViewModel> views)
    {
        this.sizes = sizes;
        this.skins = skins;
        this.views = views;
        try
        {
            this.components = ProcessingComponentSuite.deserialize(ResourceUtilsFactory
                .getDefaultResourceUtils().getFirst("carrot2-default/suite.xml"));
            log.info("Loaded " + components.getSources().size() + " sources and "
                + components.getAlgorithms().size() + " algorithms");
        }
        catch (Exception e)
        {
            throw ExceptionUtils.wrapAs(RuntimeException.class, e);
        }
    }

    public static String getContextRelativeSkinStylesheet(String skinName)
    {
        return SKINS_FOLDER + "/" + skinName + "/page.xsl";
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
}
