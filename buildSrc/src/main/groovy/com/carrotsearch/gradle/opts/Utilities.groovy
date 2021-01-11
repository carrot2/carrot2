package com.carrotsearch.gradle.opts

import groovy.transform.CompileStatic
import org.gradle.api.Project

@CompileStatic
class Utilities {
  // Project property, system property or default value (result of a closure call, if it's a closure).
  static Object propertyOrDefault(Project project, String propName, Object defValue) {
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
  static Object propertyOrEnvOrDefault(Project project, String propName, String envName, Object defValue) {
    return propertyOrDefault(project, propName, envOrDefault(envName, defValue));
  }

  // System environment variable or default.
  static Object envOrDefault(String envName, Object defValue) {
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
}
