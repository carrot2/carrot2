package com.carrotsearch.gradle.randomizedtesting

import com.carrotsearch.gradle.opts.Option
import com.carrotsearch.gradle.opts.OptsPluginExtension
import groovy.transform.CompileStatic
import org.apache.tools.ant.types.Commandline
import org.gradle.BuildResult
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult

import java.nio.file.Path

/**
 * This implements a rule that enforces the following:
 *
 * <ul>
 *   <li>if test filters (<code>--tests</code>) are used,
 * </ul>
 * do:
 * <ul>
 *   <li>disable <code>failOnNoMatchingTests</code> on any individual task (filter),
 *   <li>add a global after-build hook to assert that:
 *     <ul>
 *       <li>at least one test task has been executed,
 *       <li>at least one test has been executed.
 *     </ul>
 * </ul>
 */
@CompileStatic
class GlobalFailOnNoTests {
  private final Project project
  private final RandomizedTestingExtension conf

  GlobalFailOnNoTests(Project project) {
    this.project = project
    this.conf = project.extensions.findByType(RandomizedTestingExtension)
  }

  void apply() {
    project.afterEvaluate {
      if (conf.failOnNoTests) {
        RootProjectGlobals globals = project.rootProject.extensions.findByType(RootProjectGlobals)
        if (globals.failOnNoTestsHook.getAndSet(false)) {
          installAfterBuildCheck(conf.project.gradle, globals)
        }

        applyToTestTasks()
      }
    }
  }

  private void applyToTestTasks() {
    Collection<Test> testTasks = conf.tasks.collect()

    // Don't fail on each individual project, fail later on.
    testTasks*.filter.failOnNoMatchingTests = false

    def globals = conf.project.rootProject.extensions
        .findByName(RootProjectGlobals.EXT_NAME) as RootProjectGlobals

    globals.allTestTasks.addAll(testTasks)
  }

  private static class FailureEntry {
    String name
    String project
    Path output
    String reproduceLine
  }

  private static void installAfterBuildCheck(Gradle gradle, RootProjectGlobals rootExtension) {
    def args = gradle.startParameter.taskNames

    boolean hasTestFilters = args.findAll({ arg ->
      return arg == /--tests/
    }).size() > 0

    gradle.taskGraph.whenReady { TaskExecutionGraph graph ->
      List<FailureEntry> failedTests = []
      def allTestTasks = rootExtension.allTestTasks

      // Only apply the check if we are actually filtering and
      // if there are some test tasks collected.
      if (!allTestTasks.isEmpty()) {
        def testTasks = graph.allTasks.findAll { task ->
          task instanceof Test && allTestTasks.contains(task)
        }

        def executedTests = 0
        def executedTasks = 0

        testTasks.each { task ->
          task.doFirst {
            executedTasks++
          }

          def test = task as Test
          def project = test.project
          def reproLine = buildReproLine(project)
          Path outputsDir = task.extensions.extraProperties.get(HandleOutputs.TEST_OUTPUTS_DIR_PROPERTY) as Path

          test.afterTest { TestDescriptor desc, TestResult result ->
            if (result.resultType == TestResult.ResultType.FAILURE) {
              failedTests << new FailureEntry([
                  "name": "${desc.className}.${desc.name}",
                  "project": "${project.path}",
                  "output": outputsDir.resolve(ErrorReportingTestListener.getOutputLogName(desc.parent)),
                  "reproduceLine": "gradlew ${project.path}:${test.name} --tests \"${desc.className}.${desc.name}\" ${reproLine}"
              ])
            }
          }

          test.afterSuite { TestDescriptor desc, TestResult result ->
            executedTests += result.testCount
            if (result.exceptions) {
              failedTests << new FailureEntry([
                  "name": "${desc.name}",
                  "project": "${project.path}",
                  "output": outputsDir.resolve(ErrorReportingTestListener.getOutputLogName(desc)),
                  "reproduceLine": "gradlew ${project.path}:${test.name} --tests \"${desc.name}\" ${reproLine}"
              ])
            }
          }
        }

        // After the build is finished, check the test count.
        gradle.buildFinished { BuildResult result ->
          if (hasTestFilters) {
            if (executedTests == 0) {
              if (result.failure) {
                // Skip reporting tests if something else failed.
                return
              }

              if (executedTasks > 0) {
                throw new GradleException("No tests executed, the provided filters excluded all tests, maybe?")
              } else {
                throw new GradleException("No test tasks executed (use --rerun-tasks or cleanTest)?")
              }
            }
          }

          if (failedTests) {
            def formatted = failedTests
                .sort { a, b -> b.project.compareTo(a.project) }
                .collect { e -> String.format(Locale.ROOT,
                    "  - %s (%s)\n    Test output: %s\n    Reproduce with: %s\n",
                    e.name,
                    e.project,
                    e.output,
                    e.reproduceLine
                  )
                }
                .join("\n")

            int count = failedTests.size()
            throw new GradleException("${count} test${count != 1 ? 's' : ''} have failed:\n${formatted}")
          }
        }
      }
    }
  }

  static String buildReproLine(Project project) {
    return project.extensions.findByType(OptsPluginExtension)
        .findAll { Option p -> !p.equalsDefault }
        .collect { p -> Commandline.quoteArgument("-P" + ((Option) p).name + "=" + p ).toString() }
        .join(" ")
  }
}
