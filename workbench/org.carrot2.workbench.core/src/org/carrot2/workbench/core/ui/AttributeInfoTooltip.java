
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

import static org.carrot2.workbench.core.ui.StyledTextContentBuilder.BOLD;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.AttributeMetadata;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.progress.UIJob;

/**
 * A tooltip for an {@link AttributeDescriptor}.
 */
public class AttributeInfoTooltip extends ToolTip
{
    private final AttributeDescriptor descriptor;

    private final SelectionListener listener = new SelectionAdapter()
    {
        public void widgetSelected(SelectionEvent e)
        {
            showInView();
        }
    };

    /*
     * 
     */
    private AttributeInfoTooltip(Control parent, AttributeDescriptor descriptor)
    {
        super(parent, ToolTip.NO_RECREATE, false);

        this.descriptor = descriptor;

        this.setShift(new Point(-5, -5));
        this.setRespectDisplayBounds(true);
        this.setRespectMonitorBounds(true);
        this.setHideOnMouseDown(false);
        
        // This shows a help cursor when you hover over the target component. Looks
        // too strange for me to actually commit it in, but take a look yourself, maybe
        // you'll like it.

        // parent.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HELP));
    }

    /*
     * 
     */
    private void showInView()
    {
        final IWorkbenchPage page = 
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        if (page != null)
        {
            try
            {
                IViewPart view2 = page.findView(AttributeInfoView.ID);
                if (!page.isPartVisible(view2))
                {
                    view2 = page.showView(AttributeInfoView.ID);
                }

                ((AttributeInfoView) view2).show(descriptor);
                hide();
            }
            catch (PartInitException x)
            {
                // Ignore, nothing to do here.
            }
        }
    }
    
    @Override
    protected boolean shouldCreateToolTip(Event event)
    {
        boolean value = super.shouldCreateToolTip(event);
        if (value && isSynchronizedWithView())
        {
            final Job job = new UIJob("Show attribute info") {
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor)
                {
                    showInView();
                    return Status.OK_STATUS;
                }
            };
            job.setSystem(true);
            job.setPriority(Job.DECORATE);
            job.schedule();

            value = false;
        }
        return value;
    }

    /*
     * 
     */
    @Override
    protected Composite createToolTipContentArea(Event event, Composite parent)
    {
        final Display display = parent.getShell().getDisplay();

        final Composite inner = new Composite(parent, SWT.NONE);
        final GridLayout layout = GridLayoutFactory.fillDefaults().spacing(0, 0).margins(2, 2).create();
        inner.setLayout(layout);

        final StyledText text = createStyledText(inner);
        text.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        text.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));

        if (!isSynchronizedWithView())
        {
            final Label separator = new Label(inner, SWT.SEPARATOR | SWT.HORIZONTAL);
            separator.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BOTTOM).create());
    
            final Link link = new Link(inner, SWT.NONE);
            link.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
            link.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
            link.setText("<a>Show full documentation</a>.");
            link.setLayoutData(GridDataFactory.swtDefaults().align(SWT.TRAIL, SWT.CENTER).create());
            link.addSelectionListener(listener);
        }

        parent.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        inner.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

        return inner;
    }

    /*
     * 
     */
    private boolean isSynchronizedWithView()
    {
        return WorkbenchCorePlugin.getDefault()
            .getPreferenceStore().getBoolean(PreferenceConstants.ATTRIBUTE_INFO_SYNC);
    }

    /**
     * Create styled text with attribute descriptor info.
     */
    private StyledText createStyledText(Composite parent)
    {
        final StyledText text = new StyledText(parent, SWT.READ_ONLY | SWT.WRAP);
        text.setLayoutData(
            GridDataFactory.fillDefaults().minSize(250, SWT.DEFAULT).hint(300, SWT.DEFAULT)
            .grab(true, true).create());

        final StyledTextContentBuilder builder = new StyledTextContentBuilder();
        buildContent(builder);

        text.setText(builder.getText());
        text.setStyleRanges(builder.getStyleRanges());

        return text;
    }

    /**
     * Build text content to be displayed in the tooltip.
     */
    protected void buildContent(StyledTextContentBuilder builder)
    {
        final AttributeMetadata meta = descriptor.metadata;
        final String INDENT = "    ";

        builder.println("Attribute:", BOLD)
            .print(INDENT)
            .println(meta != null ? meta.getLabelOrTitle() : descriptor.key)
            .println();

        builder.println("Availability:", BOLD)
            .print(INDENT)
            .print(descriptor.requiredAttribute ? "required" : "optional");

        if (descriptor.defaultValue != null)
        {
            builder.println().println()
                .println("Default value:", BOLD)
                .print(INDENT)
                .print(descriptor.defaultValue.toString());
        }
    }

    /**
     * Attach a custom tooltip to the {@link Control}.
     */
    public static void attach(Control control, AttributeDescriptor descriptor)
    {
        new AttributeInfoTooltip(control, descriptor);
    }
}
