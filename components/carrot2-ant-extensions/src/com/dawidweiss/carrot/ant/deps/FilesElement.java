
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A list of {@link FileElement}s provided by the component.
 */
class FilesElement implements Serializable {

	private final boolean buildPathExclude;
    private final List files = new LinkedList();

	public FilesElement(File base, Element configElement)
        throws Exception {

        String prefix = configElement.getAttribute("prefix");
        if (prefix == null) {
            prefix = "";
        }
        
        String buildPathExclude = configElement.getAttribute("build-path-exclude");
        this.buildPathExclude = Boolean.valueOf(buildPathExclude).booleanValue();

        NodeList list = configElement.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            switch (n.getNodeType()) {
                case Node.ELEMENT_NODE:
                    if ("file".equals(n.getNodeName())) {
                        files.add( new FileElement(base, prefix, (Element) n) );
                    } 
                    else
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

    public File [] getMissingFiles() {
        ArrayList l = new ArrayList();
        for (Iterator i = files.iterator(); i.hasNext(); ) {
            FileElement file = (FileElement) i.next();

            if (!file.getFileReference().getAbsoluteFile().exists())
                l.add(file.getFileReference().getAbsoluteFile());
        }
        File [] files = new File [ l.size() ];
        l.toArray(files);
        return files;
    }

	public boolean allFilesExist() {
        return getMissingFiles().length == 0;
	}

	public List getAllFileReferences(boolean buildPath) {
        if (this.buildPathExclude && buildPath)
            return Collections.EMPTY_LIST;

        FileReference [] files = new FileReference [this.files.size()];
        int j = 0;
        for (Iterator i = this.files.iterator(); i.hasNext(); ) {
            FileElement file = (FileElement) i.next();
            files[j] = file.getFileReference();
            j++;
        }
        return java.util.Arrays.asList(files);
	}

    /**
     * @return Returns the date of the most recently modified target file.
     */
    public long getMostRecentFileTimestamp() {
        long timestamp = Long.MIN_VALUE;

        for (Iterator i = files.iterator(); i.hasNext(); ) {
            FileElement file = (FileElement) i.next();
            File f = file.getFileReference().getAbsoluteFile();
            if (f.exists()) {
                timestamp = Math.max(f.lastModified(), timestamp);
            }
        }
        if (timestamp == Long.MIN_VALUE) {
            throw new RuntimeException("No files to check timestamps.");
        }
        return timestamp;
    }
}
