
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 */
class ProvidesElement {

    private FilesElement files;
    private ArrayList builds = new ArrayList();
    private ArrayList conditions = new ArrayList(); 
    private String profile;
    private ComponentDependency component;

	public ProvidesElement(Project project, File base, Element configElement, ComponentDependency component) throws Exception {
        this.component = component;

        this.profile = configElement.getAttribute("profile");
        if (profile != null && "".equals(profile.trim()))
            profile = null;

        // configure
        NodeList list = configElement.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            switch (n.getNodeType()) {
                case Node.ELEMENT_NODE:
                    if ("files".equals(n.getNodeName())) {
                        this.files = new FilesElement(base, (Element) n);
                    } else if ("build".equals(n.getNodeName())) {
                        builds.add(new BuildElement(base, (Element) n));
                    } else if ("check-newer".equals(n.getNodeName())) {
                        conditions.add(new CheckNewerElement(project, base, (Element) n));
                    } else {
                        throw new SAXException("Unexpected node: "
                            + n.getNodeName());
                    }
                break;
                case Node.TEXT_NODE:
                    if (!n.getNodeValue().trim().equals("")) {
                        throw new SAXException("Unexpected text in 'component' node.");
                    }
                break;
                case Node.COMMENT_NODE:
                    continue;
                default:
                    throw new SAXException("Unexpected node: "
                        + n.getNodeName());
            }
        }
	}
    
    
	/**
	 * Brings the provided files to date using 'build' element,
     * if it exists. 
	 */
	public void bringUpToDate(Project project, String currentProfile) throws BuildException {
        boolean rebuild = false;

        if (files != null) {
            if (files.allFilesExist() == false) {
                // not all files exist. rebuild.
                project.log("Component [" + component.getName() + "] has missing files, rebuilding.", Project.MSG_INFO);
                rebuild = true;
            } else {
                for (Iterator i = conditions.iterator(); i.hasNext();) {
                    Object o = i.next();
                    if (o instanceof CheckNewerElement) {
                        // Check the most recently modified file.
                        long sourceTimestamp = ((CheckNewerElement) o).getMostRecentFileTimestamp();
                        long targetTimestamp = files.getMostRecentFileTimestamp();
                        if (sourceTimestamp > targetTimestamp) {
                            project.log("Component [" + component.getName() + "] needs to be rebuilt (timestamps source: "
                                    + new Date(sourceTimestamp) + ", target: " + new Date(targetTimestamp) + "), rebuilding.");
                            rebuild = true;
                            break;
                        }
                    } else {
                        throw new BuildException("Unknown condition: " + o.getClass());
                    }
                }
            }
        } else {
            // Component provides no files. Perform builds unconditionally.
            rebuild = true;
        }
        
        // make a rebuild.
        if (rebuild) {
	        for (Iterator i = builds.iterator(); i.hasNext();) {
	            BuildElement build = (BuildElement) i.next();
	            build.build(project, currentProfile);
	        }
        }
        
        // check if we have all the files now.
        if (files != null && false == files.allFilesExist()) {
            File [] missingFiles = files.getMissingFiles();
            StringBuffer buf = new StringBuffer();

            buf.append("Not all files are available after build. " +
                "Incorrect dependency file specification? Missing files: \n");
            for (int i=0;i<missingFiles.length;i++) {
                buf.append( missingFiles[i].getAbsolutePath() );
                buf.append("\n");
            }
            project.log(buf.toString(), Project.MSG_ERR);
            throw new BuildException(buf.toString());
        }
	}
    
	public String getProfile() {
		return profile;
	}

    public List getProvidedFileReferences(boolean buildPath) {
        if (this.files != null) {
            return files.getAllFileReferences(buildPath);
        } else {
            return java.util.Collections.EMPTY_LIST;
        }
    }

	public List getProvidedFiles(boolean buildPath) {
        if (this.files != null) {
            List l = getProvidedFileReferences(buildPath);
            List nl = new ArrayList( l.size() );
            for (Iterator i = l.iterator(); i.hasNext();) {
                nl.add( ((FileReference) i.next()).getAbsoluteFile());
            }
            return nl;
        } else {
            return java.util.Collections.EMPTY_LIST;
        }
	}
}