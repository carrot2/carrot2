package com.carrotsearch.gradle.randomizedtesting


import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion

/**
 * Infrastructure for setting up randomized testing.
 */
@CompileStatic
class RandomizedTestingPlugin implements Plugin<Project> {
  static String EXTENSION_NAME = "randomizedtesting"

  RootProjectGlobals globals

  @Override
  void apply(Project project) {
    if (GradleVersion.current() < GradleVersion.version("6.2")) {
      project.logger.error("Requires Gradle >= 6.2")
    }

    def ext = new RandomizedTestingExtension(project)
    project.extensions.add(EXTENSION_NAME, ext)

    // Disable HTML report generation. The reports are big and slow to generate.
    def testTasks = ext.tasks
    testTasks*.reports.html.enabled = false

    def failOnNoTests = new GlobalFailOnNoTests(ext)
    def handleOutputs = new HandleOutputs(ext)

    globals = project.rootProject.extensions.findByName(RootProjectGlobals.EXT_NAME) as RootProjectGlobals
    if (globals == null) {
      globals = new RootProjectGlobals()
      globals.rootSeed = String.format("%08X", new Random().nextLong())
      project.rootProject.extensions.add(RootProjectGlobals.EXT_NAME, globals)

      failOnNoTests.registerRootGlobals(globals)
    }
  }
}
