package org.carrot2.workbench.core.ui;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.AnnotationsPredicate;
import org.carrot2.util.attribute.AttributeDescriptor;
import org.carrot2.util.attribute.AttributeValueSet;
import org.carrot2.util.attribute.AttributeValueSets;
import org.carrot2.util.attribute.BindableDescriptor;
import org.carrot2.util.attribute.BindableDescriptorBuilder;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.BindableDescriptor.GroupingMethod;
import org.carrot2.util.attribute.constraint.ConstraintValidator;
import org.carrot2.workbench.core.ExtensionImpl;
import org.carrot2.workbench.core.ExtensionLoader;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.core.helpers.Utils;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.widgets.CScrolledComposite;
import org.carrot2.workbench.editors.AttributeChangedEvent;
import org.carrot2.workbench.editors.AttributeListenerAdapter;
import org.carrot2.workbench.editors.IAttributeEditor;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;


/**
 * The search view defines a combination of source, algorithm and required input
 * parameters required to open a new editor.
 */
public class SearchInputView extends ViewPart
{
    /**
     * Public identifier of this view.
     */
    public static final String ID = "org.carrot2.workbench.core.views.search";

    /**
     * A bit modified {@link StackLayout}. The size of the composite equals the size of
     * the visible control, not the biggest control available on the attributesPagesStack.
     */
    private static class VisibleComponentSizeStackLayout extends StackLayout
    {
        @Override
        protected Point computeSize(Composite composite, int wHint, int hHint,
            boolean flushCache)
        {
            if (topControl == null) return new Point(0, 0);

            return topControl.computeSize(wHint, hHint);
        }
    }

    /**
     * Memento branch for persisting algorithm component ID;
     */
    private static final String MEMENTO_ALGORITHM_ID = "algorithmId";

    /**
     * Memento branch for persisting source component ID;
     */
    private static final String MEMENTO_SOURCE_ID = "sourceId";

    /**
     * Memento branch for persisting {@link #attributes}.
     */
    private static final String MEMENTO_ATTRIBUTES = "attribute-set";
    
    /**
     * A serialized {@link AttributeValueSet} attribute inside memento.
     */
    private static final String MEMENTO_ATTRIBUTE_SET_SERIALIZED = "serialized";

    /**
     * State persistence.
     */
    private IMemento state;

    private ComboViewer sourceViewer;
    private ComboViewer algorithmViewer;

    /**
     * A composite with a {@link StackLayout}, holding {@link AttributeGroups}s for
     * all document sources. 
     */
    private VisibleComponentSizeStackLayout attributesPagesStack;
    /*
    private Composite editorStack;
    */

    /*
     * 
     */
    private Button processButton;

    /**
     * A map of {@link AttributeGroups} associated with all document source IDs from
     * {@link WorkbenchCorePlugin#getSources()}.
     */
    private Map<String, AttributeGroups> editors = Maps.newHashMap();

    /**
     * A map of {@link AttributeValueSet} associated with all document source IDs from
     * {@link WorkbenchCorePlugin#getSources()}.
     */
    private Map<String, AttributeValueSet> attributes = Maps.newHashMap();

    /**
     * A map of {@link BindableDescriptor} for each document source ID from {@link
     * WorkbenchCorePlugin#getSources()}.
     */
    private Map<String, BindableDescriptor> descriptors = Maps.newHashMap();

    /**
     * Scroller composite container.
     */
    private CScrolledComposite scroller;

    /**
     * Filter showing only required attributes.
     */
    @SuppressWarnings("unchecked")
    private final static Predicate<AttributeDescriptor> SHOW_REQUIRED = new AnnotationsPredicate(false, Required.class);

    /**
     * Filter showing all attributes.
     */
    private final static Predicate<AttributeDescriptor> SHOW_ALL = Predicates.alwaysTrue();
    
    /**
     * Toggles between {@link SearchInputView#SHOW_REQUIRED} and
     * {@link SearchInputView#SHOW_ALL}. 
     */
    private class ShowRequiredOnlyAction extends Action
    {
        public ShowRequiredOnlyAction()
        {
            super("Show only required attributes", SWT.TOGGLE);
        }

        @Override
        public void run()
        {
            final IPreferenceStore preferenceStore = 
                WorkbenchCorePlugin.getDefault().getPreferenceStore();

            final boolean state =  preferenceStore.getBoolean(
                PreferenceConstants.SHOW_REQUIRED_ONLY);

            preferenceStore.setValue(PreferenceConstants.SHOW_REQUIRED_ONLY, !state);
        }

        @Override
        public boolean isChecked()
        {
            return WorkbenchCorePlugin.getDefault().getPreferenceStore().getBoolean(
                PreferenceConstants.SHOW_REQUIRED_ONLY);
        }
    }

    /*
     * 
     */
    @Override
    public void init(IViewSite site) throws PartInitException
    {
        super.init(site);

        final IActionBars bars = site.getActionBars();
        createMenu(bars.getMenuManager());

        bars.updateActionBars();        
    }
    
    /*
     * 
     */
    private void createMenu(IMenuManager menuManager)
    {
        final IAction showRequiredOnly = new ShowRequiredOnlyAction();

        menuManager.add(showRequiredOnly);
        menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     * Create user interface for the view.
     */
    @Override
    public void createPartControl(Composite parent)
    {
        createComponents(parent);

        /*
         * Hook processing startup to the processing button and on focus-list (when all
         * attributes are given).
         */
        processButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                fireProcessing();
            }
        });

        parent.addTraverseListener(new TraverseListener()
        {
            public void keyTraversed(TraverseEvent e)
            {
                if (e.detail == SWT.TRAVERSE_RETURN)
                {
                    /*
                     * Consume the traversal event, spawn a new editor.
                     */
                    e.doit = false;
                    e.detail = SWT.TRAVERSE_NONE;

                    fireProcessing();
                }
            }
        });
        
        /*
         * Hook global preference updates.
         */
        final IPreferenceStore preferenceStore = 
            WorkbenchCorePlugin.getDefault().getPreferenceStore();
        preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event)
            {
                if (PreferenceConstants.SHOW_REQUIRED_ONLY.equals(
                    event.getProperty()))
                {
                    updateRequiredFilterState();
                }
            }
        });
    }

    /**
     * Update attribute panels with current filtering settings.
     */
    private void updateRequiredFilterState()
    {
        final IPreferenceStore preferenceStore = 
            WorkbenchCorePlugin.getDefault().getPreferenceStore();

        final Predicate<AttributeDescriptor> filter;
        if (preferenceStore.getBoolean(PreferenceConstants.SHOW_REQUIRED_ONLY))
        {
            filter = SHOW_REQUIRED;
        }
        else
        {
            filter = SHOW_ALL;
        }

        for (AttributeGroups i : editors.values())
        {
            i.setFilter(filter);
        }

        /*
         * Update current attribute values. 
         */
        for (String sourceID : editors.keySet())
        {
            final AttributeValueSet sourceAttrs = attributes.get(sourceID);
            final AttributeGroups editor = editors.get(sourceID);

            for (Map.Entry<String, Object> entry : sourceAttrs.getAttributeValues().entrySet())
            {
                editor.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        scroller.reflow(true);
    }

    /**
     * Creates permanent GUI elements (source, algorithm combos).
     */
    private void createComponents(Composite parent)
    {
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
        parent.setLayout(new FillLayout());

        this.scroller = new CScrolledComposite(parent, 
            SWT.H_SCROLL | SWT.V_SCROLL);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(false);

        Composite innerComposite = GUIFactory.createSpacer(scroller);
        final GridLayout gridLayout = (GridLayout) innerComposite.getLayout();
        gridLayout.numColumns = 2;
        gridLayout.makeColumnsEqualWidth = false;

        scroller.setContent(innerComposite);

        sourceViewer = createComboViewer(innerComposite, "Source", core.getSources());
        sourceViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            public void selectionChanged(SelectionChangedEvent event)
            {
                final IStructuredSelection selection = (IStructuredSelection) event
                    .getSelection();
                final ExtensionImpl impl = (ExtensionImpl) selection.getFirstElement();
                if (impl != null)
                {
                    attributesPagesStack.topControl = editors.get(impl.id);
                    checkAllRequiredAttributes();
                    scroller.reflow(true);
                }
            }
        });

        algorithmViewer = createComboViewer(innerComposite, "Algorithm", core.getAlgorithms());

        createRequiredAttributesLayout(innerComposite);

        final GridData processButtonGridData = new GridData();
        processButtonGridData.horizontalAlignment = GridData.END;
        processButtonGridData.verticalAlignment = GridData.END;
        processButtonGridData.horizontalSpan = 2;

        processButton = new Button(innerComposite, SWT.PUSH);
        processButton.setText("Process");
        processButton.setLayoutData(processButtonGridData);

        /*
         * Restore state and push initial values to editors.
         */
        restoreState();
        updateRequiredFilterState();

        /*
         * Hook up listeners updating attributes on changes in editors. 
         */
        for (String sourceID : editors.keySet())
        {
            final AttributeValueSet sourceAttrs = attributes.get(sourceID);
            final AttributeGroups editor = editors.get(sourceID);

            editor.addAttributeChangeListener(new AttributeListenerAdapter() {
                public void attributeChange(AttributeChangedEvent event)
                {
                    sourceAttrs.setAttributeValue(event.key, event.value);
                    checkAllRequiredAttributes();
                }

                public void contentChanging(IAttributeEditor editor, Object value)
                {
                    /*
                     * On content changing, temporarily substitute the value of the
                     * given attribute with the new value.
                     */
                    final String attributeKey = editor.getAttributeKey();
                    final AttributeValueSet attributeSet = attributes.get(getSourceId());

                    final Object currentValue = attributeSet.getAttributeValue(attributeKey);
                    attributeSet.setAttributeValue(attributeKey, value);
                    checkAllRequiredAttributes();
                    attributeSet.setAttributeValue(attributeKey, currentValue);
                }
            });
        }
    }

    /**
     * creates a JFace ComboViewer around a collection of extension point implementations.
     * Restores saved selection state if possible.
     */
    private ComboViewer createComboViewer(Composite parent, String comboLabel, ExtensionLoader loader)
    {
        final Label label = new Label(parent, SWT.CENTER);
        label.setLayoutData(new GridData());
        label.setText(comboLabel);

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;

        final Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY
            | SWT.BORDER);
        combo.setLayoutData(gridData);
        combo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        final ComboViewer viewer = new ComboViewer(combo);
        viewer.setLabelProvider(new LabelProvider()
        {
            public String getText(Object element)
            {
                return ((ExtensionImpl) element).label;
            }
        });
        viewer.setComparator(new ViewerComparator(new Comparator<String>()
        {
            public int compare(String s1, String s2)
            {
                return s1.toLowerCase().compareTo(s2.toLowerCase());
            }
        }));

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(loader.getImplementations());

        return viewer;
    }

    /**
     * Initiate query processing. Open an editor with the current parameter values.
     */
    private void fireProcessing()
    {
        if (!hasAllRequiredAttributes(getSourceId()))
        {
            return;
        }

        final IWorkbenchPage page = getViewSite().getWorkbenchWindow().getActivePage();

        /*
         * Clone current attribute values so that they can be freely 
         * modified in the editor and in the input view.
         */
        final AttributeValueSet current = attributes.get(getSourceId());
        final AttributeValueSet cloned = new AttributeValueSet("request");
        cloned.setAttributeValues(current.getAttributeValues());

        final SearchInput input = new SearchInput(getSourceId(), getAlgorithmId(), cloned);
        try
        {
            page.openEditor(input, SearchEditor.ID);
        }
        catch (Exception x)
        {
            final IStatus status = new OperationStatus(IStatus.ERROR,
                WorkbenchCorePlugin.PLUGIN_ID, -2, "Editor could not be opened.", x);
            Utils.showError(status);
        }
    }

    /**
     * Check if all required {@link Input} attributes of a given source are properly initialized.
     */
    private boolean hasAllRequiredAttributes(String sourceId)
    {
        final AttributeValueSet values = attributes.get(sourceId);
        
        final Collection<AttributeDescriptor> desc = descriptors.get(sourceId)
            .flatten().attributeDescriptors.values();

        for (AttributeDescriptor d : desc)
        {
            final Object value = values.getAttributeValue(d.key);

            if (!isValid(d, value))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if all required attributes for the current configuration are available and
     * update the {@link #processButton}.
     */
    private void checkAllRequiredAttributes()
    {
        processButton.setEnabled(hasAllRequiredAttributes(getSourceId()));
    }
    
    /**
     * Returns <code>true</code> if the value described by a given attribute descriptor
     * is valid (non-<code>null</code> for {@link Required} attributes and fulfilling
     * all other constraints.
     */
    private boolean isValid(AttributeDescriptor d, Object value)
    {
        if (d.requiredAttribute && value == null)
        {
            return false;
        }

        if (value == null)
        {
            value = d.defaultValue;
        }

        final Annotation [] constraints = d.constraints.toArray(
            new Annotation [d.constraints.size()]);

        return ConstraintValidator.isMet(value, constraints).length == 0;
    }

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    private void createRequiredAttributesLayout(Composite innerComposite)
    {
        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();

        attributesPagesStack = new VisibleComponentSizeStackLayout();
        final Composite editorStack = new Composite(innerComposite, SWT.NONE);
        editorStack.setLayout(attributesPagesStack);

        final GridData attributesGridData = new GridData();
        attributesGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        attributesGridData.grabExcessHorizontalSpace = true;
        attributesGridData.horizontalSpan = 2;
        editorStack.setLayoutData(attributesGridData);

        for (ExtensionImpl implementation : core.getSources().getImplementations())
        {
            final ProcessingComponent source = implementation.getInstance();

            final String sourceID = implementation.id;
            if (!attributes.containsKey(sourceID))
            {
                this.attributes.put(sourceID, new AttributeValueSet(sourceID));
            }

            final BindableDescriptor descriptor = BindableDescriptorBuilder
                .buildDescriptor(source).only(
                    Input.class, Processing.class);
            
            descriptors.put(sourceID, descriptor);

            final AttributeGroups page = new AttributeGroups(
                editorStack, descriptor, GroupingMethod.NONE, SHOW_REQUIRED);

            page.setBackground(
                PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE));

            this.editors.put(sourceID, page);
        }
    }

    /**
     * Restore state of UI components from memento.
     */
    @SuppressWarnings("unchecked")
    private void restoreState()
    {
        /*
         * Restore attribute sets for inputs.
         */
        if (state != null)
        {
            final IMemento [] mementos = state.getChildren(MEMENTO_ATTRIBUTES);
            if (mementos != null)
            {
                for (int i = 0; i < mementos.length; i++)
                {
                    final IMemento single = mementos[i];

                    final String id = single.getID();
                    final String serialized = single.getString(MEMENTO_ATTRIBUTE_SET_SERIALIZED);

                    if (id == null || serialized == null)
                    {
                        continue;
                    }

                    try
                    {
                        final AttributeValueSets sets = AttributeValueSets.deserialize(
                            new StringReader(serialized));
                        final AttributeValueSet set = sets.getDefaultAttributeValueSet();
                        if (set != null)
                        {
                            this.attributes.put(id, set);
                        }
                    }
                    catch (Exception e)
                    {
                        Utils.logError(e, false);
                    }
                }
            }
        }

        /*
         * Combo boxes state.
         */
        try
        {
            final Object [][] combos = new Object [] []
            {
                {
                    sourceViewer, MEMENTO_SOURCE_ID
                },
                {
                    algorithmViewer, MEMENTO_ALGORITHM_ID
                }
            };

            for (Object [] comboPair : combos)
            {
                ComboViewer combo = (ComboViewer) comboPair[0];
                String mementoAttr = (String) comboPair[1];

                Collection<ExtensionImpl> options = (Collection<ExtensionImpl>) combo
                    .getInput();

                /*
                 * Attempt to restore selection from memento, if not available,
                 * set the default option.
                 */
                boolean restored = false;
                if (state != null)
                {
                    String id = state.getString(mementoAttr);
                    if (id != null)
                    {
                        for (ExtensionImpl i : options)
                        {
                            if (i.id.equals(id))
                            {
                                combo.setSelection(new StructuredSelection(i), true);
                                restored = true;
                            }
                        }
                    }
                }

                if (!restored && combo.getCombo().getItemCount() > 0)
                {
                    final Object element = options.iterator().next();
                    combo.setSelection(new StructuredSelection(element));
                }
            }
        }
        catch (RuntimeException re)
        {
            Utils.logError("Could not restore search view state", re, false);
        }

        /*
         * Disable GUI if no inputs or algorithms.
         */

        final WorkbenchCorePlugin core = WorkbenchCorePlugin.getDefault();
        if (core.getSources().getImplementations().isEmpty())
        {
            disableComboWithMessage(sourceViewer.getCombo(), "No document sources found.");
            processButton.setEnabled(false);
        }

        if (core.getAlgorithms().getImplementations().isEmpty())
        {
            disableComboWithMessage(algorithmViewer.getCombo(),
                "No clustering algorithms found.");
            processButton.setEnabled(false);
        }
    }

    /**
     * 
     */
    private void disableComboWithMessage(Combo toDisable, String message)
    {
        toDisable.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        toDisable.setToolTipText(message);
        toDisable.setEnabled(false);
    }

    /**
     * 
     */
    private String getSourceId()
    {
        return getSelectedId(sourceViewer);
    }

    /**
     * 
     */
    private String getAlgorithmId()
    {
        return getSelectedId(algorithmViewer);
    }

    /**
     * 
     */
    private String getSelectedId(ComboViewer combo)
    {
        if (combo.getSelection().isEmpty())
        {
            return null;
        }

        final IStructuredSelection selection = ((IStructuredSelection) combo
            .getSelection());
        return ((ExtensionImpl) selection.getFirstElement()).id;
    }

    /*
     * 
     */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        state = memento;
        super.init(site, memento);
    }

    /*
     * 
     */
    @Override
    public void saveState(IMemento memento)
    {
        memento.putString(MEMENTO_SOURCE_ID, getSourceId());
        memento.putString(MEMENTO_ALGORITHM_ID, getAlgorithmId());

        for (Map.Entry<String, AttributeValueSet> entry : attributes.entrySet())
        {
            final String sourceID = entry.getKey();
            final AttributeValueSet set = entry.getValue();

            try
            {
                final StringWriter w = new StringWriter();
                final AttributeValueSets sets = new AttributeValueSets();
                sets.addAttributeValueSet(sourceID, set);
                sets.serialize(w);
                w.close();

                final String serialized = w.toString();

                final IMemento single = memento.createChild(MEMENTO_ATTRIBUTES, sourceID);
                single.putString(MEMENTO_ATTRIBUTE_SET_SERIALIZED, serialized);
            }
            catch (Exception e)
            {
                Utils.logError(e, false);
            }
        }
    }

    @Override
    public void setFocus()
    {
        this.sourceViewer.getCombo().setFocus();
    }

    @Override
    public void dispose()
    {
        for (Widget w : editors.values())
        {
            w.dispose();
        }

        super.dispose();
    }
}
