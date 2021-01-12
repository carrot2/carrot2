package com.carrotsearch.gradle.randomizedtesting

import com.carrotsearch.gradle.opts.Utilities
import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

/**
 * Plugin configuring root project extensions (globals)
 */
@CompileStatic
class RandomizedTestingRootPlugin implements Plugin<Project> {
  public static String RANDOMIZATION_INFO_TASK_NAME = "randomizationInfo"

  RootProjectGlobals globals

  @Override
  void apply(Project project) {
    if (project != project.rootProject) {
      throw new GradleException("This plugin can only be applied to the root project.")
    }

    def rootSeed = (String) Utilities.propertyOrDefault(
        project, TestOpts.PROP_TESTS_SEED, String.format("%08X", new Random().nextLong()))

    globals = new RootProjectGlobals(rootSeed, project)
    project.extensions.add(RootProjectGlobals.EXT_NAME, globals)

    def randomizationInfoTask = project.tasks.register(RANDOMIZATION_INFO_TASK_NAME, {task ->
      task.doFirst {
        task.logger.lifecycle("Root randomization seed: tests.seed=${globals.rootSeed}")
      }
    })
    project.allprojects*.tasks*.withType(Test) { Test task ->
      task.dependsOn randomizationInfoTask
    }
  }
}
