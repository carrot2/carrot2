
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

package org.carrot2.workbench.core.ui;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.carrot2.workbench.core.WorkbenchCorePlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * File dialogs and utility methods.
 */
public final class FileDialogs
{
    /**
     * Open save-as dialog prompting for an XML file name, with a possible path hint.
     */
    public static Path openSaveXML(IPath pathHint)
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
        }

        dialog.setFilterExtensions(new String []
        {
            "*.xml", "*.*"
        });
        dialog.setFilterNames(new String []
        {
            "XML Files", "All Files"
        });

        dialog.setOverwrite(true);

        String result = dialog.open();
        if (result == null) return null;
        return new Path(result);
    }

    /**
     * Open a dialog prompting for XML file name.
     */
    public static IPath openReadXML(IPath directoryHint)
    {
        final Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
        final FileDialog dialog = new FileDialog(shell, SWT.OPEN);

        if (directoryHint != null && !directoryHint.isEmpty())
        {
            dialog.setFilterPath(directoryHint.toOSString());
        }

        dialog.setFilterExtensions(new String []
        {
            "*.xml", "*.*"
        });
        dialog.setFilterNames(new String []
        {
            "XML Files", "All Files"
        });

        String result = dialog.open();
        if (result == null || !new File(result).exists())
        {
            return null;
        }

        return new Path(result);
    }

    /**
     * Remember a dialog's path at the given preferences key.
     */
    public static void rememberPath(String preferencesKey, IPath path)
    {
        WorkbenchCorePlugin.getPreferences().put(preferencesKey, path.toOSString());
    }

    /**
     * Remember the path's directory.
     */
    public static void rememberDirectory(String preferencesKey, IPath path)
    {
        File f = path.toFile();
        if (!f.isDirectory())
        {
            if (f.getParentFile() != null)
            {
                f = f.getParentFile();
            }
        }

        rememberPath(preferencesKey, new Path(f.getAbsolutePath()));
    }
    
    /**
     * Recall a dialog's path from the given preferences key. Root of the filesystem is
     * returned if the path does not exist.
     */
    public static Path recallPath(String preferencesKey)
    {
        String pathAsString = 
            WorkbenchCorePlugin.getPreferences().get(preferencesKey, getDefaultPath().toOSString());

        return checkOrDefault(pathAsString);
    }

    /**
     * Check if a given path points to a directory. If not, return the default path. 
     */
    public static Path checkOrDefault(String path)
    {
        if (path == null)
            return getDefaultPath();

        final File file = new File(path);
        if (file.getParentFile() == null || file.getParentFile().exists())
        {
            return new Path(file.getAbsolutePath());
        }
        else
        {
            return getDefaultPath();
        }
    }

    /**
     * @return The default path is user's home.
     */
    public static Path getDefaultPath()
    {
        return new Path(new File(System.getProperty("user.home", ".")).getAbsolutePath());
    }

    /**
     * Sanitize filename not to include special characters.
     */
    public static String sanitizeFileName(String anything)
    {
        String result = anything.replaceAll("[^a-zA-Z0-9_\\-.\\s]", "");
        result = result.trim().replaceAll("[\\s]+", "-");
        result = result.toLowerCase();
        if (StringUtils.isEmpty(result))
        {
            result = "unnamed";
        }
        return result;
    }
}
