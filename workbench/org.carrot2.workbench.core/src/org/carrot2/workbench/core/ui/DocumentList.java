package org.carrot2.workbench.core.ui;

import java.io.StringWriter;
import java.net.URL;
import java.util.Comparator;
import java.util.Locale;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeInstance;
import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.velocity.BundleResourceLoader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * Simple SWT composite displaying a list of {@link Document} or {@link Cluster} objects.
 */
public final class DocumentList extends Composite
{
    /**
     * Bundle folder in which templates are located.
     */
    private static final String TEMPLATES_PREFIX = "/templates/";

    /**
     * Template displayed in {@link #show(ProcessingResult)}.
     */
    private static final String TEMPLATE_PROCESSING_RESULT = "processing-result.vm";
    
    /**
     * Template displayed in {@link #clear()}.
     */
    private static final String TEMPLATE_CLEAR = "clear.vm";

    /**
     * Template displayed in {@link #show(Cluster...)}.
     */
    private static final String TEMPLATE_CLUSTERS = "clusters.vm";
    

    /**
     * Lazy velocity initialization flag.
     */
    private static boolean initialized;

    /**
     * Internal HTML browser for displaying rendered results.
     */
    private Browser browser;

    /**
     * Velocity instance for processing templates.
     */
    private static RuntimeInstance velocity;

    /*
     * 
     */
    public DocumentList(Composite parent, int style)
    {
        super(parent, style);

        synchronized (this.getClass())
        {
            if (!initialized)
            {
                velocity = initVelocity();
                initialized = true;
            }
        }

        createComponents();
    }

    /**
     * Show a template displaying all results from a {@link ProcessingResult}. We simply
     * display all documents available (no explicit cluster markers).
     */
    public void show(ProcessingResult result)
    {
        final VelocityContext context = new VelocityContext();
        context.put("result", result);

        if (result.getAttributes().get(AttributeNames.RESULTS_TOTAL) != null)
        {
            final long total = (Long) result.getAttributes().get(
                AttributeNames.RESULTS_TOTAL);

            context.put(AttributeNames.RESULTS_TOTAL + "-formatted", 
                String.format(Locale.ENGLISH, "%1$,d", total));
        }

        final String query = (String) result.getAttributes().get("query");
        context.put("query-escaped", StringEscapeUtils.escapeHtml(query));

        update(context, TEMPLATE_PROCESSING_RESULT);
    }

    /**
     * Show a template displaying one or more {@link Cluster}s.
     */
    public void show(Cluster... clusters)
    {
        if (clusters.length == 0)
        {
            clear();
        }
        else
        {
            final VelocityContext context = new VelocityContext();

            final Comparator<Document> comparator = Document.BY_ID_COMPARATOR;
            context.put("comparator", comparator);
            context.put("clusters", clusters);

            update(context, TEMPLATE_CLUSTERS);
        }
    }

    /**
     * Clear the display.
     */
    public void clear()
    {
        update(null, TEMPLATE_CLEAR);
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        browser.dispose();
    }

    /**
     * Update browser with new HTML rendered using Velocity template.
     */
    private void update(VelocityContext context, String templateName)
    {
        StringWriter sw = new StringWriter();
        try
        {
            final Template template = velocity.getTemplate(templateName, "UTF-8");
            template.merge(context, sw);
        }
        catch (Exception e)
        {
            Utils.logError("Error while loading template", e, true);
            return;
        }

        browser.setText(sw.toString());
    }

    /**
     * Create GUI components.
     */
    private void createComponents()
    {
        this.setLayout(new FillLayout());

        browser = new Browser(this, SWT.NONE);

        /*
         * Attach to location change event and open 
         * a new browser instead of changing internal browser's location. 
         */
        browser.addLocationListener(new LocationAdapter()
        {
            @Override
            public void changing(LocationEvent event)
            {
                // Browser was refreshed using setText() method
                if (event.location.equals("about:blank"))
                {
                    return;
                }
    
                try
                {
                    WorkbenchCorePlugin.getDefault().getWorkbench().getBrowserSupport()
                        .createBrowser(
                            IWorkbenchBrowserSupport.AS_EDITOR
                                | IWorkbenchBrowserSupport.LOCATION_BAR
                                | IWorkbenchBrowserSupport.NAVIGATION_BAR
                                | IWorkbenchBrowserSupport.STATUS, null, null, null)
                        .openURL(new URL(event.location));
                }
                catch (Exception e)
                {
                    Utils.logError("Couldn't open internal browser", e, true);
                }
    
                // Cancel the internal event so that we don't open the new location.
                event.doit = false;
            }
        });
    }

    /**
     * Initialize Velocity engine.
     */
    private static RuntimeInstance initVelocity()
    {
        final ExtendedProperties p = new ExtendedProperties();
        p.setProperty("resource.loader", "bundle");
        p.setProperty("bundle.resource.loader.instance", 
            new BundleResourceLoader(WorkbenchCorePlugin.PLUGIN_ID, TEMPLATES_PREFIX));

        // Disable separate Velocity logging.
        p.setProperty(RuntimeConstants.RUNTIME_LOG, "");

        try
        {
            final RuntimeInstance velocity = new RuntimeInstance();
            velocity.setConfiguration(p);
            return velocity;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Velocity initialization failed.", e);
        }
    }
}
