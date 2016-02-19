
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
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * Replaces licensing information according to the given set of replace-rules.
 */
public class LicenseReplaceTask extends AbstractLicenseTask
{
    /**
     * A replaceable block contains a key which detects the header to be replaced ({@link #setContains(String)})
     * and a replacement ({@link #setReplacement(String)}) which replaces it.
     */
    public abstract class ReplaceableBlock
    {
        private String containsKey;
        private String replacement;
        private boolean empty;
        private String canonicalContainsKey;

        public void setContains(String key)
        {
            this.containsKey = getProject().replaceProperties(key);
        }

        public void setReplacement(String replacement)
        {
            this.replacement = getProject().replaceProperties(replacement);
        }

        public void setIsempty(boolean emptyHeader)
        {
            this.empty = emptyHeader;
        }

        protected void checkAttributes() throws BuildException
        {
            if (empty && containsKey != null)
            {
                throw new BuildException(
                    "Attributes empty and contains are mutually exclusive.");
            }

            this.canonicalContainsKey = SourceFile.canonicalize(containsKey);
        }

        public abstract SourceFile process(SourceFile original);

        protected boolean checkReplace(String text)
        {
            if (empty)
            {
                if (text.length() == 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            else
            {
                return text.indexOf(canonicalContainsKey) >= 0;
            }
        }
    }

    public class HeaderBlock extends ReplaceableBlock
    {
        public SourceFile process(SourceFile original)
        {
            checkAttributes();
            if (checkReplace(original.getCanonicalizedHeader()))
            {
                return new SourceFile(super.replacement, original.getContent(), original
                    .getFooter());
            }
            else
            {
                return null;
            }
        }
    }

    public class FooterBlock extends ReplaceableBlock
    {
        public SourceFile process(SourceFile original)
        {
            checkAttributes();
            if (checkReplace(original.getCanonicalizedFooter()))
            {
                return new SourceFile(original.getHeader(), original.getContent(),
                    super.replacement);
            }
            else
            {
                return null;
            }
        }
    }

    /** Verbose info. */
    private boolean verbose;

    /**
     * Number of processed files using each of the {@link ReplaceableBlock}s in
     * {@link #blocks} variable.
     */
    private int [] processed;

    /**
     * An extension added to saved files. Leave empty to overwrite.
     */
    private String saveExtension;

    /**
     * Replaceable blocks for processing.
     */
    private ArrayList<ReplaceableBlock> blocks = new ArrayList<ReplaceableBlock>();

    public ReplaceableBlock createHeader()
    {
        HeaderBlock block = new HeaderBlock();
        blocks.add(block);
        return block;
    }

    public ReplaceableBlock createFooter()
    {
        FooterBlock block = new FooterBlock();
        blocks.add(block);
        return block;
    }

    /**
     * If set to <code>true</code> a more verbose info is dumped.
     */
    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    /**
     * An extension added to saved files. Leave empty to overwrite.
     */
    public void setSaveExtension(String extension)
    {
        this.saveExtension = extension;
    }

    public void execute() throws BuildException
    {
        this.processed = new int [blocks.size()];
        super.execute();

        final StringBuffer buf = new StringBuffer();
        int total = 0;
        for (int i = 0; i < processed.length; i++)
        {
            final ReplaceableBlock block = (ReplaceableBlock) blocks.get(i);

            if (processed[i] > 0)
            {
                buf.append("License (key: " + block.containsKey + "): " + processed[i]
                    + "\n");
            }
            total += processed[i];
        }

        super.log("Saved: " + total + " files.", Project.MSG_INFO);
        if (buf.toString().trim().length() > 0)
        {
            super.log(buf.toString(), Project.MSG_INFO);
        }
    }

    /**
     * Process a single file.
     */
    protected void checkLicense(File file) throws IOException
    {
        final String content = readFile(file);
        final SourceFile split = new SourceFile(content, file);

        for (int i = 0; i < blocks.size(); i++)
        {
            final ReplaceableBlock block = (ReplaceableBlock) blocks.get(i);
            final SourceFile newSplit = block.process(split);
            if (newSplit != null)
            {
                if (newSplit.isIdentical(split))
                {
                    return;
                }

                processed[i]++;
                super.log("Saving replaced file: " + file.getName(),
                    verbose ? Project.MSG_INFO : Project.MSG_VERBOSE);

                writeFile(newSplit.recreateFile(), new File(file.getAbsolutePath()
                    + saveExtension));
            }
        }
    }
}
