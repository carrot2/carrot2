
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

package com.dawidweiss.carrot.ant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;

/**
 * An ANT task that sets the name of a given
 * property to the timestamp of the least recently
 * modified file in a provided <code>FileSet</code>.
 * The timestamp is expressed in milliseconds since
 * Jan 1, 1970.
 */
public class MostRecentFileDate
    extends Task 
{
    private CustomFormat format;

    /** Name of a property that will be initialized with formatted date
     * of the most recent file. */
    private String dateProperty;

    /** A list of <code>FileSet</code> objects. */
    private List fileSets = new LinkedList();
    
    /** Name of the property to set. */
    private String millisProperty;
    
    /**
     * If <code>true</code>, the result is stored in {@link #lastModified}
     * field.
     */
    private boolean storeInField;
    
    /**
     * If {@link #storeInField} is <code>true</code>, this field
     * will contain the result of this task after invoking {@link #execute()}.
     */
    private long lastModified;

    /** 
     *  If <code>true</code> an exception is thrown
     *  if no files are included in all of the filesets.
     */
    private boolean exceptionOnEmpty;

    /**
     * A simple inner class for providing custom date format.
     */
    public static class CustomFormat {

        private String pattern;

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getFormatted(long millis) {
            if (this.pattern == null)
                throw new BuildException("Pattern attribute must be provided");
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(new Date(millis));
        }
    }
    
    /**
     * If set to true, the task will throw an exception
     * if there are no files included in all of the 
     * combined filesets.
     */    
    public void setExceptionOnEmpty(boolean value) {
        this.exceptionOnEmpty = value;
    }
    
    /** 
     * Adds a <code>FileSet</code> to the
     * list of files to check.
     */
    public void addFileset(FileSet fs) {
        fileSets.add( fs );
    }
    
    /**
     * Sets the name of the property to initialize
     * with milliseconds.
     */
    public void setMillisProperty(String name) {
        this.millisProperty = name;
    }
    
    /**
     * Sets the name of the property to initialize
     * with formatted date.
     */
    public void setDateProperty(String name) {
        this.dateProperty = name;
    }
    
    /**
     * Sets {@link #storeInField}.
     */
    public void setStoreInField(boolean v) {
        this.storeInField = v;
    }
    
    public long getLastModified() {
        return lastModified;
    }

    public CustomFormat createFormat() {
        if (format != null)
            throw new BuildException("Only one format child element is allowed.");

        CustomFormat cts = new CustomFormat();
        format = cts;
        return cts;
    } 

    /**
     * Verifies that all parameters are provided and correct;
     * @throws BuildException
     */
    protected void checkParameters() throws BuildException {
        if (this.fileSets.size() == 0) {
            throw new BuildException("At least one embedded FileSet is required.");
        }
        
        if (this.millisProperty == null && this.dateProperty == null && storeInField == false) {
            throw new BuildException("dateProperty or millisProperty attribute must be provided.");
        }
    }

    /**
     * Verifies parameters and executes the task.
     * @throws BuildException 
     */
	public void execute() throws BuildException {
		super.execute();

        checkParameters();
        
        FileUtils fileUtils = FileUtils.newFileUtils();
        
        int includedFiles = 0;
        long lastModifiedDate = 0;
        ArrayList lastModifiedFiles = new ArrayList();
        
        // Iterate filesets and get update the most recent 
        // modification date accordingly.
        for (Iterator fsIterator = this.fileSets.iterator(); fsIterator.hasNext(); ) {
            FileSet fs = (FileSet) fsIterator.next();

            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            File fromDir = fs.getDir(getProject());
            String [] srcFiles = ds.getIncludedFiles();

            for (int fIndex = 0; fIndex < srcFiles.length; fIndex++) {
                File file = fileUtils.resolveFile(fromDir, srcFiles[fIndex]);
                if (includedFiles == 0) {
                    lastModifiedDate = file.lastModified();
                } else {
                    if (lastModifiedDate == file.lastModified()) {
                        lastModifiedFiles.add( file );
                    } else if (lastModifiedDate < file.lastModified()) {
                        lastModifiedDate = file.lastModified();
                        lastModifiedFiles.clear();
                        lastModifiedFiles.add(file);
                    } 
                }
                includedFiles++;
            }
        }

        if (includedFiles == 0) {
            if (this.exceptionOnEmpty) {
                throw new BuildException("No files included in the fileset.");
            }
            log("No files included.", Project.MSG_INFO);
        } else {
            log("Last modified "
                + "[millis: " + lastModifiedDate + ", "
                + "date: " + new Date( lastModifiedDate )
                + "]: ", Project.MSG_VERBOSE);
    
            // set the property to the most recently modified file
            if (this.millisProperty != null) {
                getProject().setNewProperty(this.millisProperty,
                    Long.toString(lastModifiedDate));
            }
            if (this.dateProperty != null) {
                String out;
                if (this.format != null) {
                    out = format.getFormatted(lastModifiedDate);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    out = sdf.format(new Date(lastModifiedDate));
                }
                getProject().setNewProperty(this.dateProperty, out);
            }
            if (this.storeInField) {
                this.lastModified = lastModifiedDate;
            }
        }
	}
}
