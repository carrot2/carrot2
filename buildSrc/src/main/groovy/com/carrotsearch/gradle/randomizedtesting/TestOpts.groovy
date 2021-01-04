package com.carrotsearch.gradle.randomizedtesting

import com.carrotsearch.gradle.opts.Option
import com.carrotsearch.gradle.opts.OptsPlugin
import com.carrotsearch.gradle.opts.OptsPluginExtension
import org.apache.tools.ant.types.Commandline
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test

class TestOpts {
  private final Project project

  Option jvmArgs = new Option(name: "tests.jvmargs", value: null, description: "JVM arguments to pass directly to the test JVM.")
  Option minHeap = new Option(name: "tests.minheap", value: null, description: "Minimum heap size for test JVMs.")
  Option maxHeap = new Option(name: "tests.maxheap", value: null, description: "Maximum heap size for test JVMs.")
  Option haltOnFailure = new Option(name: "tests.haltOnFailure", value: true, description: "Stop eagerly on test failures.")
  Option verboseMode = new Option(name: "tests.verbose", value: false, description: "Pipe sys/err streams from tests.")

  Option seed = new Option(name: "tests.seed", value: { -> rootSeed() }, description: "Sets the randomization seed.")

  TestOpts(Project project) {
    this.project = project

    project.plugins.withType(JavaPlugin.class).configureEach {
      project.plugins.apply(OptsPlugin.class)

      def opts = project.extensions.findByName(OptsPlugin.OPTIONS_EXTENSION_NAME) as OptsPluginExtension
      opts.option(jvmArgs)
      opts.option(minHeap)
      opts.option(maxHeap)
      opts.option(haltOnFailure)
      opts.option(verboseMode)
      opts.option(seed)

      project.afterEvaluate {
        project.tasks.withType(Test).configureEach { task ->
          this.jvmArgs.acceptIfNotNull { value -> task.jvmArgs Commandline.translateCommandline((String) value) }
          this.minHeap.acceptIfNotNull { value -> task.minHeapSize = value }
          this.maxHeap.acceptIfNotNull { value -> task.maxHeapSize = value }
          this.haltOnFailure.acceptIfNotNull { value -> task.ignoreFailures = Boolean.parseBoolean((String) value) }

          // Pass randomization settings.
          systemProperty(seed.name, seed.toString())
        }
      }
    }
  }

  String rootSeed() {
    def globals = project.rootProject.extensions
        .findByName(RootProjectGlobals.EXT_NAME) as RootProjectGlobals
    return globals.rootSeed
  }
}
