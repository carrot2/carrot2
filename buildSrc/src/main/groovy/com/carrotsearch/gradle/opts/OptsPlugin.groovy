package com.carrotsearch.gradle.opts

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion

/**
 * Configurable options plugin.
 */
@CompileStatic
class OptsPlugin implements Plugin<Project> {
  static String OPTIONS_EXTENSION_NAME = "opts"

  @Override
  void apply(Project project) {
    if (GradleVersion.current() < GradleVersion.version("6.2")) {
      project.logger.error("Requires Gradle >= 6.2")
    }

    project.extensions.add(OPTIONS_EXTENSION_NAME, new OptsPluginExtension(project))

    project.tasks.register(ShowOptionsTask.NAME, ShowOptionsTask.class)
  }
}
