package com.dawidweiss.carrot.ant.deps;

import org.apache.tools.ant.Project;

/**
 */
public interface BuildTask {

	void execute(Project project, String profile);

}
