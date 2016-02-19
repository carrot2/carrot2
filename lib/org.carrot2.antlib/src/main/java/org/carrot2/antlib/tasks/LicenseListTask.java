
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

package org.carrot2.antlib.tasks;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Lists all different licensing texts appearing in source code headers and/or footers.
 */
public class LicenseListTask extends AbstractLicenseTask
{
    /**
     * Process headers.
     */
    private boolean processHeader;

    /**
     * Process footers.
     */
    private boolean processFooter;

    /**
     * Verbose info.
     */
    private boolean verbose;

    private final HashMap<String, List<File>> headers = new HashMap<String, List<File>>();
    private final HashMap<String, List<File>> footers = new HashMap<String, List<File>>();

    /**
     * If set to <code>true</code>, process footers.
     */
    public void setFooter(boolean processFooter)
    {
        this.processFooter = processFooter;
    }

    /**
     * If set to <code>true</code>, process headers.
     */
    public void setHeader(boolean header)
    {
        this.processHeader = header;
    }

    /**
     * If set to <code>true</code> a more verbose info is dumped.
     */
    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    public void execute() throws BuildException
    {
        super.execute();
        final StringBuilder buffer = new StringBuilder();

        if (processHeader)
        {
            for (Map.Entry<String, List<File>> entry : headers.entrySet())
            {
                final List<File> files = entry.getValue();
                String header = entry.getKey();

                if ("".equals(header.trim()))
                {
                    header = "(empty)";
                }

                buffer.append("Header of " + files.size() + " file(s) is:");
                buffer.append("\n-------------------------\n");
                buffer.append(header);
                buffer.append("\n-------------------------\n");
                if (verbose)
                {
                    Collections.sort(files);
                    for (int f = 0; f < files.size(); f++)
                    {
                        buffer.append('\t');
                        buffer.append(((File) files.get(f)).getAbsolutePath());
                        buffer.append('\n');
                    }
                }
                buffer.append("\n");
            }
        }

        if (processFooter)
        {
            for (Map.Entry<String, List<File>> entry : footers.entrySet())
            {
                final List<File> files = entry.getValue();
                String footer = entry.getKey();

                if ("".equals(footer.trim()))
                {
                    footer = "(empty)";
                }

                buffer.append("Footer of " + files.size() + " file(s) is:");
                buffer.append("\n-------------------------\n");
                buffer.append(footer);
                buffer.append("\n-------------------------\n");
                if (verbose)
                {
                    Collections.sort(files);
                    for (int f = 0; f < files.size(); f++)
                    {
                        buffer.append('\t');
                        buffer.append(((File) files.get(f)).getAbsolutePath());
                        buffer.append('\n');
                    }
                }
                buffer.append("\n");
            }
        }

        super.log(buffer.toString(), Project.MSG_INFO);
    }

    /**
     * Process a single file.
     */
    protected void checkLicense(File file) throws IOException
    {
        final String content = readFile(file);
        final SourceFile split = new SourceFile(content, file);

        // Check header.
        if (processHeader)
        {
            final String trimmed = split.getCanonicalizedHeader();
            List<File> files = headers.get(trimmed);
            if (files == null)
            {
                files = new ArrayList<File>();
                headers.put(trimmed, files);
            }
            files.add(file);
        }

        // Check footer.
        if (processFooter)
        {
            final String trimmed = split.getCanonicalizedFooter();
            List<File> files = footers.get(trimmed);
            if (files == null)
            {
                files = new ArrayList<File>();
                footers.put(trimmed, files);
            }
            files.add(file);
        }
    }
}
