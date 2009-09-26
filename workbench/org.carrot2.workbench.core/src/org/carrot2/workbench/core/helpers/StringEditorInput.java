
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.workbench.core.helpers;

import java.io.*;
import java.net.URI;

import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.*;

/**
 * An input implementation for Eclipse's default text editor that reads from
 * an in-memory string and allows specifying encoding information.
 */
@SuppressWarnings("unchecked")
public final class StringEditorInput 
    implements IStorageEditorInput, IURIEditorInput
{
    private final String inputString;

    public StringEditorInput(String inputString)
    {
        this.inputString = inputString;
    }

    public boolean exists()
    {
        return false;
    }

    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }

    public IPersistableElement getPersistable()
    {
        return null;
    }

    public Object getAdapter(Class adapter)
    {
        return null;
    }

    public String getName()
    {
        return "untitled";
    }

    public String getToolTipText()
    {
        return "";
    }

    public URI getURI()
    {
        return new File("unnamed.txt").toURI();
    }

    public IStorage getStorage() throws CoreException
    {
        return new IEncodedStorage()
        {
            public InputStream getContents() throws CoreException
            {
                try
                {
                    return new ByteArrayInputStream(inputString.getBytes("UTF-8"));
                }
                catch (UnsupportedEncodingException e)
                {
                    throw new RuntimeException(e);
                }
            }

            public IPath getFullPath()
            {
                return null;
            }

            public String getName()
            {
                return StringEditorInput.this.getName();
            }

            public boolean isReadOnly()
            {
                return true;
            }

            public Object getAdapter(Class adapter)
            {
                return null;
            }

            public String getCharset() throws CoreException
            {
                return "UTF-8";
            }
        };
    }
}
