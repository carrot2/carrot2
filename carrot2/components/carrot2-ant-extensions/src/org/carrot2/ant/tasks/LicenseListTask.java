
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.ant.tasks;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;


/**
 * Lists various licenses in headers and/or footers.
 * 
 * @author Dawid Weiss
 */
public class LicenseListTask extends AbstractLicenseTask {
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

    private final HashMap headers = new HashMap();
    private final HashMap footers = new HashMap();

    /**
     * If set to <code>true</code>, process footers.
     */
    public void setFooter(boolean processFooter) {
        this.processFooter = processFooter;
    }

    /**
     * If set to <code>true</code>, process headers.
     */
    public void setHeader(boolean header) {
        this.processHeader = header;
    }
    
    /**
     * If set to <code>true</code> a more verbose info is dumped.
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void execute() throws BuildException {
        super.execute();

        // Emit the result.
        final StringBuffer buffer = new StringBuffer();

        if (processHeader) {
            for (Iterator i = headers.entrySet().iterator(); i.hasNext();) {
                final Map.Entry mapEntry = (Map.Entry) i.next();
                final ArrayList files = (ArrayList) mapEntry.getValue();
                String header = (String) mapEntry.getKey();

                if ("".equals(header.trim())) {
                    header = "(empty)";
                }

                buffer.append("Header of " + files.size() + " file(s) is:");
                buffer.append("\n-------------------------\n");
                buffer.append(header);
                buffer.append("\n-------------------------\n");
                if (verbose) {
                    Collections.sort(files);
                    for (int f = 0; f < files.size(); f++) {
                        buffer.append('\t');
                        buffer.append(((File) files.get(f)).getAbsolutePath());
                        buffer.append('\n');
                    }
                }
                buffer.append("\n");
            }
        }
        if (processFooter) {
            for (Iterator i = footers.entrySet().iterator(); i.hasNext();) {
                final Map.Entry mapEntry = (Map.Entry) i.next();
                final ArrayList files = (ArrayList) mapEntry.getValue();
                String footer = (String) mapEntry.getKey();
                
                if ("".equals(footer.trim())) {
                    footer = "(empty)";
                }

                buffer.append("Footer of " + files.size() + " file(s) is:");
                buffer.append("\n-------------------------\n");
                buffer.append(footer);
                buffer.append("\n-------------------------\n");
                if (verbose) {
                    Collections.sort(files);
                    for (int f = 0; f < files.size(); f++) {
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
    protected void checkLicense(File file) throws IOException {
        final String content = readFile(file);
        final SourceFile split = new SourceFile(content, file);

        // Check header.
        if (processHeader) {
            final String trimmed = split.getCanonicalizedHeader();
            ArrayList files = (ArrayList) headers.get(trimmed);
            if (files == null) {
                files = new ArrayList();
                headers.put(trimmed, files);
            }
            files.add(file);
        }

        // Check footer.
        if (processFooter) {
            final String trimmed = split.getCanonicalizedFooter();
            ArrayList files = (ArrayList) footers.get(trimmed);
            if (files == null) {
                files = new ArrayList();
                footers.put(trimmed, files);
            }
            files.add(file);
        }
    }
}
