package com.carrotsearch.gradle.randomizedtesting

import groovy.transform.CompileStatic
import org.gradle.api.internal.tasks.testing.logging.DefaultTestLogging
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestLoggingContainer

import java.nio.file.Path

/**
 * Configure test output capturing and preset logging defaults.
 */
@CompileStatic
class HandleOutputs {
  final static String TEST_OUTPUTS_DIR = "test-outputs"
  final static String TEST_OUTPUTS_DIR_PROPERTY = "testOutputsDir"

  private final RandomizedTestingExtension conf

  HandleOutputs(RandomizedTestingExtension conf) {
    this.conf = conf
    apply()
  }

  private void apply() {
    def testTasks = conf.tasks

    // Set up error logging and a custom error stream redirector.
    testTasks.all { Test test ->
      TestLoggingContainer container = test.testLogging
      container.events()
      container.exceptionFormat = TestExceptionFormat.SHORT
      container.showExceptions = true
      container.showCauses = false
      container.showStackTraces = false
      container.stackTraceFilters.clear()
      container.showStandardStreams = false
    }

    def verboseModeProvider =
        conf.project.provider({-> Boolean.parseBoolean(conf.testOpts.verboseMode.toString())})

    // Set up custom output redirector.
    testTasks.all { Test test ->
      Path spillDir = test.getTemporaryDir().toPath()

      Path testOutputsDir = test.project.buildDir.toPath().resolve(TEST_OUTPUTS_DIR).resolve(test.name)
      test.extensions.extraProperties.set(TEST_OUTPUTS_DIR_PROPERTY, testOutputsDir.toAbsolutePath())

      test.doFirst {
        test.project.delete(testOutputsDir.toFile())
      }

      DefaultTestLogging logging = new DefaultTestLogging()
      logging.events(TestLogEvent.FAILED)
      logging.exceptionFormat = TestExceptionFormat.FULL
      logging.showExceptions = true
      logging.showCauses = true
      logging.showStackTraces = true
      logging.stackTraceFilters.clear()

      def listener = new ErrorReportingTestListener(logging, spillDir, testOutputsDir,
          verboseModeProvider)
      test.addTestOutputListener(listener)
      test.addTestListener(listener)
    }
  }
}
