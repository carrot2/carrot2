package org.carrot2.workbench.core.ui;

import java.io.StringWriter;
import java.net.URL;
import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * Simple SWT composite displaying a list of {@link Document} or {@link Cluster} objects.
 */
public final class DocumentList extends Composite
{
    /**
     * Template displayed in {@link #show(ProcessingResult)}.
     */
    private static final String TEMPLATE_PROCESSING_RESULT = "processing-result.vm";
    
    /**
     * Template displayed in {@link #clear()}.
     */
    private static final String TEMPLATE_CLEAR = "clear.vm";

    /**
     * Lazy velocity initialization flag.
     */
    private static boolean initialized;

    /**
     * Internal HTML browser for displaying rendered results.
     */
    private Browser browser;

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
                initVelocity();
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
            /*
            final List<List<Document>> clusterDocuments = Lists.newArrayList();
            for (Cluster c : clusters)
            {
                clusterDocuments.add(c.getAllDocuments(Document.BY_ID_COMPARATOR));
            }
            */

            final VelocityContext context = new VelocityContext();

            final Comparator<Document> comparator = Document.BY_ID_COMPARATOR;
            context.put("comparator", comparator);
            context.put("clusters", clusters);

            update(context, "clusters.vm");
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
        Template template = null;
        StringWriter sw = new StringWriter();
        try
        {
            template = Velocity.getTemplate(templateName);
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
    private static void initVelocity()
    {
        final Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("class.resource.loader.class", ClasspathResourceLoader.class
            .getName());
    
        // Disable separate Velocity logging.
        p.setProperty(RuntimeConstants.RUNTIME_LOG, "");
    
        try
        {
            Velocity.init(p);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Velocity initialization failed.", e);
        }
    }
}
