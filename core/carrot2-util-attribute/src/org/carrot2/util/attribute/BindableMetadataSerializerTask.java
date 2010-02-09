
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.types.resources.FileResource;

/**
 * An ANT task for building XML metadata for classes annotated with {@link Bindable}.
 */
public final class BindableMetadataSerializerTask extends Task
{
    /**
     * Metadata builder.
     */
    private BindableMetadataBuilder builder;

    /**
     * Destination directory.
     */
    private File destDir;
    
    /** A list of input files. */
    private ArrayList<File> inputFiles = new ArrayList<File>();

    /** A list of metadata files. */
    private ArrayList<Path> metadataFiles = new ArrayList<Path>();

    @Override
    public void setProject(Project project)
    {
        super.setProject(project);
        builder = new BindableMetadataBuilder(project);        
    }

    /**
     * Set destination directory.
     */
    public void setDestdir(File destDir)
    {
        this.destDir = destDir;
    }

    /**
     * Add a set of files to process.
     */
    public void addConfiguredFileset(FileSet set)
    {
        add(set);
    }

    /**
     * Create nested element for common metadata files.
     */
    public Path createCommonMetadata()
    {
        Path p = new Path(getProject());
        metadataFiles.add(p);
        return p;
    }

    /**
     * Add a collection of resources to process.
     */
    @SuppressWarnings("unchecked")
    public void add(ResourceCollection res)
    {
        final Iterator<Resource> i = res.iterator();
        while (i.hasNext())
        {
            final Resource r = i.next();
            if (!(r instanceof FileResource))
            {
                throw new BuildException("Only file resources are supported: " + r);
            }
            addForProcessing((FileResource) r);
        }
    }

    /**
     * Add a file resource for processing.
     */
    private void addForProcessing(FileResource res)
    {
        inputFiles.add(res.getFile());
    }

    /**
     * Execute the task.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws BuildException
    {
        validate();

        if (inputFiles.size() == 0)
        {
            // No input files to process, exit immediately to avoid parsing metadata files.
            log("No input files, exiting.", Project.MSG_WARN);
            return;
        }

        for (File f : inputFiles)
        {
            log("Adding file for processing: " + f, Project.MSG_VERBOSE);
            try
            {
                builder.addSource(f);
            }
            catch (IOException e)
            {
                throw new BuildException("Could not process file: " + f, e);
            }
        }

        for (Path p : metadataFiles)
        {
            Iterator<Resource> i = p.iterator();
            while (i.hasNext())
            {
                final Resource r = i.next();
                if (!(r instanceof FileResource))
                {
                    throw new BuildException("Only file resources supported: " + r);
                }

                final FileResource fr = (FileResource) r;
                final File f = fr.getFile();

                log("Adding metadata file: " + f, Project.MSG_VERBOSE);
                try
                {
                    builder.addCommonMetadataSource(f);
                }
                catch (IOException e)
                {
                    throw new BuildException("Could not add metadata file: " + f);
                }
            }
        }
        
        builder.addListener(new BindableMetadataBuilderListener.XmlSerializerListener(
            destDir));
        builder.buildAttributeMetadata();
    }

    /**
     * Validate arguments.
     */
    private void validate()
    {
        if (destDir == null) throw new BuildException("destdir attribute is required.");
        if (!destDir.isDirectory()) throw new BuildException("Not a directory: "
            + destDir.getAbsolutePath());
    }
}
