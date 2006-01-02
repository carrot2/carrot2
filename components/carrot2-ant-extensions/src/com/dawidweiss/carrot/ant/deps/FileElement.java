
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.ant.deps;

import java.io.File;

import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Element;

/**
 */
public class FileElement {

	private File base;
	private File file;
    private static FileUtils futils = FileUtils.newFileUtils();

    public FileElement(File base, String localPrefix, Element configElement) 
     throws Exception {
        String location = configElement.getAttribute( "location" );
        if (location == null)
            throw new Exception("location attribute required.");
        this.base = futils.resolveFile(base, localPrefix);
        this.file = new File(this.base, location);
    }

	public FileReference getFileReference() {
        return new FileReference(base, file);
	}
}
