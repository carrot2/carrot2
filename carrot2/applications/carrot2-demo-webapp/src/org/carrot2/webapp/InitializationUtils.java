
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.controller.*;
import org.carrot2.core.controller.loaders.BeanShellFactoryDescriptionLoader;
import org.carrot2.core.impl.*;
import org.carrot2.core.profiling.ProfiledLocalController;
import org.carrot2.util.StringUtils;
import org.carrot2.webapp.serializers.XMLSerializersFactory;

/**
 * Some static initialization utilities refactored from {@link QueryProcessorServlet}.
 */
final public class InitializationUtils
{
    /**
     * No instances.
     */
    private InitializationUtils()
    {
        // no instances.
    }

    /**
     * Initializes results serializers.
     */
    static SerializersFactory initializeSerializers(Logger logger, ServletConfig conf) throws ServletException
    {
        String serializerFactoryClass = conf.getInitParameter("results.serializerFactory");
        if (serializerFactoryClass == null)
        {
            serializerFactoryClass = XMLSerializersFactory.class.getName();
            logger.warn("serializerFactory undefined, using the default: " + serializerFactoryClass);
        }

        final SerializersFactory factory;
        try
        {
            factory = (SerializersFactory) Thread.currentThread().getContextClassLoader().loadClass(
                serializerFactoryClass).newInstance();
            factory.configure(conf);
        }
        catch (Exception e)
        {
            throw new ServletException("Could not create results serializer: " + serializerFactoryClass, e);
        }
        logger.info("Using serializer factory: " + factory.getClass().getName());

        return factory;
    }

    /**
     * Initializes caches.
     */
    static Cache initializeCache(ServletConfig config) throws ServletException
    {
        final String EHCACHE_NAME = "carrot2";

        // Initialize EHCache
        final String cacheConfigResource = config.getInitParameter("ehcache.config.resource");
        if (cacheConfigResource == null)
        {
            throw new ServletException("EHCache configuration location (ehcache.config.resource) is missing.");
        }

        final URL configStream;
        try
        {
            configStream = config.getServletContext().getResource(cacheConfigResource);
        }
        catch (MalformedURLException e)
        {
            throw new ServletException("Resource URL malformed.", e);
        }
        if (configStream == null)
        {
            throw new ServletException("EHCache configuration is missing: " + cacheConfigResource);
        }

        final CacheManager cm = CacheManager.create(configStream);
        if (!cm.cacheExists(EHCACHE_NAME))
        {
            throw new ServletException("EHCache for Carrot2 not defined (" + EHCACHE_NAME + ")");
        }
        return cm.getCache(EHCACHE_NAME);
    }

    /**
     * Initializes and returns {@link LocalController} which will contain processes for collecting {@link RawDocument}s.
     * This method also initializes {@link TabSearchInput} instances corresponding to processes defined in the returned
     * controller.
     */
    static LocalControllerBase initializeInputs(final Logger logger, final File inputScripts,
        final SearchSettings settings)
    {
        if (!inputScripts.isDirectory())
        {
            throw new RuntimeException("Scripts for input tabs not initialized.");
        }

        final LocalControllerBase controller = new LocalControllerBase();
        final ControllerHelper helper = new ControllerHelper();

        // Register context path for beanshell scripts.
        final ComponentFactoryLoader bshLoader = helper
            .getComponentFactoryLoader(ControllerHelper.EXT_COMPONENT_FACTORY_LOADER_BEANSHELL);
        if (bshLoader != null)
        {
            final HashMap globals = new HashMap();
            globals.put("inputsDirFile", inputScripts);
            ((BeanShellFactoryDescriptionLoader) bshLoader).setGlobals(globals);
        }

        try
        {
            // Add an output sink component now.
            controller.addLocalComponentFactory("collector", new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    return new BroadcasterPushOutputComponent();
                }
            });

            // Add document enumerator.
            controller.addLocalComponentFactory("enumerator", new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    return new RawDocumentEnumerator();
                }
            });
        }
        catch (DuplicatedKeyException e)
        {
            // not-reachable
            throw new RuntimeException(e);
        }

        // And now add inputs.
        try
        {
            final LoadedComponentFactory [] factories = helper.loadComponentFactoriesFromDir(inputScripts);
            for (int i = 0; i < factories.length; i++)
            {
                final LoadedComponentFactory loaded = factories[i];
                // check if the required properties are present.
                final String tabName = (String) loaded.getProperty("tab.name");
                final String tabDesc = (String) loaded.getProperty("tab.description");
                if (tabName == null || tabDesc == null)
                {
                    logger.warn("The input factory: " + loaded.getId()
                        + " must specify 'tab.name' and 'tab.description' properties.");
                    continue;
                }
                controller.addLocalComponentFactory(loaded.getId(), loaded.getFactory());

                final Map otherProps = new HashMap();
                for (Iterator j = loaded.getProperties().entrySet().iterator(); j.hasNext();)
                {
                    final Map.Entry entry = (Map.Entry) j.next();
                    final String key = (String) entry.getKey();
                    if (key.startsWith("tab."))
                    {
                        otherProps.put(key, entry.getValue());
                    }
                }

                final boolean ignoreOnError = "true".equals(loaded.getProperty("tab.ignoreOnError"));
                try
                {
                    final boolean defaultTab = "true".equals(loaded.getProperty("tab.default"));
                    controller.addProcess(tabName, new LocalProcessBase(loaded.getId(), "collector",
                    /* filters */new String []
                    {
                        "enumerator"
                    }));
                    settings.add(new TabSearchInput(tabName, tabDesc, otherProps));
                    if (defaultTab)
                    {
                        settings.setDefaultTabIndex(settings.getInputTabs().size() - 1);
                    }
                    logger.info("Added input tab: " + tabName + " (component: " + loaded.getId() + ")");
                }
                catch (Exception e)
                {
                    if (ignoreOnError)
                    {
                        // ignore exception.
                        logger.warn("Skipping input tab: " + tabName + " (ignored exception: " +
                            StringUtils.chainExceptionMessages(e) + ")");
                    }
                    else
                    {
                        // rethrow.
                        throw e;
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Problems initializing input components.", e);
        }

        // Check if there are any inputs at all.
        if (settings.inputTabs.size() == 0)
        {
            throw new RuntimeException("At least one input tab must be defined.");
        }

        return controller;
    }

    /**
     * Initializes and returns a {@link LocalController} containing clustering algorithms.
     */
    static LocalControllerBase initializeAlgorithms(final Logger logger, File algorithmScripts,
        SearchSettings searchSettings)
    {
        if (!algorithmScripts.isDirectory())
        {
            throw new RuntimeException("Scripts for algorithm tabs not initialized.");
        }

        final LocalControllerBase controller = new ProfiledLocalController();
        final ControllerHelper helper = new ControllerHelper();

        try
        {
            // Add an input producer component now.
            controller.addLocalComponentFactory("input-demo-webapp", new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    return new ArrayInputComponent();
                }
            });
            // Add an output collector.
            controller.addLocalComponentFactory("output-demo-webapp", new LocalComponentFactory()
            {
                public LocalComponent getInstance()
                {
                    return new ArrayOutputComponent();
                }
            });
        }
        catch (DuplicatedKeyException e)
        {
            // not-reachable
            throw new RuntimeException(e);
        }

        final FileFilter processFilter = new FileFilter()
        {
            public boolean accept(File file)
            {
                return file.getName().startsWith("alg-");
            }
        };
        final FileFilter facetFilter = new FileFilter()
        {
            public boolean accept(File file)
            {
                return file.getName().startsWith("facet-");
            }
        };
        final FileFilter componentFilter = new FileFilter()
        {
            private final FileFilter subFilter = helper.getComponentFilter();

            public boolean accept(File file)
            {
                return this.subFilter.accept(file) && !file.getName().startsWith("alg-")
                    && !file.getName().startsWith("facet-");
            }
        };

        try
        {
            controller.setComponentAutoload(true);
            helper.addAll(controller, helper.loadComponentFactoriesFromDir(algorithmScripts, componentFilter));
            final LoadedProcess [] processes = helper.loadProcessesFromDir(algorithmScripts, processFilter);
            for (int i = 0; i < processes.length; i++)
            {
                final String shortName = processes[i].getProcess().getName();
                final String description = processes[i].getProcess().getDescription();
                final boolean defaultAlgorithm = "true".equals(processes[i].getAttributes().get("process.default"));
                searchSettings.addAlgorithm(new TabAlgorithm(shortName, description));
                if (defaultAlgorithm)
                {
                    searchSettings.setDefaultAlgorithmIndex(searchSettings.getAlgorithms().size() - 1);
                }
                controller.addProcess(shortName, processes[i].getProcess());
                logger.info("Added algorithm: " + shortName);
            }
            
            final LoadedProcess [] facets = helper.loadProcessesFromDir(algorithmScripts, facetFilter);
            if (facets.length > 0) 
            {
                searchSettings.addFacet(new TabAlgorithm("Topics", ""));
                searchSettings.setTopicsFacetIndex(0);
                for (int i = 0; i < facets.length; i++)
                {
                    final String shortName = facets[i].getProcess().getName();
                    searchSettings.addFacet(new TabAlgorithm(shortName, ""));
                    controller.addProcess(shortName, facets[i].getProcess());
                    logger.info("Added facet: " + shortName);
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Problems initializing algorithms.", e);
        }

        return controller;
    }

    /**
     * Initializes the resource bundle with localized messages.
     */
    static ResourceBundle initializeResourceBundle(final ServletConfig config)
    {
        String locale = getLocalizationString(config);

        return ResourceBundle.getBundle("messages", new Locale(locale), Thread
            .currentThread().getContextClassLoader());
    }

    public static String getLocalizationString(final ServletConfig config)
    {
        String locale = config.getInitParameter("localization");
        if (locale == null)
        {
            locale = Constants.DEFAULT_LOCALE;
        }
        return locale;
    }

    /**
     * Initializes the XML feed key.
     */
    static String initializeXmlFeedKey(final ServletConfig config)
    {
        return config.getInitParameter("xml.feed.key");
    }

    /**
     * 
     */
    public static QueryExpander initializeQueryExpander(final Logger logger, ServletConfig config)
    {
        String expanderClassName = config.getInitParameter("query.expander");
        if (expanderClassName == null || expanderClassName.length() == 0)
        {
            return null;
        }

        try
        {
            Class expanderClass = Class.forName(expanderClassName);
            QueryExpander instance = (QueryExpander)expanderClass.newInstance();
            instance.configure(config);
            return instance;
        }
        catch (ClassNotFoundException e)
        {
            logger.warn("Query expander class not found: " + expanderClassName);
            return null;
        }
        catch (InstantiationException e)
        {
            logger.warn("Could not instantiate Query expander class", e);
            return null;
        }
        catch (IllegalAccessException e)
        {
            logger.warn("Could not instantiate Query expander class", e);
            return null;
        }
        catch (ClassCastException e)
        {
            logger.warn("Query expander class does not implement the org.carrot2.webapp.QueryExpander interface");
            return null;
        }
    }
}
