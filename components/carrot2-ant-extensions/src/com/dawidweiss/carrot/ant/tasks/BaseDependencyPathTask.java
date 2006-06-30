
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

package com.dawidweiss.carrot.ant.tasks;

import java.io.IOException;
import java.util.*;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import com.dawidweiss.carrot.ant.deps.ComponentDependency;
import com.dawidweiss.carrot.ant.types.DependencyPath;

/**
 * A {@link Task} that accepts <code>dependencies</code> subelement
 * with a path to <code>*.dep.xml</code> files. 
 * 
 * @author Dawid Weiss
 */
public class BaseDependencyPathTask extends Task {
    
    public BaseDependencyPathTask() {
    }

    /**
     * A list of <code>FileSet</code> objects
     * that point to component dependency descriptors.
     * This list is used when searching for named
     * dependencies. 
     */
    private LinkedList dependencies = new LinkedList();
    
    /**
     * Crates a new path with a set of dependency files to scan.
     */
    public Path createDependencies() {
        final Path newPath = new DependencyPath(getProject());
        dependencies.add(newPath);
        return newPath;
    }

    /**
     * Returns a map of <code>identifier</code>-{@link ComponentDependency} entries
     * corresponding to <code>*.dep.xml</code> files found in path.
     */
    public Map getAllComponents() throws IOException {
        final HashMap all = new HashMap();
        if (this.dependencies.size() == 0) {
            // do nothing, empty map is returned.
        } else if (this.dependencies.size() == 1) {
            all.putAll(((DependencyPath) dependencies.getFirst()).getAllComponents());
        } else {
            for (Iterator i = dependencies.iterator(); i.hasNext();) {
                final DependencyPath dep = (DependencyPath) i.next();
                final HashMap comps = dep.getAllComponents();
                for (Iterator j = comps.values().iterator(); j.hasNext();) {
                    Utils.addComponentToMap((ComponentDependency) j.next(), all);
                }
            }
        }
        return all;
    }
}
