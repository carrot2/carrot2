package org.carrot2.workbench.core.ui;

import org.carrot2.workbench.core.CorePlugin;
import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

    @Override
    public void createPartControl(Composite parent)
    {
        createPermanentLayout(parent);

        createItems(sourceCombo, ComponentLoader.SOURCE_LOADER);
        createItems(algorithmCombo, ComponentLoader.ALGORITHM_LOADER);
        processButton.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                try
                {
                    IWorkbenchPage page = SearchView.this.getViewSite()
                        .getWorkbenchWindow().getActivePage();
                    SearchParameters input = new SearchParameters(getSourceCaption(),
                        getAlgorithmCaption(), null);
                    page.openEditor(input, ResultsEditor.ID);
                }
                catch (Throwable ex)
                {
                    CorePlugin.getDefault().getLog().log(
                        new OperationStatus(IStatus.ERROR, CorePlugin.PLUGIN_ID, -1,
                            "Error while showing query result editor", ex));
                }
            }
        });

    }

    private String getSourceCaption()
    {
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
    }

    private void createPermanentLayout(Composite parent)
    {
        parent.setLayout(new FormLayout());

        innerComposite = new Composite(parent, SWT.NULL);
        Label sourceLabel = new Label(innerComposite, SWT.CENTER);
        sourceCombo = new Combo(innerComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        Label algorithmLabel = new Label(innerComposite, SWT.CENTER);
        algorithmCombo = new Combo(innerComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        processButton = new Button(parent, SWT.PUSH);

        // init nonviusuals
        GridData GridData_3 = new GridData();
        GridData GridData_4 = new GridData();
        FormData FormData_1 = new FormData();
        FormData FormData_2 = new FormData();

        // set fields
        GridData_3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridData_3.grabExcessHorizontalSpace = true;
        GridData_4.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
        GridData_4.grabExcessHorizontalSpace = true;
        FormData_1.right = new FormAttachment(100, 0);
        FormData_1.top = new FormAttachment(0, 0);
        FormData_1.left = new FormAttachment(0, 0);
        FormData_2.right = new FormAttachment(100, 0);
        FormData_2.top = new FormAttachment(innerComposite, 0, 0);

        innerComposite.setLayoutData(FormData_1);

        sourceLabel.setLayoutData(new GridData());
        sourceLabel.setText("Source:");

        algorithmLabel.setLayoutData(new GridData());
        algorithmLabel.setText("Algorithm:");

        sourceCombo.setLayoutData(GridData_3);
        sourceCombo.setText("Combo_1");

        algorithmCombo.setLayoutData(GridData_4);
        algorithmCombo.setText("Combo_2");

        processButton.setLayoutData(FormData_2);
        processButton.setText("Process");

        innerComposite.setLayout(new GridLayout(4, false));
    }

}
