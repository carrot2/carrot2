package com.carrotsearch.gradle.opts

import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.Project

/**
 * Extension methods.
 */
@CompileStatic
class OptsPluginExtension implements Iterable<Option> {
  final Project project
  private final TreeMap<String, Option> options = new TreeMap<>()

  OptsPluginExtension(Project project) {
    this.project = project
  }

  Option option(Map opts) {
    opts = ["project": project, *:opts]
    return option(opts as Option)
  }

  Option option(Option opt) {
    if (options.containsKey(opt.name)) {
      throw new GradleException("Option already exists on project '${project.path}': ${opt.name}")
    }

    options.put(opt.name, opt)
    return opt
  }

  @Override
  Iterator<Option> iterator() {
    return options.values().iterator()
  }

  def propertyMissing(String name) {
    if (options.containsKey(name)) {
      return options[name]
    } else {
      throw new GradleException("No option named: ${name}")
    }
  }
}