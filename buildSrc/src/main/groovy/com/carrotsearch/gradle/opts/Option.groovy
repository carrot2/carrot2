package com.carrotsearch.gradle.opts

import groovy.transform.CompileStatic
import groovy.transform.MapConstructor
import org.gradle.api.Project

import java.util.function.Consumer

@CompileStatic
@MapConstructor
class Option {
  static def UNRESOLVED = new Object()

  final Project project
  final String name
  final String description
  Object defaultValue

  Option(Project project, String name, String description = "", Object defaultValue = null) {
    this.project = project
    this.name = name
    this.description = description
    this.defaultValue = defaultValue
  }

  private def otherProperties = [:]

  private def resolvedValue = UNRESOLVED
  private def resolvedValueSource

  def propertyMissing(String name, value) { otherProperties[name] = value }
  def propertyMissing(String name) { otherProperties[name] }

  Object getValue() {
    return resolve()
  }

  private Object resolve() {
    if (resolvedValue == UNRESOLVED) {
      if (project.hasProperty(name)) {
        resolvedValue = project.property(name)
        resolvedValueSource = "project property"
      } else if (System.properties.containsKey(name)) {
        resolvedValue = System.properties.get(name)
        resolvedValueSource = "system property"
      } else if (defaultValue instanceof Closure) {
        resolvedValue = defaultValue.call()
        resolvedValueSource = "closure"
      } else {
        resolvedValue = defaultValue
        resolvedValueSource = "default value"
      }
    }
    return resolvedValue
  }

  Object acceptIfNotNull(Consumer<Object> consumer) {
    if (value != null) {
      consumer.accept(value)
    }
    return value
  }

  @Override
  String toString() {
    return Objects.toString(value)
  }

  boolean isDynamic() {
    return defaultValue instanceof Closure
  }

  boolean isEqualsDefault() {
    return Objects.equals(value, defaultValue)
  }

  String getResolvedValueSource() {
    resolve()
    return resolvedValueSource
  }

  String toInfoString() {
    return String.format(Locale.ROOT,
        "%s%-24s = %-8s # %s%s",
        equalsDefault ? "  " : "! ",
        name,
        value,
        equalsDefault
            ? ""
            : "(source: ${resolvedValueSource}${dynamic ? '' : ", default: " + defaultValue}) ",
        description)
  }
}
