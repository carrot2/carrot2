package org.carrot2.webapp.model;

import java.util.List;

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
    final static List<Integer> SIZES = Lists.immutableList(50, 100, 150, 200);

    final static List<SkinModel> SKINS = Lists.immutableList(new SkinModel("fancy-large",
        RequestType.PAGE));

    public final static WebappConfig INSTANCE = new WebappConfig(SIZES, SKINS);

    public static final String SKINS_FOLDER = "/skins";

    @Element
    public ProcessingComponentSuite components;

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

    public WebappConfig(List<Integer> sizes, List<SkinModel> skins)
    {
        this.sizes = sizes;
        this.skins = skins;
        try
        {
            this.components = ProcessingComponentSuite.deserialize(ResourceUtilsFactory
                .getDefaultResourceUtils().getFirst("carrot2-default/suite.xml"));
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
}
