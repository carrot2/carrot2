package com.carrotsearch.gradle.opts

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.logging.text.StyledTextOutput.Style
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.internal.logging.text.StyledTextOutputFactory

@CompileStatic
class ShowOptionsTask extends DefaultTask {
  static String NAME = "showOpts"

  @TaskAction
  void exec() {
    def opts = project.extensions.findByName(OptsPlugin.OPTIONS_EXTENSION_NAME) as OptsPluginExtension

    def sourceStyle = Style.Identifier
    def notDefaultStyle = Style.FailureHeader
    def normal = Style.Normal
    def comment = Style.ProgressStatus

    StyledTextOutput out = getServices().get(StyledTextOutputFactory).create(this.getClass())

    out.println("Configurable options in ${project == project.rootProject ? "root project" : project.path}:\n\n")
    opts.each {opt ->
      out.style(notDefaultStyle).format("%s", opt.equalsDefault ? "  " : "! ").style(normal)
      out.format("%-24s = %-8s", opt.name, opt.value)
      out.style(comment)
      out.format(" # ")
      if (!opt.equalsDefault) {
        out.style(sourceStyle)
        out.format("(source: ${opt.resolvedValueSource}${opt.dynamic ? '' : ", default: " + opt.defaultValue}) ")
        out.style(comment)
      }
      out.format(opt.description)
      out.println()
    }
  }
}
