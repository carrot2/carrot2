package org.carrot2.webapp.model;

import javax.servlet.http.HttpServletRequest;

import org.carrot2.core.ProcessingResult;
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
    }
}
