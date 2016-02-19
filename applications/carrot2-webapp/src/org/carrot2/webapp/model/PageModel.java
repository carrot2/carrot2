
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

package org.carrot2.webapp.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.carrot2.core.ProcessingException;
import org.carrot2.core.ProcessingResult;
import org.carrot2.util.StringUtils;
import org.carrot2.webapp.jawr.JawrUrlGenerator;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

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

    @Attribute(name = "request-uri")
    public final String requestUri;

    @Attribute(name = "timestamp")
    public final String timestamp = new SimpleDateFormat("yyyy-MMM-dd_HH-mm").format(new Date());

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
        this.fullHtml = !RequestType.DOCUMENTS.equals(requestModel.type) && 
                        !RequestType.CLUSTERS.equals(requestModel.type);

        this.contextPath = request.getContextPath();
        this.skinPath = contextPath + "/" + webappConfig.skinsFolder;
        this.assetUrls = new AssetUrlsModel(
            webappConfig.getSkinById(requestModel.skin), request, urlGenerator);

        this.requestUri = buildRequestUri(requestModel);
        
        StringBuilder b = new StringBuilder();
        buildSearchUrlBase(b, requestModel, config.SEARCH_URL);
        this.requestUrl = b.toString() + this.requestUri;

        // XML stream url base
        b.setLength(0);
        buildSearchUrlBase(b, requestModel, config.XML_URL);
        b.append(this.requestUri);
        appendParameter(b, WebappConfig.TYPE_PARAM, RequestType.CARROT2.name());
        this.xmlUrlEncoded = StringUtils.urlEncodeWrapException(b.toString(), "UTF-8");

        this.currentYear = Calendar.getInstance().get(Calendar.YEAR);
    }

    private String buildRequestUri(RequestModel requestModel)
    {
        StringBuilder builder = new StringBuilder();
        appendParameter(builder, WebappConfig.QUERY_PARAM, requestModel.query, '?');
        appendParameter(builder, WebappConfig.RESULTS_PARAM, Integer.toString(requestModel.results));
        appendParameter(builder, WebappConfig.SOURCE_PARAM, requestModel.source);
        appendParameter(builder, WebappConfig.ALGORITHM_PARAM, requestModel.algorithm);
        appendParameter(builder, WebappConfig.VIEW_PARAM, requestModel.view);
        appendParameter(builder, WebappConfig.SKIN_PARAM, requestModel.skin);
        for (Map.Entry<String, Object> entry: requestModel.otherParameters.entrySet())
        {
            appendParameter(builder, entry.getKey(), entry.getValue().toString());
        }
        return builder.toString();
    }

    private void buildSearchUrlBase(StringBuilder builder, RequestModel requestModel, String action)
    {
        builder.append(contextPath);
        builder.append('/');
        builder.append(action);
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
