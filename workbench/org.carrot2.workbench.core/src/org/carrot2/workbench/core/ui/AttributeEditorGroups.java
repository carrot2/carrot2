package org.carrot2.workbench.core.ui;

import static org.eclipse.swt.SWT.NONE;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.editors.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.*;

import com.google.common.collect.Maps;

/**
 * An SWT composite displaying groups of {@link IAttributeEditor}s.
 */
public final class AttributeEditorGroups extends SharedScrolledComposite implements
    IAttributeChangeProvider
{
    /**
     * Method of grouping attribute editors.
     */
    private GroupingMethod grouping = GroupingMethod.GROUP;

    /**
     * Main control in which editors are embedded.
     */
    private Composite mainControl;

    /**
     * Attribute change listeners.
     */
    private final List<IAttributeListener> listeners = new CopyOnWriteArrayList<IAttributeListener>();

    /**
     * Forward events from editors to external listeners.
     */
    private final IAttributeListener forwardListener = new IAttributeListener()
    {
        public void attributeChange(AttributeChangedEvent event)
        {
            for (IAttributeListener listener : listeners)
            {
                listener.attributeChange(event);
            }
        }
    };

    /**
     * Descriptors of attribute editors to be created.
     */
    private BindableDescriptor descriptor;

    /**
     * A hashmap of attribute IDs to {@link AttributeEditorList} that contain them.
     * 
     * @see #setAttribute(String, Object)
     */
    private Map<String, AttributeEditorList> attributeEditors = Maps.newHashMap();

    /**
     * Builds the component for a given {@link BindableDescriptor}.
     */
    public AttributeEditorGroups(Composite parent, BindableDescriptor descriptor)
    {
        super(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        this.setDelayedReflow(false);

        this.descriptor = descriptor;
        createComponents();
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        super.dispose();
        this.listeners.clear();
    }

    /**
     * Create GUI components (sections, editors).
     */
    private void createComponents()
    {
        final Composite mainControl = new Composite(this, NONE);
        this.setContent(mainControl);
        mainControl.setLayout(new GridLayout());
        this.mainControl = mainControl;

        createEditors(mainControl, grouping);

        /*
         * Expand controls horizontally, do not expand vertically.
         */
        this.setExpandHorizontal(true);
        this.setExpandVertical(false);
        this.reflow(true);
    }

    /**
     * Create editors based on the given {@link GroupingMethod}.
     */
    private void createEditors(Composite parent, GroupingMethod grouping)
    {
        /*
         * Create a filtered view of attribute descriptors.
         */
        final BindableDescriptor descriptor = this.descriptor.group(grouping);

        /*
         * Create sections for attribute groups.
         */
        for (Object groupKey : descriptor.attributeGroups.keySet())
        {
            createEditorGroup(mainControl, groupKey.toString(), descriptor,
                descriptor.attributeGroups.get(groupKey));
        }

        /*
         * Add remaining attributes.
         */
        if (!descriptor.attributeDescriptors.isEmpty())
        {
            createEditorGroup(mainControl, "Ungrouped", descriptor,
                descriptor.attributeDescriptors);
        }
    }

    /**
     * Create an editor group associated with a group of attributes.
     */
    @SuppressWarnings("unchecked")
    private void createEditorGroup(final Composite parent, String groupName,
        BindableDescriptor descriptor, Map<String, AttributeDescriptor> attributes)
    {
        /*
         * Vertical space between groups
         */
        final int GROUP_VSPACE = 10;

        /*
         * Prepare a group widget.
         */
        final int style = ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT;
        final Section group = new Section(parent, style);
        group.setText(groupName);
        group.setExpanded(true);
        group.setSeparatorControl(new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL));

        /*
         * Prepare editors inside the group widget. Add some extra space at the bottom of
         * the control to separate from the next group
         */
        final Composite inner = new Composite(group, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginBottom = GROUP_VSPACE;
        inner.setLayout(layout);

        final AttributeEditorList editorList = new AttributeEditorList(inner, attributes,
            descriptor.type);

        final GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = false;
        editorList.setLayoutData(data);

        /*
         * Update mapping between attribute keys and editors (for event routing).
         */

        for (String key : attributes.keySet())
        {
            attributeEditors.put(key, editorList);
        }

        /*
         * Register for event notifications from editors.
         */
        editorList.addAttributeChangeListener(forwardListener);

        group.setClient(inner);

        /*
         * Grid data for the entire group.
         */
        final GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = false;
        group.setLayoutData(gd);

        /*
         * On group expand/contract, reflow the scrollable composite.
         */
        group.addExpansionListener(new ExpansionAdapter()
        {
            public void expansionStateChanged(ExpansionEvent e)
            {
                AttributeEditorGroups.this.reflow(true);
            }
        });
    }

    /**
     * On reflow, update both vertical and horizontal scroller (for some reason the
     * horizontal one is neglected in current version of Eclipse).
     */
    @Override
    public void reflow(boolean flushCache)
    {
        super.reflow(flushCache);

        final ScrollBar hbar = getHorizontalBar();
        if (hbar != null)
        {
            final Rectangle clientArea = getClientArea();
            final int increment = Math.max(clientArea.width - 5, 5);
            hbar.setPageIncrement(increment);
        }
    }

    /**
     * 
     */
    public void addAttributeChangeListener(IAttributeListener listener)
    {
        this.listeners.add(listener);
    }

    /**
     * 
     */
    public void removeAttributeChangeListener(IAttributeListener listener)
    {
        this.listeners.remove(listener);
    }

    /**
     * 
     */
    public void setAttribute(String key, Object value)
    {
        final AttributeEditorList attributeEditorList = this.attributeEditors.get(key);
        if (attributeEditorList != null)
        {
            attributeEditorList.setAttribute(key, value);
        }
    }
}
