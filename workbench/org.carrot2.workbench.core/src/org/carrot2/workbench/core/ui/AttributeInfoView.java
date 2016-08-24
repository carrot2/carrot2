
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeInstance;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.*;
import org.carrot2.workbench.core.ui.actions.AttributeInfoSyncAction;
import org.carrot2.workbench.velocity.VelocityInitializer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;

import org.carrot2.shaded.guava.common.collect.Maps;

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
    public void init(IViewSite site) throws PartInitException
    {
        super.init(site);

        /*
         * Create toolbar and menu contributions.
         */
        final IActionBars bars = getViewSite().getActionBars();
        createToolbar(bars.getToolBarManager());
        bars.updateActionBars();
    }
    
    /*
     * 
     */
    private void createToolbar(IToolBarManager toolBarManager)
    {
        final IAction action = new ActionDelegateProxy(new AttributeInfoSyncAction(), SWT.TOGGLE);
        action.setImageDescriptor(WorkbenchCorePlugin.getImageDescriptor("icons/help-sync.png"));
        action.setToolTipText("Update when attribute tooltip is shown.");

        toolBarManager.add(action);
    }

    /*
     * 
     */
    @Override
    public void createPartControl(Composite parent)
    {
        this.browser = BrowserFacade.createNew(parent, SWT.NONE);
        bin.add(browser);
        
        clear();
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
        
        /*
         * Add artificial fields.
         */
        fields.put("processingAttribute", descriptor.getAnnotation(Processing.class) != null);
        fields.put("initAttribute", descriptor.getAnnotation(Init.class) != null);

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

    /**
     * Clears the view.
     */
    public void clear()
    {
        final VelocityContext context = VelocityInitializer.createContext();
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
