package com.carrotsearch.gradle.opts

import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Provider

import java.util.function.Consumer

@CompileStatic
class Option {
  static def UNRESOLVED = new Object()

  String name
  String description
  private def defaultValue
  private def otherProperties = [:]

  private def value = UNRESOLVED
  private def valueSource
  OptsPluginExtension ext

  def propertyMissing(String name, value) { otherProperties[name] = value }
  def propertyMissing(String name) { otherProperties[name] }

  void setValue(Object v) {
    this.defaultValue = v
  }

  Object getValue() {
    return resolve()
  }

  private Project getProject() {
    def project = ext.project
    if (project == null) {
      throw new GradleException("Option not bound to any project: ${name}")
    }
    return project
  }

  Object resolve() {
    if (value == UNRESOLVED) {
      if (project.hasProperty(name)) {
        value = project.property(name)
        valueSource = "project property"
      } else if (System.properties.containsKey(name)) {
        value = System.properties.get(name)
        valueSource = "system property"
      } else if (defaultValue instanceof Closure) {
        value = defaultValue.call()
        valueSource = "closure"
      } else {
        value = defaultValue
        valueSource = "default value"
      }
    }
    return value
  }

  Object acceptIfNotNull(Consumer<Object> consumer) {
    def value = resolve()
    if (value != null) {
      consumer.accept(value)
    }
    return value
  }

  @Override
  String toString() {
    return getValue()
  }

  boolean isDynamic() {
    return defaultValue instanceof Closure
  }

  boolean isEqualsDefault() {
    return Objects.equals(resolve(), defaultValue)
  }

  String toInfoString() {
    return String.format(Locale.ROOT,
        "%s%-24s = %-8s # %s%s",
        equalsDefault ? "  " : "! ",
        name,
        value,
        equalsDefault
            ? ""
            : "(source: ${valueSource}${dynamic ? '' : ", default: " + defaultValue}) ",
        description)
  }

  Provider<Object> asProvider() {
    return project.provider({-> value })
  }
}
