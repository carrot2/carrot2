
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

import static org.eclipse.swt.SWT.NONE;

import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.IProcessingComponent;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.editors.*;
import org.carrot2.workbench.editors.factory.EditorFactory;
import org.carrot2.workbench.editors.factory.EditorNotFoundException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.*;

import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.base.Predicates;
import org.carrot2.shaded.guava.common.collect.Maps;
import org.carrot2.shaded.guava.common.collect.Sets;

/**
 * An SWT composite capable of displaying groups of {@link IAttributeEditor}s, sorted by
 * {@link GroupingMethod} and filtered using {@link #setFilter(Predicate)}.
 * <p>
 * This component does not store its own state, you should save and restore the state from
 * within parent components.
 */
public final class AttributeGroups extends Composite implements IAttributeEventProvider
{
    /**
     * Padding between a given group (when it is expanded) and the following group.
     */
    public static final int SPACE_TO_NEXT_GROUP = 10;

    /**
     * Section name for ungrouped attributes.
     */
    private static final String UNGROUPED_ATTRIBUTES_GROUP = "Ungrouped";

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
    private final IAttributeListener forwardListener = new ForwardingAttributeListener(
        listeners);

    /**
     * Update {@link #currentValues}.
     */
    private final IAttributeListener updateListener = new AttributeListenerAdapter()
    {
        public void valueChanged(AttributeEvent event)
        {
            currentValues.put(event.key, event.value);
        }

        @Override
        public void valueChanging(AttributeEvent event)
        {
            valueChanged(event);
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
    private Map<String, AttributeList> attributeEditors = Maps.newLinkedHashMap();

    /**
     * Predicate used to filter attribute descriptors shown in the editor or
     * <code>null</code>.
     */
    private Predicate<AttributeDescriptor> filterPredicate;

    /**
     * A copy of the initial default values, kept in sync with changes reported by the
     * editors.
     */
    private HashMap<String, Object> currentValues;

    /**
     * {@link Section} objects for attribute groups, if any.
     */
    private Map<String, Section> attributeGroupSections = Maps.newHashMap();

    /**
     * Expansion state for sections (regardless if they are shown or not).
     */
    private Map<String, Boolean> attributeGroupExpansionState = Maps.newHashMap();

    /**
     * A predicate that filters out all descriptors without an editor.
     */
    private class HasEditorPredicate implements Predicate<AttributeDescriptor>
    {
        public boolean apply(AttributeDescriptor ad)
        {
            try
            {
                Class<?> clazz = AttributeGroups.this.descriptor.type;
                if (clazz != null && IProcessingComponent.class.isAssignableFrom(clazz))
                {
                    EditorFactory.getEditorFor(clazz
                        .asSubclass(IProcessingComponent.class), ad);
                }
                else
                {
                    EditorFactory.getEditorFor(null, ad);
                }

                return true;
            }
            catch (EditorNotFoundException e)
            {
                return false;
            }
        }
    }

    /**
     * Builds the component for a given {@link BindableDescriptor}.
     */
    public AttributeGroups(Composite parent, BindableDescriptor descriptor,
        GroupingMethod grouping, Predicate<AttributeDescriptor> filter,
        Map<String, Object> defaultValues)
    {
        super(parent, SWT.NONE);

        this.descriptor = descriptor;
        this.grouping = grouping;
        this.filterPredicate = filter;
        createComponents();

        this.currentValues = Maps.newHashMap(defaultValues);
        refreshUI();
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
    private void createEditors(Composite parent)
    {
        /*
         * Create a filtered view of attribute descriptors.
         */
        BindableDescriptor descriptor = this.descriptor.only(
            Predicates.not(new InternalAttributePredicate(false))).only(
            new HasEditorPredicate());
        if (filterPredicate != null)
        {
            descriptor = descriptor.only(filterPredicate);
        }

        /*
         * CARROT-818: Check if there is anything for display. 
         */
        if (descriptor.attributeDescriptors.isEmpty())
        {
            createMessage(mainControl, this, "No attributes.");
        }

        /*
         * Group attributes.
         */
        descriptor = descriptor.group(grouping);
        
        /*
         * Create sections for attribute groups.
         */
        for (Object groupKey : descriptor.attributeGroups.keySet())
        {
            final String groupLabel;
            if (groupKey instanceof Class<?>)
            {
                groupLabel = ((Class<?>) groupKey).getSimpleName();
            }
            else
            {
                // Anything else, we simply convert to a string.
                groupLabel = StringUtils.abbreviate(groupKey.toString(), 160);
            }

            createEditorGroup(mainControl, groupKey.toString(), groupLabel, descriptor,
                descriptor.attributeGroups.get(groupKey), this);
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
                    descriptor.attributeDescriptors, this);
            }
        }
        else
        {
            /*
             * Otherwise, add remaining attributes under a synthetic group.
             */
            if (!descriptor.attributeDescriptors.isEmpty())
            {
                createEditorGroup(mainControl, UNGROUPED_ATTRIBUTES_GROUP,
                    UNGROUPED_ATTRIBUTES_GROUP, descriptor,
                    descriptor.attributeDescriptors, this);
            }
        }
    }

    /**
     * Creates a message control showing a given message.
     */
    private void createMessage(Composite parent, AttributeGroups attributeGroups, String message)
    {
        final GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = false;

        final CLabel label = new CLabel(parent, SWT.NONE);
        label.setLayoutData(gd);

        label.setText(message);
        label.setAlignment(SWT.LEFT);
    }

    /**
     * Create an unnamed (no section title, no twistie) editor group associated with a
     * group of attributes.
     */
    private void createUntitledEditorGroup(final Composite parent,
        BindableDescriptor descriptor, Map<String, AttributeDescriptor> attributes,
        IAttributeEventProvider globalEventsProvider)
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

        final AttributeList editorList = new AttributeList(inner, descriptor, attributes,
            globalEventsProvider, currentValues);

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
        editorList.addAttributeListener(forwardListener);
        editorList.addAttributeListener(updateListener);
    }

    /**
     * Create an editor group associated with a group of attributes.
     */
    private void createEditorGroup(final Composite parent, final String groupKey,
        String groupName, BindableDescriptor descriptor,
        Map<String, AttributeDescriptor> attributes,
        IAttributeEventProvider globalEventsProvider)
    {
        final int style = ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT;
        final Section group = new Section(parent, style);
        group.setText(groupName);
        group.setExpanded(getExpansionState(groupKey));
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

        final AttributeList editorList = new AttributeList(inner, descriptor, attributes,
            globalEventsProvider, currentValues);

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
        editorList.addAttributeListener(forwardListener);
        editorList.addAttributeListener(updateListener);

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
                attributeGroupExpansionState.put(groupKey, group.isExpanded());
            }
        });

        // Store the history of sections to remember folding state.
        attributeGroupSections.put(groupKey, group);
    }

    /**
     * Returns expansion state for a given group.
     */
    private boolean getExpansionState(String groupKey)
    {
        if (!attributeGroupExpansionState.containsKey(groupKey))
        {
            attributeGroupExpansionState.put(groupKey, true);
        }
        return attributeGroupExpansionState.containsKey(groupKey);
    }

    /**
     * 
     */
    public void addAttributeListener(IAttributeListener listener)
    {
        this.listeners.add(listener);
    }

    /**
     * 
     */
    public void removeAttributeListener(IAttributeListener listener)
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
        else
        {
            forwardListener.valueChanged(new AttributeEvent(this, key, value));
        }
    }

    /**
     * 
     */
    public void setAttributes(Map<String, Object> attributeValues)
    {
        for (Map.Entry<String, Object> e : attributeValues.entrySet())
        {
            setAttribute(e.getKey(), e.getValue());
        }
    }

    /**
     * Dispose and unregister any editors currently held.
     */
    private void disposeEditors()
    {
        /*
         * Unsubscribe from attribute editors (unique).
         */
        final HashSet<AttributeList> values = Sets.newHashSet(attributeEditors.values());
        for (AttributeList attEditor : values)
        {
            attEditor.removeAttributeListener(forwardListener);
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
        this.attributeGroupSections.clear();
    }

    /**
     * Set the focus to the {@link AttributeList} containing {@link AttributeNames#QUERY},
     * if possible.
     */
    @Override
    public boolean setFocus()
    {
        if (this.attributeEditors.containsKey(AttributeNames.QUERY))
        {
            return attributeEditors.get(AttributeNames.QUERY).setFocus();
        }

        return false;
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

    /**
     * Expand or collapse all currently shown attribute groups.
     * 
     * @see Section#setExpanded(boolean)
     */
    public void setExpanded(boolean expanded)
    {
        assert Display.getCurrent() != null;
        for (String key : attributeGroupSections.keySet())
        {
            setExpanded(key, expanded);
        }
    }

    /**
     * Expand or collapse a given attribute group section.
     */
    public void setExpanded(String groupKey, boolean expanded)
    {
        assert Display.getCurrent() != null;
        this.attributeGroupExpansionState.put(groupKey, expanded);
        final Section section = this.attributeGroupSections.get(groupKey);
        if (section != null)
        {
            section.setExpanded(expanded);
        }
    }

    /**
     * Returns a snapshot of expansion states of this component's sections.
     */
    public Map<String, Boolean> getExpansionStates()
    {
        assert Display.getCurrent() != null;
        return Maps.newHashMap(this.attributeGroupExpansionState);
    }

    /**
     * Calls {@link #setExpanded(String, boolean)} for all entries.
     */
    public void setExpanded(Map<String, Boolean> map)
    {
        assert Display.getCurrent() != null;
        for (Map.Entry<String, Boolean> e : map.entrySet())
        {
            setExpanded(e.getKey(), e.getValue());
        }
    }
}
