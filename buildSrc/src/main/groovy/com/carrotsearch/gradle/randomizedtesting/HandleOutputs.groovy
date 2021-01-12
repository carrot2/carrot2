package com.carrotsearch.gradle.randomizedtesting

import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.internal.tasks.testing.logging.DefaultTestLogging
import org.gradle.api.provider.Provider
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

  private final Project project
  private final RandomizedTestingExtension conf

  HandleOutputs(Project project) {
    this.project = project
    this.conf = project.extensions.findByType(RandomizedTestingExtension)
  }

  void apply() {
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

    Provider<Boolean> verboseModeProvider =
        project.provider({-> Boolean.parseBoolean(conf.testOpts.verboseMode.toString())})

    RootProjectGlobals globals = project.rootProject.extensions.findByType(RootProjectGlobals)
    globals.setVerboseMode(verboseModeProvider)
    if (globals.verboseModeHook.getAndSet(false)) {
      installGlobalHook(globals)
    }

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

      def listener = new ErrorReportingTestListener(logging, spillDir, testOutputsDir, verboseModeProvider)
      test.addTestOutputListener(listener)
      test.addTestListener(listener)
    }
  }

  /**
   * If we're running in verbose mode and:
   * <ul>
   * <li>worker count > 1
   * <li>number of 'test' tasks in the build is &gt; 1
   * </ul>
   *
   * then the output would very likely be mangled on the
   * console. Fail and let the user know what to do.
   */
  static void installGlobalHook(RootProjectGlobals globals) {
    def gradle = globals.rootProject.gradle
    if (gradle.startParameter.maxWorkerCount > 1) {
      gradle.taskGraph.whenReady { TaskExecutionGraph graph ->
        if (globals.isVerboseMode()) {
          def tasks = graph.allTasks.findAll { t -> t instanceof Test && globals.allTestTasks.contains(t) }
          if (tasks.size() > 1) {
            throw new GradleException("When running tests in verbose mode, pass --max-workers=1 option " +
                "to gradle to prevent mangled console output.")
          }
        }
      }
    }
  }
}
