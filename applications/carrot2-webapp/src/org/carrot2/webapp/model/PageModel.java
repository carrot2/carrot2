package org.carrot2.webapp.model;

import javax.servlet.http.HttpServletRequest;

import org.carrot2.core.ProcessingResult;
import org.carrot2.util.StringUtils;
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

    @Attribute(name = "xml-url-encoded")
    public final String xmlUrlEncoded;

    @Attribute(name = "request-url")
    public final String requestUrl;

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

        this.requestUrl = buildSearchUrlBase(requestModel, webappConfig.searchUrl)
            .toString();

        // XML stream url base
        StringBuilder xmlUrl = buildSearchUrlBase(requestModel, webappConfig.xmlUrl);
        appendParameter(xmlUrl, WebappConfig.TYPE_PARAM, RequestType.CARROT2.name());
        this.xmlUrlEncoded = StringUtils.urlEncodeWrapException(xmlUrl.toString(),
            "UTF-8");
    }

    private StringBuilder buildSearchUrlBase(RequestModel requestModel, String action)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(contextPath);
        stringBuilder.append('/');
        stringBuilder.append(action);
        appendParameter(stringBuilder, WebappConfig.QUERY_PARAM, requestModel.query, '?');
        appendParameter(stringBuilder, WebappConfig.RESULTS_PARAM, Integer
            .toString(requestModel.results));
        appendParameter(stringBuilder, WebappConfig.SOURCE_PARAM, requestModel.source);
        appendParameter(stringBuilder, WebappConfig.ALGORITHM_PARAM,
            requestModel.algorithm);
        appendParameter(stringBuilder, WebappConfig.VIEW_PARAM, requestModel.view);
        appendParameter(stringBuilder, WebappConfig.SKIN_PARAM, requestModel.skin);
        return stringBuilder;
    }

    private static void appendParameter(StringBuilder builder, String name, String value)
    {
        appendParameter(builder, name, value, '&');
    }

    private static void appendParameter(StringBuilder builder, String name, String value,
        char separator)
    {
        if (org.apache.commons.lang.StringUtils.isNotBlank(value))
        {
            builder.append(separator);
            builder.append(name);
            builder.append('=');
            builder.append(StringUtils.urlEncodeWrapException(value, "UTF-8"));
        }
    }
}
