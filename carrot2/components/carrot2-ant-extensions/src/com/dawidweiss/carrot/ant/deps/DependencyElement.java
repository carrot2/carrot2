
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

package com.dawidweiss.carrot.ant.deps;

import java.io.File;
import org.w3c.dom.Element;


/**
 * A class which represents the <code>dependency</code>
 * element in a <code>.dep.xml</code> file.
 * 
 * @author Dawid Weiss
 */
class DependencyElement {

    /**
     * Name of the dependent component.
     */
    private String name;

    /**
     * A dependency is parsed only if the current profile matches this
     * field.
     */
	private String profile;
    
    /**
     * Each {@link ComponentDependency} may have an associated profile.
     * This field indicates which profile we need from the dependent
     * component. 
     */
    private String inprofile;

    /**
     * Base for resolving relative files.
     */
	private final File base;

    /** 
     * If <code>true</code>, none of the files of this dependency,
     * or its dependencies are copied by 
     * {@link com.dawidweiss.carrot.ant.CopyDependencies} task. 
     */
    private final boolean nocopy;

	public DependencyElement(File file, Element configElement) 
        throws Exception {
        this.base = file;

        this.profile = configElement.getAttribute("profile");
        if (profile != null && "".equals(profile.trim()))
            profile = null;

        this.inprofile = configElement.getAttribute("in-profile");
        if (inprofile != null && "".equals(inprofile.trim()))
            inprofile = null;
        
        this.name = configElement.getAttribute("name");
        if (name == null || "".equals(name))
            throw new Exception("name attribute is required.");
        
        String nocopy = configElement.getAttribute("nocopy");
        if (nocopy != null && Boolean.valueOf(nocopy).booleanValue()) {
            this.nocopy = true;
        } else {
            this.nocopy = false;
        }
    }

	public String getName() {
		return name;
	}

	public String getProfile() {
		return profile;
	}
    
    public String getInProfile() {
        return inprofile;
    }
    
    public boolean isNoCopy() {
        return this.nocopy;
    }

    public String toString() {
	    return "[dependency name=" + name + " profile=" + profile + " base="
	    + base.getAbsolutePath() + " inprofile=" + inprofile + " nocopy=" + nocopy + "]";
    }
}
