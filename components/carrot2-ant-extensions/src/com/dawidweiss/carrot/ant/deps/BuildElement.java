package com.dawidweiss.carrot.ant.deps;

import java.io.File;
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
public class BuildElement {

	private File base;
    private List execs = new LinkedList();
    private List conditions = new LinkedList();

	/**
	 * @param base
	 * @param element
	 */
	public BuildElement(Project project, File base, Element configElement) 
        throws Exception {
        this.base = base;
        
        // configure
        NodeList list = configElement.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            switch (n.getNodeType()) {
                case Node.ELEMENT_NODE:
                    if ("ant".equals(n.getNodeName())) {
                        execs.add( new AntBuildElement(project, base, (Element) n) );
                    }
                    else if ("when-newer-in".equals(n.getNodeName())) {
                        conditions.add( new WhenNewerInElement( project, base, (Element) n));
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
     * 
	 * @param force Force build, even if other conditions would indicate
     *              it is not necessary.
	 */
	public void build(Project project, boolean force, String profile) throws BuildException {
        boolean doBuild = force;
        if (doBuild==false) {
            if (conditions.size() > 0) {
                throw new BuildException("Conditions not implemented yet.");
            }
        }
        if (doBuild) {
            for (Iterator i = execs.iterator();i.hasNext();) {
                BuildTask task = (BuildTask) i.next();
                task.execute(project, profile);
            }
        }
	}

}
