package org.carrot2.workbench.core.ui.actions;

import java.io.IOException;
import java.io.OutputStream;

public interface IImageStreamProvider
{
    public void save(OutputStream os) throws IOException;
}
