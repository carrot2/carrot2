package com.dawidweiss.carrot.ant.deps;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Element;

/**
 */
public class WhenNewerInElement {

    private File base;
    private File dir;

	public WhenNewerInElement(Project project, File base, Element configElement) throws Exception {
        this.base = base;

        if (configElement.getAttribute("dir") == null)
            throw new Exception("dir attribute expected.");

        String dir = configElement.getAttribute("dir");
        FileUtils futils = FileUtils.newFileUtils();
        this.dir = futils.resolveFile(base, dir);

        project.log("Directory does not exist: "
            + this.dir.getAbsolutePath(), Project.MSG_WARN);
    }

}
