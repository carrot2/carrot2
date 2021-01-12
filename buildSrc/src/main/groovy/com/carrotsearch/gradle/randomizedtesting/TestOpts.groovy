package com.carrotsearch.gradle.randomizedtesting

import com.carrotsearch.gradle.opts.Option
import com.carrotsearch.gradle.opts.OptsPlugin
import com.carrotsearch.gradle.opts.OptsPluginExtension
import org.apache.tools.ant.types.Commandline
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class TestOpts {
  public static String PROP_TESTS_SEED = "tests.seed"

  private final Project project

  final Option jvmArgs
  final Option minHeap
  final Option maxHeap
  final Option haltOnFailure
  final Option verboseMode
  final Option forceRerun
  final Option jvms

  final Option cwdDir
  final Option tmpDir

  final Option seed

  TestOpts(Project project) {
    this.project = project

    if (!project.plugins.hasPlugin(JavaPlugin)) {
      throw new GradleException("Test options can only be applied to Java projects, apply the Java plugin to: ${project.path}")
    }

    // Apply opts plugin, we need it.
    project.plugins.apply(OptsPlugin.class)

    def opts = project.extensions.findByName(OptsPlugin.OPTIONS_EXTENSION_NAME) as OptsPluginExtension

    seed = opts.option(name: PROP_TESTS_SEED, defaultValue: { -> rootSeed() }, description: "Sets the randomization seed.")

    jvmArgs = opts.option(name: "tests.jvmargs", defaultValue: null, description: "JVM arguments to pass directly to the test JVM.")
    minHeap = opts.option(name: "tests.minheap", defaultValue: null, description: "Minimum heap size for test JVMs.")
    maxHeap = opts.option(name: "tests.maxheap", defaultValue: null, description: "Maximum heap size for test JVMs.")
    haltOnFailure = opts.option(name: "tests.haltOnFailure", defaultValue: true, description: "Stop eagerly on test failures.")
    verboseMode = opts.option(name: "tests.verbose", defaultValue: false, description: "Pipe sys/err streams from tests.")
    forceRerun = opts.option(name: "tests.rerun", defaultValue: false, description: "Force test task re-run.")
    jvms = opts.option(name: "tests.jvms", defaultValue: { -> defaultTestJvms() as String }, description: "Number of forked test JVMs.")

    cwdDir = opts.option(name: "tests.cwdDir", defaultValue: { -> project.buildDir.toPath().resolve("test-cwd") }, description: "Work directory for forked test JVMs.")
    tmpDir = opts.option(name: "tests.tmpDir", defaultValue: { -> project.buildDir.toPath().resolve("test-tmp") }, description: "Temp directory for forked test JVMs.")

    project.afterEvaluate {
      project.tasks.withType(Test).configureEach { task ->
        // gradle test task setup.
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

        // Configure forks
        Integer forks = null
        def verbose = false
        verboseMode.acceptIfNotNull { v -> verbose = Boolean.parseBoolean((String) v) }
        this.jvms.acceptIfNotNull { value ->
          forks = Integer.parseInt((String) jvms.value)
        }
        if (verbose && (forks == null || forks > 1)) {
          task.logger.info("${task.path}.maxParallelForks forced to 1 in verbose mode.")
          forks = 1
        }
        if (forks != null) {
          task.maxParallelForks = forks
        }

        // Configure directories
        cwdDir.acceptIfNotNull { value ->
          Path p = Paths.get((String) value)
          task.setWorkingDir(p)
          task.doFirst {
            Files.createDirectories(p)
          }
        }
        tmpDir.acceptIfNotNull { value ->
          Path p = Paths.get((String) value)
          task.systemProperty("java.io.tmpdir", p.toString())
          task.doFirst {
            Files.createDirectories(p)
          }
        }

        // Pass several randomizedtesting options via system properties.
        task.systemProperty(seed.name, seed.toString())
      }
    }
  }

  String rootSeed() {
    def globals = project.rootProject.extensions
        .findByName(RootProjectGlobals.EXT_NAME) as RootProjectGlobals
    return globals.rootSeed
  }

  private int defaultTestJvms() {
    return ((int) Math.max(1, Math.min(Runtime.runtime.availableProcessors() / 2.0, 8.0)))
  }
}
