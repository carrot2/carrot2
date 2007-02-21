
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.ant.deps;

import java.io.File;

import org.apache.tools.ant.util.FileUtils;

/**
 * A file reference resolved from {@link FileElement}.
 */
public class FileReference {
    private File absolute;
    private String relative;
    private File base;

    public FileReference(File base, String relative) {
        this(base, new File( base, relative));
    }

	public FileReference(File base, File file) 
        throws IllegalArgumentException {
        final FileUtils futils = FileUtils.getFileUtils(); 
        if (!base.isAbsolute()) {
            throw new IllegalArgumentException("Base must be an" +
                " absolute reference: " + base.getPath());
        }
        String stripped = futils.removeLeadingPath(base, file);
        if (new File(stripped).isAbsolute()) {
            throw new IllegalArgumentException("File "
                + base.getAbsolutePath() + " cannot be " +
                    "a leading path of: " + file.getAbsolutePath());
        }
        
        this.base = base;
        this.relative = stripped;
		this.absolute = futils.resolveFile(base, relative);
	}

    public File getAbsoluteFile() {
        return absolute;
    }
    
    public String getRelative() {
        return this.relative;
    }
    
    public File getBase() {
        return this.base;
    }

	public boolean equals(Object obj) {
		if (obj instanceof FileReference) {
            return  ((FileReference) obj).getAbsoluteFile().equals(this.getAbsoluteFile());
        }
        return false;
	}

	/* 
	 */
	public int hashCode() {
		return this.absolute.hashCode();
	}

}
