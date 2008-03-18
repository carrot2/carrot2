package org.carrot2.workbench.core.ui;

import java.util.ArrayList;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.workbench.core.helpers.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;

public class SearchView extends ViewPart
{
    public static final String ID = "org.carrot2.workbench.core.search";

    private Composite innerComposite;
    private Combo sourceCombo;
    private Combo algorithmCombo;
    private Button processButton;
    private Text queryText;
    private java.util.List<Resource> toDispose = new ArrayList<Resource>();

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

        createItems(sourceCombo, ComponentLoader.SOURCE_LOADER);
        createItems(algorithmCombo, ComponentLoader.ALGORITHM_LOADER);

        checkProcessingConditions(parent);

        final Runnable execQuery = new RunnableWithErrorDialog()
        {
            public void runCore() throws Exception
            {
                IWorkbenchPage page =
                    SearchView.this.getViewSite().getWorkbenchWindow().getActivePage();
                SearchParameters input =
                    new SearchParameters(getSourceCaption(), getAlgorithmCaption(), null);
                input.putAttribute(AttributeNames.QUERY, queryText.getText());
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
                queryText.setFocus();
            }
        });

        queryText.addListener(SWT.DefaultSelection, new Listener()
        {
            public void handleEvent(Event e)
            {
                execQuery.run();
            }
        });
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
            queryText.setEnabled(false);
        }
        if (ComponentLoader.SOURCE_LOADER.getComponents().isEmpty())
        {
            disableComboWithMessage(sourceCombo, "No Document Source found!");
        }
        if (ComponentLoader.ALGORITHM_LOADER.getComponents().isEmpty())
        {
            disableComboWithMessage(algorithmCombo, "No Clustering Algorithm found!");
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

    private String getSourceCaption()
    {
        if (sourceCombo.getSelectionIndex() == -1)
        {
            return null;
        }
        return sourceCombo.getItem(sourceCombo.getSelectionIndex());
    }

    private String getAlgorithmCaption()
    {
        if (algorithmCombo.getSelectionIndex() == -1)
        {
            return null;
        }
        return algorithmCombo.getItem(algorithmCombo.getSelectionIndex());
    }

    private void createItems(Combo combo, ComponentLoader loader)
    {
        combo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        ComboViewer viewer = new ComboViewer(combo);
        viewer.setLabelProvider(new ComponentLabelProvider());
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setInput(loader.getComponents());
        combo.select(0);
    }

    @Override
    public void setFocus()
    {
        queryText.setFocus();
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
        sourceCombo =
            new Combo(innerComposite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
        Label algorithmLabel = new Label(innerComposite, SWT.CENTER);
        algorithmCombo =
            new Combo(innerComposite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
        Label queryLabel = new Label(innerComposite, SWT.CENTER);
        queryText = new Text(innerComposite, SWT.SINGLE | SWT.SEARCH);
        processButton = new Button(parent, SWT.PUSH);

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
        GridData_5.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridData_5.grabExcessHorizontalSpace = true;
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

        queryLabel.setLayoutData(new GridData());
        queryLabel.setText("Query:");

        sourceCombo.setLayoutData(GridData_3);
        sourceCombo.setText("Combo_1");

        algorithmCombo.setLayoutData(GridData_4);
        algorithmCombo.setText("Combo_2");

        queryText.setLayoutData(GridData_5);

        processButton.setLayoutData(FormData_2);
        processButton.setText("Process");

        innerComposite.setLayout(new GridLayout(4, false));
    }
}
