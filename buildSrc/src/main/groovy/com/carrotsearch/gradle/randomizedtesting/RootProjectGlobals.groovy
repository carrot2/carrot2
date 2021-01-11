package com.carrotsearch.gradle.randomizedtesting

import groovy.transform.CompileStatic
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test

import java.util.concurrent.atomic.AtomicBoolean

@CompileStatic
class RootProjectGlobals {
  static String EXT_NAME = "_internal_randomizedtesting"

  String rootSeed

  AtomicBoolean failOnNoTestsHook = new AtomicBoolean(true)
  Set<Test> allTestTasks = new HashSet<>()
}
