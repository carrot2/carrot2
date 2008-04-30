package org.carrot2.webapp.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.carrot2.core.ProcessingResult;
import org.carrot2.util.ExceptionUtils;
import org.carrot2.webapp.jawr.JawrUrlGenerator;
import org.simpleframework.xml.*;

/**
 * Model of the page the application sends in response.
 */
@Root(name = "page")
public class PageModel
{
    @Element(name = "config")
    public final WebappConfig webappConfig = WebappConfig.INSTANCE;

    @Element(name = "asset-urls")
    public final AssetUrlsModel assetUrls;

    @Element(name = "request")
    public final RequestModel requestModel;

    @Element(name = "searchresult", required = false)
    public final ProcessingResult processingResult;

    @Attribute(name = "type")
    public final RequestType type;

    @Attribute(name = "full-html")
    public final boolean fullHtml;

    @Attribute(name = "context-path")
    public final String contextPath;

    @Attribute(name = "skin-path")
    public final String skinPath;

    @Attribute(name = "search-url-base")
    public final String searchUrlBase;

    public PageModel(HttpServletRequest request, JawrUrlGenerator urlGenerator,
        ProcessingResult processingResult, RequestModel requestModel)
    {
        this.processingResult = processingResult;
        this.requestModel = requestModel;
        this.type = requestModel.type;

        // TODO: determine based on skin
        this.fullHtml = !RequestType.DOCUMENTS.equals(requestModel.type)
            && !RequestType.CLUSTERS.equals(requestModel.type);

        this.contextPath = request.getContextPath();
        this.skinPath = contextPath + WebappConfig.SKINS_FOLDER;
        this.assetUrls = new AssetUrlsModel(requestModel.skin, request, urlGenerator);

        // Build search url base
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(contextPath);
        stringBuilder.append('/');
        stringBuilder.append(webappConfig.searchUrl);
        appendParameter(stringBuilder, WebappConfig.QUERY_PARAM, requestModel.query, '?');
        appendParameter(stringBuilder, WebappConfig.SOURCE_PARAM, requestModel.source);
        appendParameter(stringBuilder, WebappConfig.ALGORITHM_PARAM,
            requestModel.algorithm);
        appendParameter(stringBuilder, WebappConfig.RESULTS_PARAM, Integer
            .toString(requestModel.results));
        appendParameter(stringBuilder, WebappConfig.SKIN_PARAM, requestModel.skin);
        for (Entry<String, Object> parameter : requestModel.otherParameters.entrySet())
        {
            appendParameter(stringBuilder, parameter.getKey(), parameter.getValue()
                .toString());
        }
        this.searchUrlBase = stringBuilder.toString();
    }

    private static void appendParameter(StringBuilder builder, String name, String value)
    {
        appendParameter(builder, name, value, '&');
    }

    private static void appendParameter(StringBuilder builder, String name, String value,
        char separator)
    {
        builder.append(separator);
        builder.append(name);
        builder.append('=');
        try
        {
            builder.append(URLEncoder.encode(value, "UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw ExceptionUtils.wrapAs(RuntimeException.class, e);
        }
    }
}
