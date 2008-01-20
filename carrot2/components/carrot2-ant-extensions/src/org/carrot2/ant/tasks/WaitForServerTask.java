
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.ant.tasks;

import java.io.*;
import java.net.*;

import org.apache.tools.ant.*;

/**
 * A task waiting until the given URL is accessible and
 * returns some expected token.
 * 
 * @author Dawid Weiss
 */
public class WaitForServerTask extends Task {
    
    private String url;
    private int timeout;
    private String encoding;
    private String token;

    public void setUrl(String url) {
        this.url = url;
    }
    
    public void setTimeout(int timeoutSeconds) {
        this.timeout = timeoutSeconds * 1000;
    }
    
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    
    public void setToken(String token) {
        this.token = token;
    }

    public void execute() {
        if (url == null) {
            throw new BuildException("Url attribute must be given.");
        }
        if (timeout <= 0) {
            throw new BuildException("Timeout must be greater than zero.");
        }
        if (encoding == null) {
            throw new BuildException("Encoding attribute must be given.");
        }
        if (token == null) {
            throw new BuildException("Token attribute must be given");
        }

        final long deadline = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < deadline) {
            try {
                log("Connecting to: " + url, Project.MSG_DEBUG);
                final URL realUrl = new URL(url);
                final URLConnection connection = realUrl.openConnection();
                final InputStream is = connection.getInputStream();
                try {
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int bte;
                    while ((bte = is.read()) != -1) {
                        baos.write(bte);
                    }

                    final String content = new String(baos.toByteArray(), encoding);
                    if (content.indexOf(token) >= 0) {
                        // Success.
                        return;
                    } else {
                        log("Connection acquired, but token not found.",
                                Project.MSG_WARN);
                    }
                } finally {
                    is.close();
                }
            } catch (MalformedURLException e) {
                throw new BuildException("URL is malformed: " + url);
            } catch (IOException e) {
                // Ignore exceptions until deadline passes.
                log("Connection to: " + url + " failed.", Project.MSG_DEBUG);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Ignore.
            }
        }
        throw new BuildException("Could not connect to: " + url
                + " within the given timeout.");
    }
}
