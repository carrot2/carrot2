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
    option(opts as Option)
  }

  Option option(Option opt) {
    if (opt.ext != null) {
      throw new GradleException("Option already bound to a different project: ${opt.name}")
    }
    opt.ext = this
    if (options.containsKey(opt.name)) {
      throw new GradleException("Option already exists on project '${project.path}': ${opt.name}")
    }

    options.put(opt.name, opt)
    return opt
  }

  // Project property, system property or default value (result of a closure call, if it's a closure).
  Object optOrDefault(String propName, Object defValue) {
    def result
    if (project.hasProperty(propName)) {
      result = project.property(propName)
    } else if (System.properties.containsKey(propName)) {
      result = System.properties.get(propName)
    } else {
      result = closureOrValue(defValue)
    }
    return result
  }

  // Either a project, system property, environment variable or default value.
  Object optOrEnvOrDefault(String propName, String envName, Object defValue) {
    return optOrDefault(propName, envOrDefault(envName, defValue));
  }

  // System environment variable or default.
  Object envOrDefault(String envName, Object defValue) {
    def result = System.getenv(envName)
    if (result != null) {
      return result
    } else {
      return closureOrValue(defValue)
    }
  }

  static Object closureOrValue(Object value) {
    if (value instanceof Closure) {
      return value.call()
    } else {
      return value
    }
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