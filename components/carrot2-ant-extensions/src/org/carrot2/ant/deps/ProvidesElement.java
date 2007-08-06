
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
import java.io.Serializable;
import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.carrot2.ant.tasks.BringToDateTask;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


/**
 * A component can provide a set of files in a given profile (or in the default
 * profile). Each set of provided files can also have a set of 
 * conditions that trigger builds and meta information snippets.
 */
class ProvidesElement implements Serializable {
    /**
     * Meta information snippet in a <code>meta</code> tag inside
     * a <code>provides</code> element. 
     */
    final static class Meta implements Serializable {
        final String type;
        final String content;
        public Meta(String type, String content) {
            this.type = type;
            this.content = content;
        }
    }

    private ArrayList files = new ArrayList();
    private ArrayList builds = new ArrayList();
    private ArrayList conditions = new ArrayList();
    private ArrayList metas = new ArrayList();
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
                        this.files.add(new FilesElement(base, (Element) n));
                    } else if ("build".equals(n.getNodeName())) {
                        builds.add(new BuildElement(base, (Element) n));
                    } else if ("check-newer".equals(n.getNodeName())) {
                        conditions.add(new CheckNewerElement(project, base, (Element) n));
                    } else if ("rebuild-always".equals(n.getNodeName())) {
                        conditions.add(new RebuildAlways());
                    } else if ("meta".equals(n.getNodeName())) {
                        final Element e = (Element) n;
                        final String type = e.getAttribute("type");
                        final String value = getTextContent(e);
                        metas.add(new Meta(type, value));
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
     * Collect text from a node.
     */
	public static String getTextContent(Element e) {
        final StringBuffer buf = new StringBuffer();
        final NodeList nl = e.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            final Node n = nl.item(i);
            switch (n.getNodeType()) {
                case Node.ELEMENT_NODE:
                    buf.append(getTextContent((Element) n));
                    break;
                case Node.TEXT_NODE:
                case Node.CDATA_SECTION_NODE:
                    buf.append(n.getNodeValue());
                    break;
                case Node.COMMENT_NODE:
                case Node.PROCESSING_INSTRUCTION_NODE:
                    continue;
                default:
                    throw new RuntimeException("Unexpected node: " + n.getNodeName());
            }
        }

        return buf.toString();
    }

    /**
	 * Brings the provided files to date using 'build' element,
     * if it exists. 
	 */
	public void bringUpToDate(Project project, String currentProfile, BringToDateTask task) throws BuildException {
        boolean rebuild = false;

        if (files != null) {
            if (allFilesExist() == false) {
                // not all files exist. rebuild.
                project.log("Component [" + component.getName() + "] has missing files, rebuilding.", Project.MSG_INFO);
                rebuild = true;
            } else {
                long targetTimestamp = Long.MIN_VALUE;
                for (int i = 0; i < this.files.size(); i++) {
                    final FilesElement felem = (FilesElement) files.get(i);
                    targetTimestamp = Math.max(targetTimestamp, felem.getMostRecentFileTimestamp());
                }

                for (Iterator i = conditions.iterator(); i.hasNext();) {
                    Object o = i.next();
                    if (o instanceof CheckNewerElement) {
                        // Check the most recently modified file.
                        long sourceTimestamp = ((CheckNewerElement) o).getMostRecentFileTimestamp();
                        if (sourceTimestamp > targetTimestamp) {
                            project.log("Component [" + component.getName() + "] needs to be rebuilt (timestamps source: "
                                    + new Date(sourceTimestamp) + ", target: " + new Date(targetTimestamp) + "), rebuilding.");
                            rebuild = true;
                            break;
                        }
                    } else if (o instanceof RebuildAlways) {
                        project.log("Component [" + component.getName() + "] needs to be rebuilt (forced), rebuilding.");
                        rebuild = true;
                        break;
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
	            build.build(project, currentProfile, task);
	        }
        }
        
        // check if we have all the files now.
        if (files != null && false == allFilesExist()) {
            File [] missingFiles = getMissingFiles();
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
     * Returns <code>true</code> if all files in this provides element exist.
     */
	private boolean allFilesExist() {
        if (this.files != null) {
            for (int i = 0; i < this.files.size(); i++) {
                final FilesElement felem = (FilesElement) files.get(i);
                if (felem.allFilesExist() == false) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a list of missing files.
     */
    private File[] getMissingFiles() {
        final ArrayList files = new ArrayList();

        if (this.files != null) {
            for (int i = 0; i < this.files.size(); i++) {
                final FilesElement felem = (FilesElement) this.files.get(i);
                if (felem.allFilesExist() == false) {
                    files.addAll(Arrays.asList(felem.getMissingFiles()));
                }
            }
        }

        return (File []) files.toArray(new File[files.size()]);
    }

    public String getProfile() {
		return profile;
	}

    public List getProvidedFileReferences(boolean buildPath) {
        final ArrayList fileList = new ArrayList();
        if (this.files != null) {
            for (int i = 0; i < this.files.size(); i++) {
                final FilesElement felem = (FilesElement) files.get(i);
                fileList.addAll(felem.getAllFileReferences(buildPath));
            }
            return fileList;
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

    void collectMetas(List outputMetas, String type) {
        for (int i = 0; i < this.metas.size(); i++) {
            final Meta m = (Meta) this.metas.get(i);
            if (m.type.equals(type)) {
                outputMetas.add(m.content);
            }
        }
    }
}