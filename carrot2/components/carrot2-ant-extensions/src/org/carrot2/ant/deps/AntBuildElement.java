
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
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
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.util.FileUtils;
import org.carrot2.ant.tasks.BringToDateTask;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * An build trigger for a component representing an ANT file. 
 * 
 * @author Dawid Weiss
 */
class AntBuildElement implements BuildTask, Serializable {

    private String target;
	private String file;
	private File base;

	public AntBuildElement(File base, Element configElement) throws SAXException {
        this.base = base;

        // configure
        this.file = configElement.getAttribute("file");
        this.target = configElement.getAttribute("target");

        if (file == null)
            throw new SAXException("file attribute is required.");
    }

	public void execute(Project project, String profile, BringToDateTask toDateTask) {
        final Ant task = (Ant) project.createTask("ant");
        if (task == null) {
            throw new BuildException("'ant' task must be available.");
        }
        
        final FileUtils futils = FileUtils.getFileUtils();
        final File buildFile = futils.resolveFile(base, file);
        task.setAntfile(buildFile.getAbsolutePath());
        task.setDir(buildFile.getParentFile());
        task.setInheritAll(false);
        
        final List overrides = toDateTask.getOverrides();
        if (overrides.size() > 0) {
            for (Iterator i = overrides.iterator(); i.hasNext();) {
                final BringToDateTask.Override override = (BringToDateTask.Override) i.next();
                override.doOverride(task, project);
            }
        }
        
        task.setProject(project);
        if (this.target != null && !"".equals(target))
            task.setTarget(target);

        if (profile != null) {
            Property p = task.createProperty();
            p.setName("com.dawidweiss.carrot.ant.deps.profile");
            p.setValue(profile);
        }

        task.execute();
	}
}
