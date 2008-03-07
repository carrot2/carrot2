package org.carrot2.workbench.core.ui;

import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
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

    @Override
    public void createPartControl(Composite parent)
    {
        createPermanentLayout(parent);

        createItems(sourceCombo, ComponentLoader.SOURCE_LOADER);
        createItems(algorithmCombo, ComponentLoader.ALGORITHM_LOADER);

        final Runnable execQuery = new Runnable() {
            public void run() {
                try
                {
                    IWorkbenchPage page = SearchView.this.getViewSite()
                        .getWorkbenchWindow().getActivePage();
                    SearchParameters input = new SearchParameters(getSourceCaption(),
                        getAlgorithmCaption(), null);
                    input.putAttribute(AttributeNames.QUERY, queryText.getText());
                    page.openEditor(input, ResultsEditor.ID);
                }
                catch (Throwable ex)
                {
                    CorePlugin.getDefault().getLog().log(
                        new OperationStatus(IStatus.ERROR, CorePlugin.PLUGIN_ID, -1,
                            "Error while showing query result editor", ex));
                }
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

        queryText.addListener(SWT.DefaultSelection, new Listener () {
            public void handleEvent (Event e) {
                execQuery.run();
            }
        });
    }

    private String getSourceCaption()
    {
        // TODO: There should be some corner-case checks here (no algorithms, no sources?). 
        return sourceCombo.getItem(sourceCombo.getSelectionIndex());
    }

    private String getAlgorithmCaption()
    {
        return algorithmCombo.getItem(algorithmCombo.getSelectionIndex());
    }

    private void createItems(Combo combo, ComponentLoader loader)
    {
        combo.setItems(loader.getCaptions().toArray(new String [0]));
        combo.select(0);
    }

    @Override
    public void setFocus()
    {
        queryText.setFocus();
    }

    private void createPermanentLayout(Composite parent)
    {
        parent.setLayout(new FormLayout());

        innerComposite = new Composite(parent, SWT.NULL);
        Label sourceLabel = new Label(innerComposite, SWT.CENTER);
        sourceCombo = new Combo(innerComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        Label algorithmLabel = new Label(innerComposite, SWT.CENTER);
        algorithmCombo = new Combo(innerComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
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
        FormData_2.right = new FormAttachment(100, 0);
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
