package com.carrotsearch.gradle.randomizedtesting

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.testing.Test

@CompileStatic
class RandomizedTestingExtension {
  final Project project

Spec<? extends Test> testTaskFilter
  TestOpts testOpts
  boolean failOnNoTests

  RandomizedTestingExtension(Project project) {
    this.project = project
    this.failOnNoTests = true
    this.testTaskFilter = { Test task -> return true }
    this.testOpts = new TestOpts(project)
  }

  void testOpts(Closure closure) {
    project.configure(testOpts, closure)
  }

  TaskCollection<Test> getTasks() {
    def tasks = project.tasks.withType(Test).matching(testTaskFilter)
    return tasks
  }
}
