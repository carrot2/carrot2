package com.carrotsearch.gradle.randomizedtesting

import com.carrotsearch.gradle.opts.Option
import com.carrotsearch.gradle.opts.OptsPlugin
import com.carrotsearch.gradle.opts.OptsPluginExtension
import org.apache.tools.ant.types.Commandline
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test

class TestOpts {
  public static String PROP_TESTS_SEED = "tests.seed"

  private final Project project

  final Option jvmArgs
  final Option minHeap
  final Option maxHeap
  final Option haltOnFailure
  final Option verboseMode
  final Option forceRerun

  final Option seed

  TestOpts(Project project) {
    this.project = project

    if (!project.plugins.hasPlugin(JavaPlugin)) {
      throw new GradleException("Test options can only be applied to Java projects, apply the Java plugin to: ${project.path}")
    }

    project.plugins.apply(OptsPlugin.class)

    def opts = project.extensions.findByName(OptsPlugin.OPTIONS_EXTENSION_NAME) as OptsPluginExtension

    seed = opts.option(name: PROP_TESTS_SEED, defaultValue: { -> rootSeed() }, description: "Sets the randomization seed.")

    jvmArgs = opts.option(name: "tests.jvmargs", defaultValue: null, description: "JVM arguments to pass directly to the test JVM.")
    minHeap = opts.option(name: "tests.minheap", defaultValue: null, description: "Minimum heap size for test JVMs.")
    maxHeap = opts.option(name: "tests.maxheap", defaultValue: null, description: "Maximum heap size for test JVMs.")
    haltOnFailure = opts.option(name: "tests.haltOnFailure", defaultValue: true, description: "Stop eagerly on test failures.")
    verboseMode = opts.option(name: "tests.verbose", defaultValue: false, description: "Pipe sys/err streams from tests.")
    forceRerun = opts.option(name: "tests.rerun", defaultValue: false, description: "Force test task re-run.")

    project.afterEvaluate {
      project.tasks.withType(Test).configureEach { task ->
        this.jvmArgs.acceptIfNotNull { value -> task.jvmArgs Commandline.translateCommandline((String) value) }
        this.minHeap.acceptIfNotNull { value -> task.minHeapSize = value }
        this.maxHeap.acceptIfNotNull { value -> task.maxHeapSize = value }
        this.haltOnFailure.acceptIfNotNull { value -> task.ignoreFailures = Boolean.parseBoolean((String) value) }

        this.forceRerun.acceptIfNotNull { value ->
          if (Boolean.parseBoolean((String) value)) {
            task.outputs.upToDateWhen {
              false
            }
          }
        }

        // Pass randomization settings.
        systemProperty(seed.name, seed.toString())
      }
    }
  }

  String rootSeed() {
    def globals = project.rootProject.extensions
        .findByName(RootProjectGlobals.EXT_NAME) as RootProjectGlobals
    return globals.rootSeed
  }
}
