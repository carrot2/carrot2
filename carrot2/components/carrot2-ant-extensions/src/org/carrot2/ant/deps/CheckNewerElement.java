
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

package org.carrot2.ant.deps;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;
import org.carrot2.ant.tasks.MostRecentFileDateTask;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


/**
 * A fileset to be checked against target files of a component.
 * If any of the fileset's files is newer then any of the target
 * files, build target is invoked. 
 */
class CheckNewerElement implements Serializable {
    final ArrayList files = new ArrayList();
    private long lastModified;

	public CheckNewerElement(Project project, File base, Element configElement) throws Exception {
        final FileUtils futils = FileUtils.newFileUtils();
        final NodeList nlist = configElement.getChildNodes();
        final MostRecentFileDateTask mrfd = new MostRecentFileDateTask();
        mrfd.setProject(project);
        mrfd.setStoreInField(true);
        mrfd.setExceptionOnEmpty(true);
        
        for (int i = 0; i < nlist.getLength(); i++) {
            Node n = nlist.item(i);
            switch (n.getNodeType()) {
                case Node.ELEMENT_NODE:
                    if ("fileset".equals(n.getNodeName())) {
                        FileSet fs = (FileSet) project.createDataType("fileset");
                        fs.setDefaultexcludes(true);
                        
                        String dir = ((Element) n).getAttribute("dir");
                        File dirFile = futils.resolveFile(base, dir);
                        if (!dirFile.exists()) {
                            project.log("Directory does not exist: "
                                    + dirFile.getAbsolutePath(), Project.MSG_WARN);
                        }
                        fs.setDir(dirFile);
                        mrfd.addFileset(fs);
                    } else {
                        throw new BuildException("Undefined node name: " + n.getNodeName());
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
        mrfd.execute();
        this.lastModified = mrfd.getLastModified();
    }

    public long getMostRecentFileTimestamp() {
        return lastModified;
    }
}
