package com.dawidweiss.carrot.ant.deps;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 */
public class ProvidesElement {

    private File base;
    private FilesElement files;
    private List builds = new LinkedList(); /* of BuildElement */
    private String profile;

	public ProvidesElement(Project project, File base, Element configElement) throws Exception {
        this.base = base;

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
                    } 
                    else if ("build".equals(n.getNodeName())) {
                        builds.add(new BuildElement(project, base, (Element) n));
                    } else
                        throw new SAXException("Unexpected node: "
                            + n.getNodeName());
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
        boolean force = false;

        if (files != null) {
            if (files.allFilesExist()) {
                force = false;
            } else {
                // not all files exist. rebuild.
                force = true;
                project.log("Component has missing files, REBUILDING.", Project.MSG_INFO);
            }
        } else {
            // no files. don't force builds, but
            // if such entries exist, perform them.
            force = false;
        }
        
        // make a rebuild.
        for (Iterator i = builds.iterator();i.hasNext();) {
            BuildElement build = (BuildElement) i.next();
            build.build( project, force, currentProfile );
        }
        
        // check if we have all the files now.
        if (files != null && false==files.allFilesExist()) {
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
    
	/**
	 * @return
	 */
	public String getProfile() {
		return profile;
	}


    /**
     * @param currentProfile
     * @return
     */
    public List getProvidedFileReferences(String currentProfile, boolean buildPath) {
        if (this.files != null) {
            return files.getAllFileReferences(buildPath);
        } else {
            return java.util.Collections.EMPTY_LIST;
        }
    }


	/**
	 * @param currentProfile
	 * @return
	 */
	public List getProvidedFiles(String currentProfile, boolean buildPath) {
        if (this.files != null) {
            List l = getProvidedFileReferences(currentProfile, buildPath);
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
