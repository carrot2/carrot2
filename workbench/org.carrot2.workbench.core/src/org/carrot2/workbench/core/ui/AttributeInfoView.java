package org.carrot2.workbench.core.ui;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeInstance;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.DisposeBin;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.velocity.VelocityInitializer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.google.common.collect.Maps;

/**
 * A view displaying short help based on the {@link AttributeDescriptor}.
 */
public class AttributeInfoView extends ViewPart
{
    /**
     * Public identifier of this view.
     */
    public static final String ID = "org.carrot2.workbench.core.views.attributeInfo";

    /**
     * Bundle folder in which templates are located.
     */
    private static final String TEMPLATES_PREFIX = "/templates/";

    /*
     * 
     */
    private static final String TEMPLATE_ATTR_INFO = "attribute-descriptor.vm";

    /*
     * 
     */
    private DisposeBin bin = new DisposeBin();

    /**
     * Browser control to display the info.
     */
    private Browser browser;

    /**
     * Velocity instance for processing templates.
     */
    private static RuntimeInstance velocity;

    public AttributeInfoView()
    {
        synchronized (this.getClass())
        {
            if (velocity == null)
            {
                velocity = VelocityInitializer.createInstance(
                    WorkbenchCorePlugin.PLUGIN_ID, TEMPLATES_PREFIX);
            }
        }
    }

    /*
     * 
     */
    @Override
    public void createPartControl(Composite parent)
    {
        this.browser = new Browser(parent, SWT.NONE);
        bin.add(browser);
    }

    /**
     * Update browser with new HTML rendered using Velocity template.
     */
    public void show(AttributeDescriptor descriptor)
    {
        final VelocityContext context = VelocityInitializer.createContext();

        context.put("descriptor", descriptor);

        /*
         * Extract public fields to a map for Velocity.
         */
        final HashMap<String, Object> fields = Maps.newHashMap();
        copyFields(fields, "", descriptor);
        context.put("fields", fields);

        StringWriter sw = new StringWriter();
        try
        {
            final Template template = velocity.getTemplate(TEMPLATE_ATTR_INFO, "UTF-8");
            template.merge(context, sw);
        }
        catch (Exception e)
        {
            Utils.logError("Error while loading template", e, true);
            return;
        }

        browser.setText(sw.toString());
    }

    /*
     * Copy public fields to a map. Velocity does not support field access
     */
    private void copyFields(Map<String, Object> fields, String prefix, Object obj)
    {
        try
        {
            for (Field f : obj.getClass().getFields())
            {
                fields.put(f.getName(), f.get(obj));
            }
        }
        catch (IllegalAccessException e)
        {
            // Silently ignore.
        }
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        if (browser != null)
        {
            browser.setFocus();
        }
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        super.dispose();
        bin.dispose();
    }
}
