
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.ui.actions;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.carrot2.util.CloseableUtils;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;

/**
 * Export a flash visualization as an image.
 */
public class ExportImageAction extends Action
{
    private static String SAVE_PNG_PATH = "dialogs.save.png.path";
    private final IImageStreamProvider imageStreamProvider;

    public ExportImageAction(IImageStreamProvider imageStreamProvider)
    {
        setImageDescriptor(WorkbenchCorePlugin
            .getImageDescriptor("icons/save_e.png"));
        setToolTipText("Export as PNG");

        this.imageStreamProvider = imageStreamProvider;
    }
    
    public ExportImageAction(final IControlProvider compositeProvider)
    {
        this(new IImageStreamProvider()
        {
            public void save(OutputStream os) throws IOException
            {
                Control control = compositeProvider.getControl();
                GC gc = new GC(control);
                Image image = new Image(control.getDisplay(), control.getBounds());
                gc.copyArea(image, 0, 0);
                gc.dispose();

                ImageLoader loader = new ImageLoader();
                loader.data = new ImageData[] {image.getImageData()};
                loader.save(os, SWT.IMAGE_PNG);

                image.dispose();                
            }
        });
    }

    @Override
    public void run()
    {
        File tempFile = null;
        OutputStream os = null;
        try {
            tempFile = File.createTempFile("capture", "png");
            os = new FileOutputStream(tempFile);
            this.imageStreamProvider.save(os);
            CloseableUtils.close(os);

            final Path path = openSavePNG(new Path("clusters-" 
                + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) 
                + ".png"));

            if (path != null)
            {
                FileUtils.moveFile(tempFile, path.toFile());
            }
        } catch (IOException e) {
            CloseableUtils.close(os);
            if (tempFile != null) tempFile.delete();
        } finally {
            CloseableUtils.close(os);
        }
    }
    
    /**
     * Open save-as dialog prompting for an PNG image name, with a possible path hint.
     */
    public static Path openSavePNG(IPath pathHint)
    {
        final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
        final FileDialog dialog = new FileDialog(shell, SWT.SAVE);

        if (pathHint != null && !pathHint.isEmpty())
        {
            if (pathHint.segmentCount() >= 2)
            {
                dialog.setFileName(pathHint.lastSegment());
                dialog.setFilterPath(pathHint.removeLastSegments(1).toOSString());
            } 
            else if (pathHint.segmentCount() == 1)
            {
                dialog.setFilterPath(
                    WorkbenchCorePlugin.getDefault().getPreferenceStore().getString(SAVE_PNG_PATH));
                dialog.setFileName(pathHint.lastSegment());
            }
        }

        dialog.setFilterExtensions(new String []
        {
            "*.png", "*.*"
        });
        dialog.setFilterNames(new String []
        {
            "PNG Files", "All Files"
        });

        dialog.setOverwrite(true);

        String result = dialog.open();
        if (result == null) return null;

        Path path = new Path(result);
        WorkbenchCorePlugin.getDefault().getPreferenceStore().setValue(SAVE_PNG_PATH, 
            path.removeLastSegments(1).toOSString());
        return path;
    }
}
