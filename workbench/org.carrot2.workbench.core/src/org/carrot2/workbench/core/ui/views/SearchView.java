package org.carrot2.workbench.core.ui.views;

import java.util.HashMap;
import java.util.Map;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.carrot2.workbench.core.helpers.*;
import org.carrot2.workbench.core.ui.ResultsEditor;
import org.carrot2.workbench.core.ui.SearchParameters;
import org.carrot2.workbench.core.ui.attributes.AttributesPage;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;

/**
 * The search view defines a combination of source, algorithm and required input
 * parameters required to open a new editor.
 */
public class SearchView extends ViewPart
{
    /**
     * Public identifier of this view.
     */
    public static final String ID = "org.carrot2.workbench.core.views.search";

    /**
     * A bit modified {@link StackLayout}. The size of the composite equals the size of
     * the visible control, not the biggest control available in the stack.
     */
    private static class VisibleComponentSizeStackLayout extends StackLayout
    {
        @Override
        protected Point computeSize(Composite composite, int wHint, int hHint,
            boolean flushCache)
        {
            return topControl.computeSize(wHint, hHint);
        }
    }

    /**
     * Currently selected algorithm (state persistence).
     */
    private static final String ALGORITHM_ID_ATTRIBUTE = "algorithmId";

    /**
     * Currently selected source (state persistence).
     */
    private static final String SOURCE_ID_ATTRIBUTE = "sourceId";

    /**
     * State persistence.
     */
    private IMemento state;

    private ComboViewer sourceViewer;
    private ComboViewer algorithmViewer;
    private Composite innerComposite;

    private Button processButton;

    /**
     * A map of {@link AttributesPage} associated with all document sources.
     */
    private Map<String, AttributesPage> attributesPages = new HashMap<String, AttributesPage>();

    /**
     * 
     */
    private class ComponentLabelProvider extends LabelProvider
    {
        @Override
        public String getText(Object element)
        {
            return ((ComponentWrapper) element).getCaption();
        }
    }

    /*
     * 
     */
    @Override
    public void createPartControl(Composite parent)
    {
        createPermanentLayout(parent);
        checkProcessingConditions(parent);

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
                    fireProcessing();
                }
            }
        });
    }

    /**
     * Initiate query processing. Open an editor with the current parameter values.
     */
    private void fireProcessing()
    {
        // Initiate processing in a new editor, opened on the currently active page.
        final IWorkbenchPage page = SearchView.this.getViewSite().getWorkbenchWindow()
            .getActivePage();
        final SearchParameters input = new SearchParameters(getSourceId(),
            getAlgorithmId(), null);
        input.putAllAttributes(attributesPages.get(getSourceId()).getAttributeValues());

        try
        {
            page.openEditor(input, ResultsEditor.ID);
        }
        catch (Exception x)
        {
            final IStatus status = new OperationStatus(IStatus.ERROR,
                WorkbenchCorePlugin.PLUGIN_ID, -2, "Editor could not be opened.", x);
            Utils.showError(status);
        }
    }

    @SuppressWarnings("unchecked")
    private void createRequiredAttributesLayout()
    {
        final StackLayout stack = new VisibleComponentSizeStackLayout();
        final Composite requiredAttributes = new Composite(innerComposite, SWT.NONE);
        requiredAttributes.setLayout(stack);

        final GridData attributesGridData = new GridData();
        attributesGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        attributesGridData.grabExcessHorizontalSpace = true;
        attributesGridData.horizontalSpan = 2;
        requiredAttributes.setLayoutData(attributesGridData);

        for (ComponentWrapper wrapper : ComponentLoader.SOURCE_LOADER.getComponents())
        {
            ProcessingComponent source = wrapper.getExecutableComponent();
            AttributesPage page = new AttributesPage(
                source.getClass(),
                BindableDescriptorBuilder.buildDescriptor(source).only(Input.class,
                    Processing.class, Required.class).not(Internal.class).flatten().attributeDescriptors);
            page.createControl(requiredAttributes);
            attributesPages.put(wrapper.getId(), page);
        }
        restoreRequiredAttributesState();

        if (getSourceId() != null)
        {
            stack.topControl = attributesPages.get(getSourceId()).getControl();
            requiredAttributes.layout();
        }
        sourceViewer.getCombo().addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (getSourceId() != null)
                {
                    stack.topControl = attributesPages.get(getSourceId()).getControl();
                    requiredAttributes.layout();
                    innerComposite.layout();
                }
            }
        });
    }

    private void restoreRequiredAttributesState()
    {
        if (state != null)
        {
            IMemento allPagesState = state.getChild("requiredAttributesPages");
            if (allPagesState != null)
            {
                IMemento [] pageStatesArray = allPagesState
                    .getChildren("requiredAttributesPage");
                for (int i = 0; i < pageStatesArray.length; i++)
                {
                    IMemento pageState = pageStatesArray[i];
                    String id = pageState.getID();
                    if (id != null)
                    {
                        attributesPages.get(id).restoreState(pageState);
                    }
                }
            }
        }
    }

    /**
     * Disables query textbox and process button if there is no document source or
     * clustering algorithm.
     * 
     * @param parent
     */
    private void checkProcessingConditions(Composite parent)
    {
        if (ComponentLoader.SOURCE_LOADER.getComponents().isEmpty()
            || ComponentLoader.ALGORITHM_LOADER.getComponents().isEmpty())
        {
            processButton.setEnabled(false);
        }
        if (ComponentLoader.SOURCE_LOADER.getComponents().isEmpty())
        {
            disableComboWithMessage(sourceViewer.getCombo(), "No document sources found.");
        }
        if (ComponentLoader.ALGORITHM_LOADER.getComponents().isEmpty())
        {
            disableComboWithMessage(algorithmViewer.getCombo(),
                "No clustering algorithms found.");
        }
    }

    /**
     * 
     */
    private void disableComboWithMessage(Combo toDisable, String message)
    {
        toDisable.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        toDisable.setItems(new String []
        {
            message
        });
        toDisable.select(0);
        toDisable.setEnabled(false);
    }

    /**
     * 
     */
    private String getSourceId()
    {
        if (sourceViewer.getSelection().isEmpty())
        {
            return null;
        }
        return ((ComponentWrapper) ((IStructuredSelection) sourceViewer.getSelection())
            .getFirstElement()).getId();
    }

    /**
     * 
     */
    private String getAlgorithmId()
    {
        if (algorithmViewer.getSelection().isEmpty())
        {
            return null;
        }
        return ((ComponentWrapper) ((IStructuredSelection) algorithmViewer.getSelection())
            .getFirstElement()).getId();
    }

    /**
     * Wraps component combobox (source or algorithm) with a JFace viewer. Restores saved
     * state if possible.
     * 
     * @param combo combo control used to create a viewer (
     *            {@link ComboViewer#ComboViewer(Combo)})
     * @param loader loader of components (sources or algorithms)
     * @param stateAttribute name of the attribute, which stores id of the component, that
     *            should be chosen by default
     * @return create wrapper
     */
    private ComboViewer createComboBoxWrapper(Combo combo, ComponentLoader loader,
        String stateAttribute)
    {
        combo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        final ComboViewer viewer = new ComboViewer(combo);
        viewer.setLabelProvider(new ComponentLabelProvider());
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(loader.getComponents());
        combo.select(0);
        try
        {
            if (state != null)
            {
                String id = state.getString(stateAttribute);
                if (id != null)
                {
                    ComponentWrapper wrapper = loader.getComponent(id);
                    IStructuredSelection sel = new StructuredSelection(wrapper);
                    viewer.setSelection(sel);
                }
            }
        }
        catch (RuntimeException re)
        {
            Utils.logError("Could not restore search view state", re, false);
        }
        return viewer;
    }

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException
    {
        state = memento;
        super.init(site, memento);
    }

    @Override
    public void saveState(IMemento memento)
    {
        memento.putString(SOURCE_ID_ATTRIBUTE, getSourceId());
        memento.putString(ALGORITHM_ID_ATTRIBUTE, getAlgorithmId());
        IMemento pagesState = memento.createChild("requiredAttributesPages");
        for (String sourceKey : attributesPages.keySet())
        {
            IMemento pageState = pagesState.createChild("requiredAttributesPage",
                sourceKey);
            attributesPages.get(sourceKey).saveState(pageState);
        }
    }

    @Override
    public void setFocus()
    {
    }

    @Override
    public void dispose()
    {
        for (AttributesPage page : this.attributesPages.values())
        {
            page.dispose();
        }

        super.dispose();
    }

    /*
     * 
     */
    private void createPermanentLayout(Composite parent)
    {
        parent.setLayout(new FillLayout());

        innerComposite = new Composite(parent, SWT.NONE);
        innerComposite.setLayout(new GridLayout(2, false));

        Label sourceLabel = new Label(innerComposite, SWT.CENTER);
        sourceLabel.setText("Source");
        Combo sourceCombo = new Combo(innerComposite, SWT.DROP_DOWN | SWT.READ_ONLY
            | SWT.BORDER);

        Label algorithmLabel = new Label(innerComposite, SWT.CENTER);
        algorithmLabel.setText("Algorithm");
        Combo algorithmCombo = new Combo(innerComposite, SWT.DROP_DOWN | SWT.READ_ONLY
            | SWT.BORDER);

        // init non-visuals
        GridData sourceGridData = new GridData();
        GridData algorithmGridData = new GridData();
        GridData processButtonGridData = new GridData();

        // set fields
        sourceGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        sourceGridData.grabExcessHorizontalSpace = true;
        algorithmGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        algorithmGridData.grabExcessHorizontalSpace = true;
        processButtonGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        processButtonGridData.verticalAlignment = GridData.END;
        processButtonGridData.horizontalSpan = 2;

        sourceLabel.setLayoutData(new GridData());

        algorithmLabel.setLayoutData(new GridData());

        sourceCombo.setLayoutData(sourceGridData);
        algorithmCombo.setLayoutData(algorithmGridData);

        sourceViewer = createComboBoxWrapper(sourceCombo, ComponentLoader.SOURCE_LOADER,
            SOURCE_ID_ATTRIBUTE);
        algorithmViewer = createComboBoxWrapper(algorithmCombo,
            ComponentLoader.ALGORITHM_LOADER, ALGORITHM_ID_ATTRIBUTE);

        createRequiredAttributesLayout();

        processButton = new Button(innerComposite, SWT.PUSH);
        processButton.setText("Process");
        processButton.setLayoutData(processButtonGridData);
    }
}
