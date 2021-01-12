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

    def normal = Style.Normal
    def computed = Style.Identifier
    def overridden = Style.FailureHeader
    def comment = Style.ProgressStatus

    int keyWidth = 1
    opts.each {opt -> keyWidth = Math.max(opt.name.length(), keyWidth) }

    StyledTextOutput out = getServices().get(StyledTextOutputFactory).create(this.getClass())

    out.format("Configurable options in %s", project == project.rootProject ? "root project" : project.path)
    out.append(" [option colors: ").style(computed).append("computed")
        .style(normal).append(", ")
        .style(overridden).append("locally overridden")
        .style(normal).append("]:\n\n")

    opts.each {opt ->
      def valueStyle = normal
      if (!opt.equalsDefault) {
        valueStyle = opt.dynamic ? computed : overridden
      }

      out.format("%-${keyWidth}s = ", opt.name)
      out.style(valueStyle)
      out.format("%-8s", opt.value)
      out.style(comment)
      out.format(" # ")
      if (!opt.equalsDefault) {
        out.style(valueStyle)
        out.format("(source: ${opt.resolvedValueSource}${opt.dynamic ? '' : ", default: " + opt.defaultValue}) ")
        out.style(comment)
      }
      out.format(opt.description)
      out.style(normal)
      out.println()
    }
  }
}
