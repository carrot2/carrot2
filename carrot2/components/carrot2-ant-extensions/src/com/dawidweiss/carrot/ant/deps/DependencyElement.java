package com.dawidweiss.carrot.ant.deps;

import java.io.File;
import org.w3c.dom.Element;


public class DependencyElement {

    private String name;

    /**
     * The profile with which to consider including
     * this dependency.
     */
	private String profile;

	private File base;
    
    /** 
     *  If true, none of the files of this dependency, or its dependencies
     *  are copied by CopyDependency task. 
     */
    private boolean nocopy;
    
    /**
     * The profile with which to parse this dependency (in
     * other words, a profile applied to its sub-dependencies).
     */
    private String inprofile;

	/**
	 * @param file
	 * @param element
	 */
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
        if (name == null)
            throw new Exception("name attribute is required.");
        
        String nocopy = configElement.getAttribute("nocopy");
        if (nocopy == null || "false".equalsIgnoreCase(nocopy)) {
            this.nocopy = false;
        } else {
            this.nocopy = true;
        }
    }


	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getProfile() {
		return profile;
	}
    
    public String getInProfile() {
        return inprofile;
    }
    
    public boolean isNoCopy() {
        return this.nocopy;
    }

}
