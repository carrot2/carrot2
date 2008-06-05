package org.carrot2.workbench.core.ui;

import java.io.StringWriter;
import java.net.URL;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.*;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class DocumentListBrowser
{
    private Browser browser;
    private ISelectionListener postSelectionListener;
    private IWorkbenchSite site;

    public void init(IWorkbenchSite site, Composite parent)
    {
        this.site = site;
        browser = new Browser(parent, SWT.NONE);
        attachToPostSelection(site.getPage());
        attachToLocationChanging();
    }

    private void attachToPostSelection(IWorkbenchPage page)
    {
        postSelectionListener = new ISelectionListener()
        {
            public void selectionChanged(IWorkbenchPart part, ISelection selection)
            {
                if (!selection.isEmpty() && selection instanceof IStructuredSelection)
                {
                    IStructuredSelection selected = (IStructuredSelection) selection;
                    if (selected.getFirstElement() instanceof Cluster)
                    {
                        if (selected.size() == 1)
                        {
                            updateBrowserText((Cluster) selected.getFirstElement());
                        }
                        else
                        {
                            clear();
                        }
                    }
                }
            }
        };
        page.addPostSelectionListener(postSelectionListener);
    }

    private void attachToLocationChanging()
    {
        browser.addLocationListener(new LocationAdapter()
        {
            @Override
            public void changing(LocationEvent event)
            {
                // browser was refreshed using setText() method
                if (event.location.equals("about:blank"))
                {
                    return;
                }
                try
                {
                    CorePlugin.getDefault().getWorkbench().getBrowserSupport()
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
                event.doit = false;
            }
        });
    }

    public void populateToolbar(IToolBarManager manager)
    {
    }

    public void updateBrowserText(ProcessingResult result)
    {
        VelocityContext context = new VelocityContext();
        context.put("result", result);

        if (result.getAttributes().get(AttributeNames.RESULTS_TOTAL) != null)
        {
            final long total =
                (Long) result.getAttributes().get(AttributeNames.RESULTS_TOTAL);
            context.put("results-total-formatted", String.format(Locale.ENGLISH, "%1$,d",
                total));
        }

        final String query = (String) result.getAttributes().get("query");
        context.put("queryEscaped", StringEscapeUtils.escapeHtml(query));

        merge(context);
    }

    public void updateBrowserText(Cluster cluster)
    {
        VelocityContext context = new VelocityContext();
        context.put("documents", cluster.getAllDocuments(Document.BY_ID_COMPARATOR));

        merge(context);
    }

    private void merge(VelocityContext context)
    {
        Template template = null;
        StringWriter sw = new StringWriter();
        try
        {
            template = Velocity.getTemplate("documents-list.vm");
            template.merge(context, sw);
        }
        catch (Exception e)
        {
            Utils.logError("Error while loading template", e, true);
            return;
        }

        browser.setText(sw.toString());
    }

    public Control getControl()
    {
        return browser;
    }

    public void dispose()
    {
        if (postSelectionListener != null)
        {
            site.getPage().removePostSelectionListener(postSelectionListener);
        }
    }

    public void clear()
    {
        browser.setText("");
    }

    public String getPartName()
    {
        return "Documents";
    }
}
