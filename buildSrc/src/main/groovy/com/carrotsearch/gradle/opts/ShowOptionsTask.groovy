package com.carrotsearch.gradle.opts

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

@CompileStatic
class ShowOptionsTask extends DefaultTask {
  static String NAME = "showOpts"

  @TaskAction
  void exec() {
    def opts = project.extensions.findByName(OptsPlugin.OPTIONS_EXTENSION_NAME) as OptsPluginExtension

    project.logger.lifecycle("Configurable options in ${project == project.rootProject ? "root project" : project.path}:\n"
        + opts.collect { it.toInfoString() }.join("\n"))
  }
}
