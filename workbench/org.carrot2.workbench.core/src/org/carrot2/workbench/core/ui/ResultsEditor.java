package org.carrot2.workbench.core.ui;

import org.carrot2.core.*;
import org.carrot2.core.attribute.AttributeNames;
import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.*;
import org.eclipse.ui.part.MultiPageEditorPart;

public class ResultsEditor extends MultiPageEditorPart
{
    public static final String ID = "org.carrot2.workbench.core.editors.results";

    /**
     * Creates page 0 of the multi-page editor, which contains a text editor.
     */
    void createPage0()
    {
        SearchParameters search = (SearchParameters) getEditorInput();

        final SimpleController controller = new SimpleController();
        ProcessingResult result = controller.process(search.getAttributes(),
            ComponentLoader.SOURCE_LOADER.getComponent(search.getSourceCaption()),
            ComponentLoader.ALGORITHM_LOADER.getComponent(search.getAlgorithmCaption()));

        Text l = new Text(getContainer(), SWT.MULTI | SWT.WRAP | SWT.READ_ONLY
            | SWT.V_SCROLL);

        StringBuilder builder = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        for (Document doc : result.getDocuments())
        {
            builder.append(doc.getField(Document.TITLE));
            builder.append(" - ");
            builder.append(doc.getField(Document.CONTENT_URL));
            builder.append(newLine);
        }
        l.setText(builder.toString());
        addPage(l);
        setPageText(0, "Documents");
    }

    protected void createPages()
    {
        createPage0();
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        if (!(input instanceof SearchParameters)) throw new PartInitException(
            "Invalid Input: Must be SearchParameters");

        super.init(site, input);
    }

    public void doSave(IProgressMonitor monitor)
    {
    }

    public void doSaveAs()
    {
    }

    public boolean isSaveAsAllowed()
    {
        return false;
    }

    @Override
    public String getPartName()
    {
        return ((SearchParameters) this.getEditorInput()).getAttributes().get(
            AttributeNames.QUERY).toString();
    }

    @Override
    public String getTitleToolTip()
    {
        return "Results";
    }
}
