package org.carrot2.workbench.core.ui;

import java.io.StringWriter;
import java.net.URL;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.carrot2.core.Cluster;
import org.carrot2.core.ProcessingResult;
import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.jobs.ProcessingStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class DocumentListBrowser
{
    private Browser browser;

    public DocumentListBrowser(IWorkbenchSite site, Composite parent, ProcessingJob job)
    {
        browser = new Browser(parent, SWT.NONE);
        attachToJobDone(job);
        attachToSelectionChanged(site.getSelectionProvider());
        attachToLocationChanging();
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

    private void attachToSelectionChanged(ISelectionProvider provider)
    {
        provider.addSelectionChangedListener(new ISelectionChangedListener()
        {

            public void selectionChanged(SelectionChangedEvent event)
            {
                if (event.getSelection().isEmpty())
                {
                    browser.setText("");
                    return;
                }
                IStructuredSelection selection =
                    (IStructuredSelection) event.getSelection();
                if (selection.size() > 1)
                {
                    browser.setText("");
                }
                else
                {
                    updateBrowserText((Cluster) selection.getFirstElement());
                }
            }

        });
    }

    private void attachToJobDone(ProcessingJob job)
    {
        job.addJobChangeListener(new JobChangeAdapter()
        {
            @Override
            public void done(IJobChangeEvent event)
            {
                if (event.getResult().getSeverity() == IStatus.OK)
                {
                    final ProcessingResult result =
                        ((ProcessingStatus) event.getResult()).result;
                    Utils.asyncExec(new Runnable()
                    {
                        public void run()
                        {
                            updateBrowserText(result);
                        }
                    });
                }
            }
        });
    }

    public void updateBrowserText(ProcessingResult result)
    {
        VelocityContext context = new VelocityContext();
        context.put("result", result);

        final String query = (String) result.getAttributes().get("query");
        context.put("queryEscaped", StringEscapeUtils.escapeHtml(query));

        merge(context);
    }

    public void updateBrowserText(Cluster cluster)
    {
        VelocityContext context = new VelocityContext();
        context.put("cluster", cluster);

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
}
