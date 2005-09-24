package com.dawidweiss.carrot.ant.deps;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BuildElement {
    private List execs = new ArrayList();

	public BuildElement(File base, Element configElement) 
        throws Exception {
        
        // configure
        NodeList list = configElement.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            switch (n.getNodeType()) {
                case Node.ELEMENT_NODE:
                    if ("ant".equals(n.getNodeName())) {
                        execs.add(new AntBuildElement(base, (Element) n));
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
    
	/**
     * Perform the build.
	 */
	public void build(Project project, String profile) throws BuildException {
        for (Iterator i = execs.iterator();i.hasNext();) {
            BuildTask task = (BuildTask) i.next();
            task.execute(project, profile);
        }
	}
}
