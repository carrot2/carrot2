
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp.model;

import java.util.Calendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.carrot2.core.ProcessingException;
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
    public final WebappConfig webappConfig;

    @Element(name = "asset-urls")
    public final AssetUrlsModel assetUrls;

    @Element(name = "attribute-metadata")
    public final AttributeMetadataModel attributesModel;

    @Element(name = "request")
    public final RequestModel requestModel;
    
    @Element(name = "searchresult", required = false)
    public final ProcessingResult processingResult;

    @Attribute(name = "exception-message", required = false)
    public final String processingExceptionMessage;

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
    
    @Attribute(name = "current-year")
    public final int currentYear;

    public PageModel(WebappConfig config, HttpServletRequest request, RequestModel requestModel,
        JawrUrlGenerator urlGenerator, ProcessingResult processingResult,
        ProcessingException processingException)
    {
        this.webappConfig = config;
        this.processingResult = processingResult;
        this.requestModel = requestModel;
        this.attributesModel = new AttributeMetadataModel(config);
        this.type = processingException == null ? requestModel.type : RequestType.ERROR;
        this.processingExceptionMessage = processingException == null ? null
            : processingException.getMessage();

        // TODO: determine based on skin
        this.fullHtml = !RequestType.DOCUMENTS.equals(requestModel.type)
            && !RequestType.CLUSTERS.equals(requestModel.type);

        this.contextPath = request.getContextPath();
        this.skinPath = contextPath + "/" + webappConfig.skinsFolder;
        this.assetUrls = new AssetUrlsModel(webappConfig.getSkinById(requestModel.skin),
            request, urlGenerator);

        this.requestUrl = buildSearchUrlBase(requestModel, webappConfig.searchUrl)
            .toString();

        // XML stream url base
        StringBuilder xmlUrl = buildSearchUrlBase(requestModel, webappConfig.xmlUrl);
        appendParameter(xmlUrl, WebappConfig.TYPE_PARAM, RequestType.CARROT2.name());
        this.xmlUrlEncoded = StringUtils.urlEncodeWrapException(xmlUrl.toString(),
            "UTF-8");
        
        this.currentYear = Calendar.getInstance().get(Calendar.YEAR);
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

        for (Map.Entry<String, Object> entry: requestModel.otherParameters.entrySet())
        {
            appendParameter(stringBuilder, entry.getKey(), entry.getValue().toString());
        }
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
