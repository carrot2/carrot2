package org.carrot2.workbench.core.ui;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchSite;

public class DocumentListBrowser
{
    private Browser browser;

    public DocumentListBrowser(IWorkbenchSite site, Composite parent)
    {
        browser = new Browser(parent, SWT.NONE);
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
                        updateBrowserText(((Cluster) selection.getFirstElement())
                            .getAllDocuments());
                    }
                }

            });
        browser.addLocationListener(new LocationAdapter()
        {

            public void changing(LocationEvent event)
            {
                if (event.location.startsWith("msg:"))
                {
                    MessageBox box =
                        new MessageBox(Display.getDefault().getActiveShell());
                    box.setMessage(event.location.substring("msg:".length()));
                    box.open();
                    event.doit = false;
                }
            }

        });
    }

    public void updateBrowserText(Collection<Document> documents)
    {
        // TODO: some management of velocity should be made
        VelocityContext context = new VelocityContext();
        context.put("documents", documents);

        Template template = null;
        StringWriter sw = new StringWriter();
        try
        {
            Properties p = new Properties();
            p.setProperty("resource.loader", "class");
            p.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

            Velocity.init(p);
            template = Velocity.getTemplate("documents-list.vm");
            template.merge(context, sw);
        }
        catch (Exception e)
        {
            CorePlugin.getDefault().getLog().log(
                new OperationStatus(IStatus.ERROR, CorePlugin.PLUGIN_ID, -1,
                    "Error while loading template", e));
            // throw new PartInitException("Error while loading template", e);
            return;
        }

        browser.setText(sw.toString());
    }

    public Control getControl()
    {
        return browser;
    }
}
