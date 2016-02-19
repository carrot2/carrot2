
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

/**
 * A source file split into header, footer and content.
 */
final class SourceFile
{
    private final String header;
    private final String content;
    private final String footer;

    private final String canonicalizedHeader;
    private final String canonicalizedFooter;

    public SourceFile(String header, String content, String footer)
    {
        this.header = header;
        this.content = content;
        this.footer = footer;

        this.canonicalizedHeader = canonicalize(header);
        this.canonicalizedFooter = canonicalize(footer);
    }

    public SourceFile(String fullFile, File file)
    {
        /*
         * Simple header detection. We assume some reasonable
         * input source format (e.g. formatted).
         */
        final int headerEnd = fullFile.indexOf("package");
        if (headerEnd < 0)
        {
            // No package? Complain.
            throw new RuntimeException(
                "Header detection failed, no package statement in file: "
                    + file.getAbsolutePath() + ", can't proceed.");
        }
        if (headerEnd == 0)
        {
            this.header = "";
        }
        else
        {
            this.header = fullFile.substring(0, headerEnd);
        }

        // Simple footer detection. Again, we assume a reasonable input format
        int footerEnd = fullFile.lastIndexOf('}');
        if (file.getName().equals("package-info.java")) {
           footerEnd = fullFile.length() - 1;
        }

        if (footerEnd < 0)
        {
            // No footer end marker? Complain.
            throw new RuntimeException("Footer detection failed, no class end in file: "
                + file.getAbsolutePath() + ", can't proceed.");
        }

        if (footerEnd + 1 - fullFile.length() == 0)
        {
            footer = "";
        }
        else
        {
            footer = fullFile.substring(footerEnd + 1, fullFile.length());
        }

        this.content = fullFile.substring(headerEnd, footerEnd + 1);

        this.canonicalizedHeader = canonicalize(header);
        this.canonicalizedFooter = canonicalize(footer);
    }

    public String getCanonicalizedHeader()
    {
        return canonicalizedHeader;
    }

    public String getCanonicalizedFooter()
    {
        return canonicalizedFooter;
    }

    public String getContent()
    {
        return content;
    }

    public String getFooter()
    {
        return footer;
    }

    public String getHeader()
    {
        return header;
    }

    /**
     * Canonicalize a header/ footer by removing trailing spaces and normalizing EOLs.
     */
    protected static String canonicalize(String string)
    {
        if (string == null) return "";
        return string.trim().replaceAll("\r", "");
    }

    /**
     * Recreates full file contents from splits.
     */
    public String recreateFile()
    {
        return this.header + this.content + this.footer;
    }

    /**
     * Returns <code>true</code> if this split is identical (in canonical sense) to
     * another.
     */
    public boolean isIdentical(SourceFile split)
    {
        if (this.content.equals(split.content)
            && this.canonicalizedHeader.equals(split.canonicalizedHeader)
            && this.canonicalizedFooter.equals(split.canonicalizedFooter))
        {
            return true;
        }
        return false;
    }
}
