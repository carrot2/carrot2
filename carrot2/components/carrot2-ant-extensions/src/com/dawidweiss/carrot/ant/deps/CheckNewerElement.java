package com.dawidweiss.carrot.ant.deps;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.dawidweiss.carrot.ant.MostRecentFileDate;

/**
 * A fileset to be checked against target files of a component.
 * If any of the fileset's files is newer then any of the target
 * files, build target is invoked. 
 */
public class CheckNewerElement {

    private File base;
    private ArrayList filesets = new ArrayList();
    private Project project;

	public CheckNewerElement(Project project, File base, Element configElement) throws Exception {
        this.base = base;
        this.project = project;
        
        FileUtils futils = FileUtils.newFileUtils();
        NodeList nlist = configElement.getChildNodes();
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
                        filesets.add(fs);
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
    }

    public long getMostRecentFileTimestamp() {
        MostRecentFileDate mrfd = new MostRecentFileDate();
        mrfd.setProject(project);
        mrfd.setStoreInField(true);
        mrfd.setExceptionOnEmpty(true);
        for (Iterator i = filesets.iterator(); i.hasNext();) {
            mrfd.addFileset((FileSet) i.next());
        }
        mrfd.execute();

        return mrfd.getLastModified();
    }
}
