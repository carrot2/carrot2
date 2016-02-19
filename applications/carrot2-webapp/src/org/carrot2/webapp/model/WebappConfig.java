
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

import java.io.InputStream;
import java.util.*;

import javax.servlet.ServletContext;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.core.attribute.InternalAttributePredicate;
import org.carrot2.util.CloseableUtils;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.*;
import org.carrot2.util.resource.ResourceLookup.Location;
import org.carrot2.webapp.QueryProcessorServlet;
import org.carrot2.webapp.filter.QueryWordHighlighter;
import org.simpleframework.xml.*;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;

import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.base.Predicates;
import org.carrot2.shaded.guava.common.collect.*;

/**
 * The application-wide configuration.
 */
public class WebappConfig
{
    private final static Logger log = org.slf4j.LoggerFactory
        .getLogger(WebappConfig.class);

    @Element(required = false)
    public ProcessingComponentSuite components;

    /**
     * Descriptors of attributes to display in the advanced document source options view,
     * keyed by document source id.
     */
    public Map<String, List<AttributeDescriptor>> sourceAttributeMetadata;

    /**
     * Values of document source attributes set at component initialization time, keyed by
     * document source id. We need these to show proper default values in advanced options
     * for those sources for which the default attribute values have been overridden in
     * the component suite.
     */
    public Map<String, Map<String, Object>> sourceInitializationAttributes;

    /**
     * A set of keys of all internal attributes of all components. We need this to prevent
     * these attributes to be bound from the HTTP request parameters.
     */
    public Set<String> componentInternalAttributeKeys;

    @ElementList(entry = "skin")
    public ArrayList<SkinModel> skins;

    @ElementList(entry = "size")
    public ArrayList<ResultsSizeModel> sizes;

    @ElementList(entry = "view")
    public ArrayList<ResultsViewModel> views;

    @ElementList(entry = "cache", required = false)
    public ArrayList<ResultsCacheModel> caches = Lists.newArrayList();
    
    @Attribute(name = "skins-folder")
    public String skinsFolder;

    @Attribute(name = "component-suite")
    public String componentSuite = "suites/suite-webapp.xml";

    @Attribute(name = "search-url", required = false)
    public String SEARCH_URL = "search";

    @Attribute(name = "xml-url", required = false)
    public String XML_URL = "xml";

    @Attribute(name = "query-highlighter", required = false)
    public String QUERY_HIGHLIGHTER_ID = QueryWordHighlighter.class.getName();

    @Attribute(name = "max-carrot2-results", required = false)
    public Integer maxCarrot2Results = null;

    // These are for output serialization only, this is somewhat clumsy. 

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

    @Attribute(name = "stylesheet-param", required = false)
    public final static String STYLESHEET_PARAM = "stylesheet";

    /**
     * @return Initialize the global configuration and return it.
     */
    public final static WebappConfig getSingleton(ServletContext context)
    {
        try
        {
            // Load web application configuration.
            final IResource webappConfig = new ResourceLookup(
                    new PrefixDecoratorLocator(new ServletContextLocator(context), "/WEB-INF/"))
                .getFirst("webapp-config.xml");

            if (webappConfig == null)
                throw new RuntimeException("Could not find WEB-INF/webapp-config.xml.");

            final WebappConfig conf = deserialize(webappConfig);

            if (conf.skins.size() == 0)
            {
                throw new RuntimeException("Configuration must contain at least one skin");
            }

            if (conf.views.size() == 0)
            {
                throw new RuntimeException("Configuration must contain at least one view");
            }

            if (conf.sizes.size() == 0)
            {
                throw new RuntimeException(
                    "Configuration must contain at least one result list size");
            }

            // Load component suite.
            List<IResourceLocator> resourceLocators = Lists.newArrayList();
            resourceLocators.add(new PrefixDecoratorLocator(
                new ServletContextLocator(context), "/WEB-INF/suites/"));

            if (Boolean.getBoolean(QueryProcessorServlet.ENABLE_CLASSPATH_LOCATOR))
                resourceLocators.add(Location.CONTEXT_CLASS_LOADER.locator);

            ResourceLookup suitesLookup = new ResourceLookup(resourceLocators);
            IResource suite = suitesLookup.getFirst(conf.componentSuite);

            if (suite == null)
            {
                throw new Exception("Suite file not found in servlet context's /WEB-INF/suites: " 
                    + conf.componentSuite);
            }

            conf.components = ProcessingComponentSuite.deserialize(suite, suitesLookup);

            log.info("Loaded " + conf.components.getSources().size() + " sources and "
                + conf.components.getAlgorithms().size() + " algorithms");

            // Prepare attribute descriptors for document sources
            conf.sourceAttributeMetadata = prepareSourceAttributeMetadata(conf.components);
            conf.sourceInitializationAttributes = prepareSourceInitializationAttributes(conf.components);
            conf.componentInternalAttributeKeys = prepareComponentInternalAttributeKeys(conf.components);

            return conf;
        }
        catch (Exception e)
        {
            log.error("Could not load application config.", e);
            throw new RuntimeException("Could not load application config.", e);
        }
    }

    public String getContextRelativeSkinStylesheet(RequestModel requestModel)
    {
        return "/" + skinsFolder + "/" + requestModel.skin + "/" + requestModel.stylesheet;
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

        return ModelWithDefault.getDefault(skins);
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

    private static Map<String, List<AttributeDescriptor>> prepareSourceAttributeMetadata(
        ProcessingComponentSuite components) throws Exception
    {
        final List<DocumentSourceDescriptor> sources = components.getSources();
        final Map<String, List<AttributeDescriptor>> sourceDescriptors = Maps
            .newLinkedHashMap();

        for (DocumentSourceDescriptor documentSourceDescriptor : sources)
        {
            final BindableDescriptor bindableDescriptor = documentSourceDescriptor
                .getBindableDescriptor().only(Input.class).only(
                    new LevelsPredicate(AttributeLevel.BASIC, AttributeLevel.MEDIUM))
                .only(
                    Predicates.<AttributeDescriptor> and(Predicates
                        .not(new InternalAttributePredicate()),
                        new Predicate<AttributeDescriptor>()
                        {
                            /** Attribute types supported in advanced source options */
                            final Set<Class<?>> ALLOWED_PLAIN_TYPES = ImmutableSet
                                .<Class<?>> of(Byte.class, Short.class, Integer.class,
                                    Long.class, Float.class, Double.class, Boolean.class,
                                    String.class, Character.class);

                            private final Set<String> IGNORED = ImmutableSet.<String> of(
                                AttributeNames.QUERY, AttributeNames.RESULTS);

                            public boolean apply(AttributeDescriptor d)
                            {
                                return (d.type.isEnum() || ALLOWED_PLAIN_TYPES
                                    .contains(d.type))
                                    && !IGNORED.contains(d.key);
                            }
                        }));

            final List<AttributeDescriptor> descriptors = Lists
                .newArrayList(bindableDescriptor.attributeDescriptors.values());
            Collections.sort(descriptors, new Comparator<AttributeDescriptor>()
            {
                public int compare(AttributeDescriptor d1, AttributeDescriptor d2)
                {
                    return getOrder(d1) - getOrder(d2);
                }

                private int getOrder(AttributeDescriptor d)
                {
                    if (d.type.isEnum())
                    {
                        return 0;
                    }
                    else if (d.type.equals(Boolean.class))
                    {
                        return 2;
                    }
                    else
                    {
                        return 1;
                    }
                }
            });

            sourceDescriptors.put(documentSourceDescriptor.getId(), descriptors);
        }

        return sourceDescriptors;
    }

    private static Map<String, Map<String, Object>> prepareSourceInitializationAttributes(
        ProcessingComponentSuite components)
    {
        final List<DocumentSourceDescriptor> sources = components.getSources();
        final Map<String, Map<String, Object>> initAttributes = Maps.newHashMap();

        for (DocumentSourceDescriptor documentSourceDescriptor : sources)
        {
            initAttributes.put(documentSourceDescriptor.getId(), documentSourceDescriptor
                .getComponentConfiguration().attributes);
        }

        return initAttributes;
    }

    private static Set<String> prepareComponentInternalAttributeKeys(
        ProcessingComponentSuite components) throws Exception
    {
        final List<ProcessingComponentDescriptor> descriptors = components
            .getComponents();
        final Set<String> internalAttributeKeys = Sets.newHashSet();

        for (ProcessingComponentDescriptor descriptor : descriptors)
        {
            internalAttributeKeys.addAll(Lists.transform(Lists
                .newArrayList(descriptor.getBindableDescriptor().only(
                    new InternalAttributePredicate()).attributeDescriptors.values()),
                AttributeDescriptor.AttributeDescriptorToKey.INSTANCE));
        }

        internalAttributeKeys.remove(AttributeNames.QUERY);

        return internalAttributeKeys;
    }
}
