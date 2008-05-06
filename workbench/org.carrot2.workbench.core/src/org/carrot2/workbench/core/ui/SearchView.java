package org.carrot2.workbench.core.ui;

import static org.eclipse.swt.SWT.FILL;

import java.util.*;

import org.carrot2.core.ProcessingComponent;
import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Required;
import org.carrot2.workbench.core.helpers.*;
import org.carrot2.workbench.core.ui.attributes.AttributesPage;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;

public class SearchView extends ViewPart
{
    private static final String ALGORITHM_ID_ATTRIBUTE = "algorithmId";

    private static final String SOURCE_ID_ATTRIBUTE = "sourceId";

    public static final String ID = "org.carrot2.workbench.core.search";

    private IMemento state;
    private Composite innerComposite;
    private ComboViewer sourceViewer;
    private ComboViewer algorithmViewer;
    private Button processButton;
    private java.util.List<Resource> toDispose = new ArrayList<Resource>();
    private Map<String, AttributesPage> attributesPages =
        new HashMap<String, AttributesPage>();

    private class ComponentLabelProvider extends LabelProvider
    {
        @Override
        public String getText(Object element)
        {
            return ((ComponentWrapper) element).getCaption();
        }

    }

    @Override
    public void createPartControl(Composite parent)
    {
        createPermanentLayout(parent);

        checkProcessingConditions(parent);

        final Runnable execQuery = new RunnableWithErrorDialog()
        {
            public void runCore() throws Exception
            {
                IWorkbenchPage page =
                    SearchView.this.getViewSite().getWorkbenchWindow().getActivePage();
                SearchParameters input =
                    new SearchParameters(getSourceId(), getAlgorithmId(), null);
                input.putAllAttributes(attributesPages.get(getSourceId())
                    .getAttributeValues());
                page.openEditor(input, ResultsEditor.ID);
            }

            @Override
            protected String getErrorTitle()
            {
                return "Error while opening query result editor";
            }
        };

        processButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                execQuery.run();

                // Set the focus back to the query box (?).
                //                queryText.setFocus();
            }
        });

    }

    @SuppressWarnings("unchecked")
    private void createRequiredAttributesLayout()
    {
        final Group requiredHolder = new Group(innerComposite, SWT.NONE);
        requiredHolder.setText("Required attributes");
        final StackLayout stack = new StackLayout();
        for (ComponentWrapper wrapper : ComponentLoader.SOURCE_LOADER.getComponents())
        {
            ProcessingComponent source = wrapper.getExecutableComponent();
            AttributesPage page =
                new AttributesPage(source, new HashMap<String, Object>());
            page.filterAttributes(Input.class, Processing.class, Required.class);
            page.init(new PageSite(this.getViewSite()));
            page.createControl(requiredHolder);
            attributesPages.put(wrapper.getId(), page);
        }
        requiredHolder.setLayout(stack);
        restoreRequiredAttributesState();

        if (getSourceId() != null)
        {
            stack.topControl = attributesPages.get(getSourceId()).getControl();
            requiredHolder.layout();
        }
        sourceViewer.getCombo().addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (getSourceId() != null)
                {
                    stack.topControl = attributesPages.get(getSourceId()).getControl();
                    requiredHolder.layout();
                }
            }
        });
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = FILL;
        gd.horizontalSpan = 2;
        requiredHolder.setLayoutData(gd);
    }

    private void restoreRequiredAttributesState()
    {
        if (state != null)
        {
            IMemento allPagesState = state.getChild("requiredAttributesPages");
            if (allPagesState != null)
            {
                IMemento [] pageStatesArray =
                    allPagesState.getChildren("requiredAttributesPage");
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
            disableComboWithMessage(sourceViewer.getCombo(), "No Document Source found!");
        }
        if (ComponentLoader.ALGORITHM_LOADER.getComponents().isEmpty())
        {
            disableComboWithMessage(algorithmViewer.getCombo(),
                "No Clustering Algorithm found!");
        }
    }

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

    private String getSourceId()
    {
        if (sourceViewer.getSelection().isEmpty())
        {
            return null;
        }
        return ((ComponentWrapper) ((IStructuredSelection) sourceViewer.getSelection())
            .getFirstElement()).getId();
    }

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
     * Wraps component combobox (source or algorithm) with JFace viewer. Restores saved
     * state if possible.
     * 
     * @param combo combo control used to create a viewer ({@link ComboViewer#ComboViewer(Combo)})
     * @param loader loader of components (sources or algorithms)
     * @param stateAttribute name of the attribute, which stores id of the component, that
     *            should be chosen by default
     * @return create wrapper
     */
    private ComboViewer createViewer(Combo combo, ComponentLoader loader,
        String stateAttribute)
    {
        combo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        ComboViewer viewer = new ComboViewer(combo);
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
            Utils
                .logError("Problem accured while restoring Search view state", re, false);
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
            IMemento pageState =
                pagesState.createChild("requiredAttributesPage", sourceKey);
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
        for (Resource resource : toDispose)
        {
            resource.dispose();
        }
        super.dispose();
    }

    private void createPermanentLayout(Composite parent)
    {
        parent.setLayout(new FormLayout());

        innerComposite = new Composite(parent, SWT.NULL);
        Label sourceLabel = new Label(innerComposite, SWT.CENTER);
        Combo sourceCombo =
            new Combo(innerComposite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
        Label algorithmLabel = new Label(innerComposite, SWT.CENTER);
        Combo algorithmCombo =
            new Combo(innerComposite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);

        // init nonvisuals
        GridData GridData_3 = new GridData();
        GridData GridData_4 = new GridData();
        GridData GridData_5 = new GridData();
        FormData FormData_1 = new FormData();
        FormData FormData_2 = new FormData();

        // set fields
        GridData_3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridData_3.grabExcessHorizontalSpace = true;
        GridData_4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridData_4.grabExcessHorizontalSpace = true;
        GridData_5.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
        GridData_5.verticalAlignment = GridData.END;
        GridData_5.horizontalSpan = 2;
        FormData_1.right = new FormAttachment(100, 0);
        FormData_1.top = new FormAttachment(0, 0);
        FormData_1.left = new FormAttachment(0, 0);
        FormData_2.right = new FormAttachment(100, -5);
        FormData_2.top = new FormAttachment(innerComposite, 0, 0);

        innerComposite.setLayoutData(FormData_1);

        // TODO: We'll need to think about i18n at some point (externalize strings).

        sourceLabel.setLayoutData(new GridData());
        sourceLabel.setText("Source:");

        algorithmLabel.setLayoutData(new GridData());
        algorithmLabel.setText("Algorithm:");

        sourceCombo.setLayoutData(GridData_3);
        sourceCombo.setText("Combo_1");

        algorithmCombo.setLayoutData(GridData_4);
        algorithmCombo.setText("Combo_2");

        sourceViewer =
            createViewer(sourceCombo, ComponentLoader.SOURCE_LOADER, SOURCE_ID_ATTRIBUTE);
        algorithmViewer =
            createViewer(algorithmCombo, ComponentLoader.ALGORITHM_LOADER,
                ALGORITHM_ID_ATTRIBUTE);

        createRequiredAttributesLayout();

        processButton = new Button(innerComposite, SWT.PUSH);
        processButton.setLayoutData(GridData_5);
        processButton.setText("Process");

        innerComposite.setLayout(new GridLayout(4, false));
    }
}
