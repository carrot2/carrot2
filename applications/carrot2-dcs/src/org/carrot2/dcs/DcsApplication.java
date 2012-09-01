package org.carrot2.dcs;

import java.io.IOException;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.IProcessingComponent;
import org.carrot2.core.ProcessingComponentSuite;
import org.carrot2.core.ProcessingResult;
import org.carrot2.source.SearchEngineBase;
import org.carrot2.text.linguistic.LexicalDataLoaderDescriptor;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.IResourceLocator;
import org.carrot2.util.resource.PrefixDecoratorLocator;
import org.carrot2.util.resource.ResourceLookup;
import org.carrot2.util.resource.ResourceLookup.Location;
import org.carrot2.util.resource.ServletContextLocator;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DcsApplication extends Application
{
    /** System property to enable class path search for resources in tests. */
    final static String ENABLE_CLASSPATH_LOCATOR = "enable.classpath.locator";

    /** System property to disable log file appender. */
    final static String DISABLE_LOGFILE_APPENDER = "disable.logfile";

    private static final Logger log = Logger.getLogger("dcs");

    @Context
    private ServletContext servletContext;

    transient ProcessingComponentSuite componentSuite;

    transient Controller controller;

    transient DcsConfig config;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() throws Exception
    {
        // Initialize appender
        if (!Boolean.getBoolean(DISABLE_LOGFILE_APPENDER))
        {
            Logger.getRootLogger().addAppender(getLogAppender(servletContext));
        }

        final PrefixDecoratorLocator webInfLocator = new PrefixDecoratorLocator(
            new ServletContextLocator(servletContext), "/WEB-INF/");

        // Load config
        log.info("Loading configuration file");
        final IResource [] configResource = webInfLocator.getAll("dcs.xml");
        if (configResource.length > 0)
        {
            config = DcsConfig.deserialize(configResource[0]);
        }
        else
        {
            log.warn("Configuration file (WEB-INF/dcs.xml) not found, using defaults.");
            config = new DcsConfig();
        }

        // We'll use WEB-INF locators (and classpath for tests)
        final List<IResourceLocator> resourceLocators = Lists.newArrayList();
        resourceLocators.add(new PrefixDecoratorLocator(webInfLocator, "suites/"));
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
        if (componentSuite.getAlgorithms().isEmpty())
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
        s.add(MetadataResource.class);
        s.add(FormatsResource.class);

        s.add(InvalidInputExceptionMapper.class);
        s.add(ProcessingExceptionMapper.class);

        return s;
    }

    ProcessingResult process(Map<String, Object> attrs, String source, String algorithm)
    {
        if (componentSuite.getSources().isEmpty())
        {
            throw new InvalidInputException("Component suite has no document sources."
                + " Only directly-fed documents can be clustered.");
        }
        return controller.process(attrs,
            firstNonBlank(source, componentSuite.getSources().get(0).getId()),
            providedOrDefaultAlgorithm(algorithm));
    }

    ProcessingResult process(Map<String, Object> attrs, String algorithm)
    {
        return controller.process(attrs, providedOrDefaultAlgorithm(algorithm));
    }

    ResponseBuilder ok()
    {
        return Response.ok().header("Access-Control-Allow-Origin", config.accessControlAllowOrigin);
    }

    private String providedOrDefaultAlgorithm(String algorithm)
    {
        return Objects.firstNonNull(algorithm, componentSuite.getAlgorithms().get(0)
            .getId());
    }

    private FileAppender getLogAppender(ServletContext context) throws IOException
    {
        String contextPath = context.getContextPath();
        if (StringUtils.isBlank(contextPath))
        {
            contextPath = "root";
        }

        contextPath = contextPath.replaceAll("[^a-zA-Z0-9\\-]", "");
        final String catalinaHome = System.getProperty("catalina.home");
        final String logPrefix = (catalinaHome != null ? catalinaHome + "/logs" : "logs");

        final FileAppender appender = new FileAppender(new PatternLayout(
            "%d{ISO8601} [%-5p] [%c] %m%n"), logPrefix + "/c2-dcs-" + contextPath
            + "-full.log", true);

        appender.setEncoding(Charsets.UTF_8.name());
        appender.setImmediateFlush(true);

        return appender;
    }
    
    private String firstNonBlank(String... strings)
    {
        for (String string : strings)
        {
            if (!Strings.isNullOrEmpty(string)) {
                return string;
            }
        }
        
        return null;
    }
}