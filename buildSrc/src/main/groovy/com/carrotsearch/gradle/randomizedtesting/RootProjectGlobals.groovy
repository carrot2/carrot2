package com.carrotsearch.gradle.randomizedtesting

import groovy.transform.CompileStatic
import org.gradle.api.tasks.testing.Test

@CompileStatic
class RootProjectGlobals {
  static String EXT_NAME = "_internal_randomizedtesting"

  String rootSeed
  Set<Test> allTestTasks = new HashSet<>()
}
