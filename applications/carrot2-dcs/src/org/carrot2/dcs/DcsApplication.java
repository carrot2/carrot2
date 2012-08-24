package org.carrot2.dcs;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.IProcessingComponent;
import org.carrot2.core.ProcessingComponentSuite;
import org.carrot2.source.SearchEngineBase;
import org.carrot2.text.linguistic.LexicalDataLoaderDescriptor;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.IResourceLocator;
import org.carrot2.util.resource.PrefixDecoratorLocator;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.resource.ResourceLookup.Location;
import org.carrot2.util.resource.ServletContextLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DcsApplication extends Application
{
    @Context
    private ServletContext servletContext;

    /** System property to enable class path search for resources in tests. */
    final static String ENABLE_CLASSPATH_LOCATOR = "enable.classpath.locator";

    private static final Logger log = LoggerFactory.getLogger("DcsApplication");

    private transient ProcessingComponentSuite componentSuite;

    transient Controller controller;

    public DcsApplication() throws SAXException
    {
        log.info("Starting Document Clustering Server application");
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() throws Exception
    {
        // We'll use WEB-INF locators (and classpath for tests)
        final List<IResourceLocator> resourceLocators = Lists.newArrayList();
        resourceLocators.add(new PrefixDecoratorLocator(new ServletContextLocator(
            servletContext), "/WEB-INF/suites/"));
        if (Boolean.getBoolean(ENABLE_CLASSPATH_LOCATOR))
        {
            resourceLocators.add(Location.CONTEXT_CLASS_LOADER.locator);
        }

        // Load component suite
        log.info("Loading component suite");
        final ResourceLookup suitesLookup = new ResourceLookup(resourceLocators);
        final IResource suiteResource = suitesLookup.getFirst("suite-dcs.xml");
        if (suiteResource == null)
        {
            throw new Exception(
                "Suite file not found in servlet context's /WEB-INF/suites: "
                    + "suite-dcs.xml");
        }
        componentSuite = ProcessingComponentSuite
            .deserialize(suiteResource, suitesLookup);
        if (componentSuite.getAlgorithms().size() == 0)
        {
            throw new ServletException("Component suite has no algorithms.");
        }

        // Initialize controller
        log.info("Initializing controller");

        final List<Class<? extends IProcessingComponent>> cachedComponentClasses = Lists
            .newArrayListWithExpectedSize(2);

        // Cache only search-engine-type sources
        cachedComponentClasses.add(SearchEngineBase.class);
        controller = ControllerFactory.createCachingPooling(cachedComponentClasses
            .toArray(new Class [cachedComponentClasses.size()]));

        // Load resources only from WEB-INF
        final List<IResourceLocator> locators = Lists.newArrayList();
        locators.add(new PrefixDecoratorLocator(
            new ServletContextLocator(servletContext), "/WEB-INF/resources/"));
        if (Boolean.getBoolean(ENABLE_CLASSPATH_LOCATOR))
        {
            locators.add(Location.CONTEXT_CLASS_LOADER.locator);
        }

        final Map<String, Object> attrs = Maps.newHashMap();
        LexicalDataLoaderDescriptor.attributeBuilder(attrs).resourceLookup(
            new ResourceLookup(locators));
        controller.init(attrs, componentSuite.getComponentConfigurations());
    }

    @PreDestroy
    public void dispose()
    {
        controller.dispose();
    }

    public Set<Class<?>> getClasses()
    {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(ClusteringResource.class);
        return s;
    }
}