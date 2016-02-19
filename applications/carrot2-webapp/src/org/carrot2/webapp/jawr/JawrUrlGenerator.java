
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

package org.carrot2.webapp.jawr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.carrot2.util.StreamUtils;

import net.jawr.web.resource.bundle.handler.ResourceBundlesHandler;
import net.jawr.web.resource.bundle.renderer.AbstractBundleLinkRenderer;
import net.jawr.web.servlet.RendererRequestUtils;

/**
 * An interface to the Jawr library, the CSS/JS compressor.
 */
public class JawrUrlGenerator
{
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(JawrUrlGenerator.class);

    private final ResourceBundlesHandler cssJawrHandler;
    private final ResourceBundlesHandler jsJawrHandler;

    public JawrUrlGenerator(ServletContext servletContext)
    {
        cssJawrHandler = (ResourceBundlesHandler) servletContext
            .getAttribute(ResourceBundlesHandler.CSS_CONTEXT_ATTRIBUTE);

        jsJawrHandler = (ResourceBundlesHandler) servletContext
            .getAttribute(ResourceBundlesHandler.JS_CONTEXT_ATTRIBUTE);

        if (cssJawrHandler == null || jsJawrHandler == null)
        {
            throw new IllegalStateException(
                "ResourceBundlesHandler not present in servlet context. "
                    + "Initialization of Jawr either failed or never occurred.");
        }
    }

    public List<String> getCssUrls(HttpServletRequest request, String bundleId)
    {
        return getUrls(request, bundleId, cssJawrHandler);
    }

    public List<String> getJsUrls(HttpServletRequest request, String bundleId)
    {
        return getUrls(request, bundleId, jsJawrHandler);
    }

    private List<String> getUrls(HttpServletRequest request, String bundleId,
        ResourceBundlesHandler handler)
    {
        final boolean isGzippable = RendererRequestUtils.isRequestGzippable(request,
            handler.getConfig());

        final ArrayList<String> links = new ArrayList<String>();
        final CollectingLinkRenderer renderer = new CollectingLinkRenderer(handler,
            false, links);
        final String localeKey = renderer.getBundler().getConfig().getLocaleResolver()
            .resolveLocaleCode(request);

        try
        {
            renderer.renderBundleLinks(bundleId, request.getContextPath(), localeKey,
                RendererRequestUtils.getAddedBundlesLog(request), isGzippable,
                StreamUtils.NULL_WRITER);
        }
        catch (IOException e)
        {
            // Cannot happen really
            logger.error(e.getMessage());
        }

        return links;
    }

    static class CollectingLinkRenderer extends AbstractBundleLinkRenderer
    {
        private final List<String> links;

        protected CollectingLinkRenderer(ResourceBundlesHandler bundler,
            boolean useRandomParam, List<String> links)
        {
            super(bundler, useRandomParam);
            this.links = links;
        }

        @Override
        protected String renderLink(String link)
        {
            links.add(link);
            return link;
        }
    }
}
