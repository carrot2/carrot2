package org.carrot2.workbench.core.search;

import java.util.List;

import org.carrot2.workbench.core.helpers.ComponentLoader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class SearchView extends ViewPart
{

    public static final String ID = "org.carrot2.workbench.core.search";

    public SearchView()
    {
    }

    @Override
    public void createPartControl(Composite parent)
    {
        Label l = new Label(parent, SWT.WRAP);
        List<String> captions = ComponentLoader.SOURCE_LOADER.getCaptions();
        String text = "Available sources:";
        text += System.getProperty("line.separator");
        for (String caption : captions)
        {
            text += caption + System.getProperty("line.separator");
        }
        captions = ComponentLoader.ALGORITHM_LOADER.getCaptions();
        text += "Available algorithms:";
        text += System.getProperty("line.separator");
        for (String caption : captions)
        {
            text += caption + System.getProperty("line.separator");
        }
        l.setText(text);
    }

    @Override
    public void setFocus()
    {
    }

}
