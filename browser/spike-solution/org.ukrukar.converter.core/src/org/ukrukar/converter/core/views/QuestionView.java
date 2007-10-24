package org.ukrukar.converter.core.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.ukrukar.converter.core.editors.MultiTextViewerEditor;
import org.ukrukar.converter.core.editors.TextInput;

public class QuestionView extends ViewPart {

	public QuestionView() {
		// TODO Auto-generated constructor stub
	}

	public static final String ID = "org.ukrukar.converter.core.questionview";

	private Combo algorithmCombo;
	private Text queryText;

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.numColumns = 2;
		parent.setLayout(layout);

		Font font = parent.getDisplay().getSystemFont();

		Label queryLabel = new Label(parent, SWT.LEFT);
		queryLabel.setText("Text");
		queryLabel.setFont(font);
		queryLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false,
				false));

		queryText = new Text(parent, SWT.LEFT | SWT.BORDER);
		queryText.setFont(font);
		queryText
				.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));

		Label algotithmLabel = new Label(parent, SWT.LEFT);
		algotithmLabel.setText("Transform"); //$NON-NLS-1$
		algotithmLabel.setFont(font);
		algotithmLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false,
				false));

		algorithmCombo = new Combo(parent, SWT.LEFT | SWT.BORDER);
		algorithmCombo.setFont(font);
		algorithmCombo.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true,
				false));

		Button submitButton = new Button(parent, SWT.PUSH | SWT.CENTER);
		submitButton.setText("GO!"); //$NON-NLS-1$
		submitButton.setFont(font);
		GridData data = new GridData(SWT.RIGHT, SWT.BOTTOM, false, false);
		data.horizontalSpan = 2;
		submitButton.setLayoutData(data);
		submitButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = queryText.getText();
				QuestionView.this.getViewSite().getWorkbenchWindow()
						.getActivePage();
				IWorkbenchPage page = QuestionView.this.getViewSite()
						.getWorkbenchWindow().getActivePage();
				TextInput input = new TextInput(text);
				try {
					page.openEditor(input, MultiTextViewerEditor.ID);
				} catch (PartInitException ex) {
					// handle error
				}

			}

		});

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
