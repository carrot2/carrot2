package org.carrot2.workbench.core.ui;

import java.io.StringWriter;
import java.util.Collection;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.carrot2.core.*;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.jobs.ProcessingJob;
import org.carrot2.workbench.core.jobs.ProcessingStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;

public class DocumentListBrowser
{
    private Browser browser;

    public DocumentListBrowser(IWorkbenchSite site, Composite parent, ProcessingJob job)
    {
        browser = new Browser(parent, SWT.NONE);
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
                            updateBrowserText(result.getDocuments());
                        }
                    });
                }
            }
        });

        site.getSelectionProvider().addSelectionChangedListener(
            new ISelectionChangedListener()
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

    public void updateBrowserText(Collection<Document> documents)
    {
        VelocityContext context = new VelocityContext();
        context.put("documents", documents);

        merge(context);
    }

    public void updateBrowserText(Cluster cluster)
    {
        VelocityContext context = new VelocityContext();
        context.put("documents", cluster.getAllDocuments());
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
