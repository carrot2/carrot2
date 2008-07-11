package org.carrot2.workbench.core.ui;

import static org.eclipse.swt.SWT.NONE;

import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang.StringUtils;
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
import com.google.common.collect.Sets;

/**
 * An SWT composite displaying groups of {@link IAttributeEditor}s.
 */
public final class AttributeEditorGroups extends SharedScrolledComposite implements
    IAttributeChangeProvider
{
    /**
     * Method of grouping attribute editors.
     */
    private GroupingMethod grouping;

    /**
     * Main control in which editors are embedded.
     */
    private Composite mainControl;

    /**
     * External attribute change listeners.
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
    public AttributeEditorGroups(Composite parent, BindableDescriptor descriptor, GroupingMethod grouping)
    {
        super(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        this.setDelayedReflow(false);

        this.descriptor = descriptor;
        createComponents();

        /*
         * Set initial grouping and recreate components.
         */
        setGrouping(grouping);
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

        /*
         * Expand controls horizontally, do not expand vertically.
         */
        this.setExpandHorizontal(true);
        this.setExpandVertical(false);
        this.reflow(true);
    }

    /**
     * Reset the grouping of attributes inside this component. 
     */
    public void setGrouping(GroupingMethod newGrouping)
    {
        if (newGrouping == grouping)
        {
            return;
        }

        this.grouping = newGrouping;

        this.mainControl.setRedraw(false);
        disposeEditors();
        createEditors(mainControl, grouping);
        this.mainControl.setRedraw(true);

        this.reflow(true);
    }

    /**
     * Returns the current grouping state.
     */
    public GroupingMethod getGrouping()
    {
        return grouping;
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
            final String groupLabel;
            final String groupTooltip;
            if (groupKey instanceof Class)
            {
                groupLabel = ((Class<?>) groupKey).getSimpleName();
                groupTooltip = "Class: " + ((Class<?>) groupKey).getName();
            }
            else
            {
                // Anything else, we simply convert to a string.
                groupTooltip = groupKey.toString();
                groupLabel = StringUtils.abbreviate(groupTooltip, 80);
            }
            
            createEditorGroup(mainControl, groupLabel, groupTooltip, descriptor,
                descriptor.attributeGroups.get(groupKey));
        }

        /*
         * Add remaining attributes.
         */
        if (!descriptor.attributeDescriptors.isEmpty())
        {
            createEditorGroup(mainControl, "Ungrouped", "Other ungrouped attributes", descriptor,
                descriptor.attributeDescriptors);
        }
    }

    /**
     * Create an editor group associated with a group of attributes.
     */
    @SuppressWarnings("unchecked")
    private void createEditorGroup(final Composite parent, String groupName,
        String tooltip,
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
        group.setToolTipText(tooltip);
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

    /**
     * Dispose and unregister any editors currently held.
     */
    private void disposeEditors()
    {
        if (this.attributeEditors.isEmpty())
            return;

        /*
         * Unsubscribe from attribute editors (unique).
         */
        final HashSet<AttributeEditorList> values = Sets.newHashSet(this.attributeEditors.values());
        for (AttributeEditorList attEditor : values)
        {
            attEditor.removeAttributeChangeListener(forwardListener);
        }
        
        /*
         * Dispose controls.
         */
        for (Control c : this.mainControl.getChildren())
        {
            if (!c.isDisposed()) c.dispose();
        }

        this.attributeEditors.clear();
    }

    /*
     * 
     */
    @Override
    public void dispose()
    {
        disposeEditors();
        this.listeners.clear();

        super.dispose();
    }
}
