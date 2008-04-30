package org.carrot2.webapp.model;

import java.util.List;

import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.source.yahoo.YahooDocumentSource;
import org.carrot2.source.yahoo.YahooNewsSearchService;
import org.simpleframework.xml.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 *
 */
public class WebappConfig
{
    final static DocumentSourceModel YAHOO_WEB = new DocumentSourceModel(
        YahooDocumentSource.class, "yahoo", "Yahoo!", "Y", "Yahoo Web Search",
        "Yahoo Web description", Lists.immutableList("data mining", "clustering", "java"));
    final static DocumentSourceModel YAHOO_NEWS = new DocumentSourceModel(
        YahooDocumentSource.class, "news", "News", "Y", "Yahoo News Search",
        "Yahoo News description", Maps.<String, Object> immutableMap(
            YahooDocumentSource.class.getName() + ".service",
            YahooNewsSearchService.class), Lists.immutableList("election", "iphone",
            "poland"));
    final static ProcessingComponentModel STC = new ProcessingComponentModel(
        STCClusteringAlgorithm.class, "stc", "STC", "S", "Suffix Tree Clustering",
        "STC description");

    final static List<DocumentSourceModel> SOURCES = Lists.immutableList(YAHOO_WEB,
        YAHOO_NEWS);
    final static List<ProcessingComponentModel> ALGORITHMS = Lists.immutableList(STC);

    final static ComponentSuiteModel COMPONENTS = new ComponentSuiteModel(SOURCES,
        ALGORITHMS);

    final static List<Integer> SIZES = Lists.immutableList(50, 100, 150, 200);

    final static List<SkinModel> SKINS = Lists.immutableList(new SkinModel("fancy-large",
        RequestType.PAGE));

    public final static WebappConfig INSTANCE = new WebappConfig(COMPONENTS, SIZES, SKINS);

    public static final String SKINS_FOLDER = "/skins";

    @Element
    public ComponentSuiteModel components;

    @ElementList(entry = "skin")
    public final List<SkinModel> skins;

    @ElementList(entry = "size")
    public final List<Integer> sizes;

    @Attribute(name = "search-url")
    public final String searchUrl = "search";

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

    @Attribute(name = "skin-param")
    public final static String SKIN_PARAM = "skin";
    
    public WebappConfig(ComponentSuiteModel components, List<Integer> sizes,
        List<SkinModel> skins)
    {
        this.components = components;
        this.sizes = sizes;
        this.skins = skins;
    }

    public static String getContextRelativeSkinStylesheet(String skinName)
    {
        return SKINS_FOLDER + "/" + skinName + "/page.xsl";
    }
}
