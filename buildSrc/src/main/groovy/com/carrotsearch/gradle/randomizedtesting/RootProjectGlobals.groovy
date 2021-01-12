package com.carrotsearch.gradle.randomizedtesting

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.testing.Test

import java.util.concurrent.atomic.AtomicBoolean

@CompileStatic
class RootProjectGlobals {
  static String EXT_NAME = "_internal_randomizedtesting"

  final String rootSeed
  final Project rootProject

  AtomicBoolean failOnNoTestsHook = new AtomicBoolean(true)
  Set<Test> allTestTasks = new HashSet<>()

  AtomicBoolean verboseModeHook = new AtomicBoolean(true)
  private Provider<Boolean> verboseMode

  void setVerboseMode(Provider<Boolean> provider) {
    verboseMode = verboseMode.orElse(provider)
  }

  boolean isVerboseMode() {
    return verboseMode.get()
  }

  RootProjectGlobals(String rootSeed, Project rootProject) {
    this.rootProject = rootProject
    this.verboseMode = rootProject.provider { false }
    this.rootSeed = rootSeed
  }
}
