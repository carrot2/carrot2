package org.ukrukar.converter.core.views;

import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.ui.part.ViewPart;
import org.krukar.converter.core.logic.ITextConverter;
import org.krukar.converter.core.logic.loader.ConverterLoader;
import org.ukrukar.converter.core.Activator;
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
		loadAlgorithmChoices();

		Button submitButton = new Button(parent, SWT.PUSH | SWT.CENTER);
		submitButton.setText("GO!"); //$NON-NLS-1$
		submitButton.setFont(font);
		GridData data = new GridData(SWT.RIGHT, SWT.BOTTOM, false, false);
		data.horizontalSpan = 2;
		submitButton.setLayoutData(data);
		submitButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String text = queryText.getText();
					String converterCaption = algorithmCombo.getText();
					ITextConverter convertion = ConverterLoader
							.getConverter(converterCaption);
					QuestionView.this.getViewSite().getWorkbenchWindow()
							.getActivePage();
					IWorkbenchPage page = QuestionView.this.getViewSite()
							.getWorkbenchWindow().getActivePage();
					TextInput input = new TextInput(convertion.convert(text));
					page.openEditor(input, MultiTextViewerEditor.ID);
				} catch (Throwable ex) {
					Activator.getDefault().getLog().log(
							new OperationStatus(IStatus.ERROR,
									Activator.PLUGIN_ID, -1,
									"Error while showing query result editor",
									ex));
				}

			}

		});

	}

	private void loadAlgorithmChoices() {
		for (String caption : ConverterLoader.getCaptions()) {
			algorithmCombo.add(caption);
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
