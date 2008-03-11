package org.carrot2.workbench.core.ui;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.carrot2.core.Document;
import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;

public class DocumentListBrowser
{
    public Browser createControls(final Composite parent, Collection<Document> documents)
        throws PartInitException
    {

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
            throw new PartInitException("Error while loading template", e);
        }

        Browser browser = new Browser(parent, SWT.NONE);
        browser.setText(sw.toString());
        return browser;
        // return list;
    }
}
