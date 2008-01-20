
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

package org.carrot2.ant.tasks;

import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.carrot2.ant.types.DependencyPath;


/**
 * Used to reference (and thus instantiate) a {@link DependencyPath}.
 * 
 * @author Dawid Weiss
 */
public class LoadDependencyPath extends BaseDependencyPathTask {
    public void execute() {
        try {
            this.getAllComponents();
        } catch (IOException e) {
            throw new BuildException("Could not load dependency path.", e);
        }
    }
}
