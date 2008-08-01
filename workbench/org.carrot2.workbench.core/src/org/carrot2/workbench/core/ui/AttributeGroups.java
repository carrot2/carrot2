package org.carrot2.workbench.core.ui;

import static org.eclipse.swt.SWT.NONE;

import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.attribute.Internal;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.editors.*;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * An SWT composite capable of displaying groups of {@link IAttributeEditor}s, sorted by
 * {@link GroupingMethod} and filtered using {@link #setFilter(Predicate)}.
 */
public final class AttributeGroups extends Composite implements
    IAttributeChangeProvider
{
    /**
     * Padding between a given group (when it is expanded) and the following group.
     */
    public static final int SPACE_TO_NEXT_GROUP = 10;
    
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

        public void contentChanging(IAttributeEditor editor, Object value)
        {
            for (IAttributeListener listener : listeners)
            {
                listener.contentChanging(editor, value);
            }
        }
    };

    /**
     * Descriptors of attribute editors to be created.
     */
    private BindableDescriptor descriptor;

    /**
     * A hashmap of attribute IDs to {@link AttributeList} that contain them.
     * 
     * @see #setAttribute(String, Object)
     */
    private Map<String, AttributeList> attributeEditors = Maps.newHashMap();

    /**
     * Predicate used to filter attribute descriptors shown in the editor or
     * <code>null</code>.
     */
    private Predicate<AttributeDescriptor> filterPredicate;

    /**
     * Builds the component for a given {@link BindableDescriptor}.
     */
    public AttributeGroups(Composite parent, BindableDescriptor descriptor,
        GroupingMethod grouping, Predicate<AttributeDescriptor> filter)
    {
        super(parent, SWT.NONE);

        this.descriptor = descriptor;
        this.grouping = grouping;
        this.filterPredicate = filter;
        createComponents();

        refreshUI();
    }

    public AttributeGroups(Composite parent, BindableDescriptor descriptor,
        GroupingMethod grouping)
    {
        this(parent, descriptor, grouping, null);
    }

    /**
     * Create GUI components (sections, editors).
     */
    private void createComponents()
    {
        final GridLayout layout = GUIFactory.zeroMarginGridLayout();
        this.setLayout(layout);

        final Composite mainControl = new Composite(this, NONE);
        mainControl.setLayout(GUIFactory.zeroMarginGridLayout());
        mainControl.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
            .create());
        this.mainControl = mainControl;
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
        refreshUI();
    }

    /**
     * Reset attribute filter.
     */
    public void setFilter(Predicate<AttributeDescriptor> filter)
    {
        if (this.filterPredicate == filter)
        {
            return;
        }

        this.filterPredicate = filter;
        refreshUI();
    }

    /**
     * Refresh user interface after a change to the displayed editors. This may take a
     * longer time.
     */
    private void refreshUI()
    {
        this.mainControl.setRedraw(false);
        disposeEditors();
        createEditors(mainControl);
        this.mainControl.setRedraw(true);

        forceReflow();
    }

    /**
     * Force reflow of parent {@link SharedScrolledComposite}s, if there are any.
     */
    private void forceReflow()
    {
        Control c = this;
        while (c != null)
        {
            if (c instanceof SharedScrolledComposite)
            {
                ((SharedScrolledComposite) c).reflow(true);
                break;
            }

            c = c.getParent();
        }
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
    @SuppressWarnings("unchecked")
    private void createEditors(Composite parent)
    {
        /*
         * Create a filtered view of attribute descriptors.
         */
        BindableDescriptor descriptor = this.descriptor.not(Internal.class);
        if (filterPredicate != null)
        {
            descriptor = descriptor.only(filterPredicate);
        }
        descriptor = descriptor.group(grouping);

        /*
         * Create sections for attribute groups.
         */
        for (Object groupKey : descriptor.attributeGroups.keySet())
        {
            final String groupLabel;
            if (groupKey instanceof Class)
            {
                groupLabel = ((Class<?>) groupKey).getSimpleName();
            }
            else
            {
                // Anything else, we simply convert to a string.
                groupLabel = StringUtils.abbreviate(groupKey.toString(), 160);
            }

            createEditorGroup(mainControl, groupLabel, descriptor,
                descriptor.attributeGroups.get(groupKey));
        }

        /*
         * If we have a NONE grouping, then skip creating section headers.
         */
        if (grouping == GroupingMethod.NONE)
        {
            if (descriptor.attributeGroups.size() > 0)
            {
                Utils.logError("There should be no groups if grouping is NONE.", false);
            }

            if (!descriptor.attributeDescriptors.isEmpty())
            {
                createUntitledEditorGroup(mainControl, descriptor,
                    descriptor.attributeDescriptors);
            }
        }
        else
        {
            /*
             * Otherwise, add remaining attributes under a synthetic group.
             */
            if (!descriptor.attributeDescriptors.isEmpty())
            {
                createEditorGroup(mainControl, "Ungrouped", 
                    descriptor, descriptor.attributeDescriptors);
            }
        }
    }

    /**
     * Create an unnamed (no section title, no twistie) editor group associated with a
     * group of attributes.
     */
    private void createUntitledEditorGroup(final Composite parent,
        BindableDescriptor descriptor, Map<String, AttributeDescriptor> attributes)
    {
        final GridLayout layout = GUIFactory.zeroMarginGridLayout();
        layout.numColumns = 1;

        final Composite inner = new Composite(parent, SWT.NONE);
        inner.setLayout(layout);

        final GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = false;
        inner.setLayoutData(gd);

        final AttributeList editorList = new AttributeList(inner, attributes,
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
    }

    /**
     * Create an editor group associated with a group of attributes.
     */
    @SuppressWarnings("unchecked")
    private void createEditorGroup(final Composite parent, String groupName, 
        BindableDescriptor descriptor, Map<String, AttributeDescriptor> attributes)
    {
        final int style = ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT;
        final Section group = new Section(parent, style);
        group.setText(groupName);
        group.setExpanded(true);
        group.setSeparatorControl(new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL));

        final GridLayout layout = GUIFactory.zeroMarginGridLayout();
        layout.numColumns = 1;

        // Vertical space between groups of attributes
        layout.marginBottom = SPACE_TO_NEXT_GROUP;
        layout.marginTop = AttributeList.SPACE_BEFORE_LABEL;

        /*
         * Prepare editors inside the group widget. Add some extra space at the bottom of
         * the control to separate from the next group
         */
        final Composite inner = new Composite(group, SWT.NONE);
        inner.setLayout(layout);

        final AttributeList editorList = new AttributeList(inner, attributes,
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
                forceReflow();
            }
        });
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
        final AttributeList attributeEditorList = this.attributeEditors.get(key);
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
        if (this.attributeEditors.isEmpty()) return;

        /*
         * Unsubscribe from attribute editors (unique).
         */
        final HashSet<AttributeList> values = Sets.newHashSet(this.attributeEditors
            .values());
        for (AttributeList attEditor : values)
        {
            attEditor.removeAttributeChangeListener(forwardListener);
        }

        /*
         * Dispose controls.
         */
        if (!mainControl.isDisposed())
        {
            for (Control c : this.mainControl.getChildren())
            {
                if (!c.isDisposed()) c.dispose();
            }
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
