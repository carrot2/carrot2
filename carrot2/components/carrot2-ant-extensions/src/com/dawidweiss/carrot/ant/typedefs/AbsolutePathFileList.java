/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * All rights reserved.
 *
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.dawidweiss.carrot.ant.typedefs;


import org.apache.tools.ant.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A subclass of <code>FileList</code> that contains only
 * absolute file references.
 */
public class AbsolutePathFileList
    extends FileList
{
    private String error;
    private ArrayList files = new ArrayList();

    public AbsolutePathFileList()
    {
        super();
    }

    /**
     * Adds a single file to this fileset.
     */
    public void addFile(File file)
    {
        if (file.isAbsolute() == false)
        {
            throw new RuntimeException(
                "Only absolute references are accepted" + " in this file list."
            );
        }

        files.add(file.getAbsolutePath());
    }


    /**
     * Return the root of the filesystem.
     */
    public File getDir(Project s)
    {
        File root = ((files.size() == 0) ? s.getBaseDir()
                                         : new File((String) files.get(0)));

        while (root.getParentFile() != null)
        {
            root = root.getParentFile();
        }

        return root;
    }


    /**
     * Forbid attribute.
     */
    public void setDir(File arg0)
        throws BuildException
    {
        throw new RuntimeException("This type does not accept dir attribute.");
    }


    /**
     * @see org.apache.tools.ant.types.FileList#getFiles(org.apache.tools.ant.Project)
     */
    public String [] getFiles(Project arg0)
    {
        String [] filesAsStrings = new String[files.size()];
        files.toArray(filesAsStrings);

        try
        {
            File absRoot = getDir(getProject()).getCanonicalFile();
            FileUtils futils = FileUtils.newFileUtils();

            for (int i = 0; i < filesAsStrings.length; i++)
            {
                File path = new File(
                        futils.removeLeadingPath(absRoot, new File(filesAsStrings[i]))
                    );

                if (path.isAbsolute())
                {
                    throw new RuntimeException(
                        "Dependency list spans across many volumes. File lists in ANT do not allow this situation."
                    );
                }

                filesAsStrings[i] = path.getPath();
            }
        }
        catch (IOException e)
        {
        }

        return filesAsStrings;
    }

}
