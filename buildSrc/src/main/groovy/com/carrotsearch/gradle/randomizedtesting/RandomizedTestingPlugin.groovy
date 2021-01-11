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

  @Override
  void apply(Project project) {
    if (GradleVersion.current() < GradleVersion.version("6.2")) {
      project.logger.error("Requires Gradle >= 6.2")
    }

    if (project != project.rootProject) {
      project.rootProject.plugins.apply(RandomizedTestingRootPlugin)
    }

    def ext = new RandomizedTestingExtension(project)
    project.extensions.add(EXTENSION_NAME, ext)

    // Disable HTML report generation. The reports are big and slow to generate.
    def testTasks = ext.tasks
    testTasks*.reports.html.enabled = false

    new GlobalFailOnNoTests(project).apply()
    new HandleOutputs(project).apply()
  }
}
