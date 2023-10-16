/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package com.carrotsearch.lingo3g.it;

import static org.assertj.core.api.Assertions.*;

import com.carrotsearch.progresso.GenericTask;
import com.carrotsearch.progresso.Task;
import com.carrotsearch.progresso.TaskStats;
import com.carrotsearch.progresso.Tasks;
import com.carrotsearch.progresso.Tracker;
import com.carrotsearch.randomizedtesting.rules.TestRuleAdapter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.slf4j.LoggerFactory;

public class RestoreDistributionStateRule extends TestRuleAdapter {
  /** The distribution ZIP under testing. Read-only! */
  private final Path distributionZip;

  /**
   * A folder with unpacked {@link #distributionZip}. Restored to pristine state before each test.
   */
  private final Path distributionUnpackPath;

  /** Base folder of the distribution, under {@link #distributionUnpackPath}. */
  private Path distributionBasePath;

  /** Opened up archive (parsed directory, etc.) */
  private ZipFile distributionZipFile;

  public RestoreDistributionStateRule(Path distributionZip, Path distributionUnpackPath) {
    this.distributionZip = distributionZip;
    this.distributionUnpackPath = distributionUnpackPath;
  }

  @Override
  protected void before() throws Throwable {
    super.before();

    this.distributionZipFile = new ZipFile(distributionZip.toFile());
    this.distributionBasePath = syncTempDistributionState();
  }

  @Override
  protected void afterAlways(List<Throwable> errors) throws Throwable {
    distributionBasePath = null;

    if (distributionZipFile != null) {
      try {
        distributionZipFile.close();
      } catch (IOException e) {
        errors.add(e);
      }
    }
    super.afterAlways(errors);
  }

  private Path syncTempDistributionState() throws IOException {
    // Sync the contents of the unpacked dir with the ZIP file.
    GenericTask task = Tasks.newGenericTask("sync");
    try (Tracker tracker = task.start()) {
      sync(distributionUnpackPath, distributionZipFile, tracker.task());
    }
    LoggerFactory.getLogger(getClass()).info("Sync stats:\n" + TaskStats.breakdown(task));

    // Make sure we have only one subdirectory (with the distribution).
    File[] subfiles = distributionUnpackPath.toFile().listFiles();
    assertThat(subfiles)
        .describedAs(distributionZip.toFile() + " => " + Arrays.toString(subfiles))
        .hasSize(1);
    assertThat(subfiles[0]).isDirectory();

    return subfiles[0].toPath().toAbsolutePath().normalize();
  }

  public Path getDistributionBasePath() {
    return distributionBasePath;
  }

  public Path getDistributionZip() {
    return distributionZip;
  }

  private static class SyncingVisitor extends SimpleFileVisitor<Path> {
    private final StringBuilder sb = new StringBuilder();
    private final Path root;
    private final Map<String, ZipArchiveEntry> visited;

    private int visitedFiles;
    private int visitedFolders;
    private final List<Path> removedPaths = new ArrayList<>();
    private final List<Path> updatedPaths = new ArrayList<>();

    public SyncingVisitor(Path root, Map<String, ZipArchiveEntry> visited) {
      this.root = root;
      this.visited = visited;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
        throws IOException {
      final String relative = relative(root, dir);
      if (relative.equals("/")) {
        return FileVisitResult.CONTINUE;
      }

      visitedFolders++;
      ZipArchiveEntry entry = visited.remove(relative);
      if (entry != null) {
        return FileVisitResult.CONTINUE;
      } else {
        remove(dir);
        removedPaths.add(dir);
        return FileVisitResult.SKIP_SUBTREE;
      }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      final String relative = relative(root, file);

      visitedFiles++;
      ZipArchiveEntry entry = visited.remove(relative);
      if (entry != null) {
        if (entry.getSize() != attrs.size()
            || attrs.creationTime().compareTo(attrs.lastModifiedTime()) != 0) {
          // Recreate.
          remove(file);
          updatedPaths.add(file);
          visited.put(relative, entry);
        }
      } else {
        remove(file);
        removedPaths.add(file);
      }

      return FileVisitResult.CONTINUE;
    }

    private String relative(Path parent, Path sub) {
      sb.setLength(0);
      for (Path component : parent.relativize(sub)) {
        sb.append(component).append('/');
      }

      // Keep trailing '/' for directories.
      if (!Files.isDirectory(sub)) {
        sb.setLength(sb.length() - 1);
      }

      return sb.toString();
    }
  }

  protected static TreeMap<String, ZipArchiveEntry> zipEntriesAsMap(ZipFile zipFile)
      throws IOException {
    TreeMap<String, ZipArchiveEntry> zipEntries = new TreeMap<>();
    for (Enumeration<ZipArchiveEntry> entries = zipFile.getEntries(); entries.hasMoreElements(); ) {
      ZipArchiveEntry entry = entries.nextElement();
      if ((entry = zipEntries.put(entry.getName(), entry)) != null) {
        throw new IOException("Duplicate ZIP entyr: " + entry.getName());
      }
    }
    return zipEntries;
  }

  private static void sync(final Path target, final ZipFile zipFile, final Task<?> parent)
      throws IOException {
    if (!Files.exists(target)) {
      Files.createDirectory(target);
    }

    assertThat(Files.isDirectory(target)).as(target.toString()).isTrue();
    assertThat(Files.isWritable(target)).as(target.toString()).isTrue();

    // Walk through the target files and folders and update.
    final Map<String, ZipArchiveEntry> remaining;
    try (Tracker zipDirectory = parent.newGenericSubtask("zip open").start()) {
      remaining = zipEntriesAsMap(zipFile);
    }

    try (Tracker fsWalk = parent.newGenericSubtask("fs walk").start()) {
      SyncingVisitor visitor = new SyncingVisitor(target, remaining);
      Files.walkFileTree(target, visitor);

      fsWalk.attribute("files", "%d", visitor.visitedFiles);
      fsWalk.attribute("folders", "%d", visitor.visitedFolders);
      fsWalk.attribute("removed", "%d", visitor.removedPaths.size());
      fsWalk.attribute("updated", "%d", visitor.updatedPaths.size());
    }

    // Check if there are any remaining files. If so, create them.
    try (Tracker fsWalk = parent.newGenericSubtask("unzip").start()) {
      int unzippedFiles = 0;
      int unzippedFolders = 0;
      if (!remaining.isEmpty()) {
        for (Map.Entry<String, ZipArchiveEntry> e : remaining.entrySet()) {
          final Path p = target.resolve(e.getKey());

          ZipArchiveEntry zipEntry = e.getValue();
          if (zipEntry.isDirectory()) {
            unzippedFolders++;
            Files.createDirectories(p);
            setPermissions(p, zipEntry);
          } else {
            unzippedFiles++;
            Path parentPath = p.getParent();
            assertThat(Files.isDirectory(parentPath)).as(parentPath.toString()).isTrue();

            try (InputStream is = zipFile.getInputStream(zipEntry)) {
              Files.copy(is, p);
            }

            BasicFileAttributeView view =
                Files.getFileAttributeView(p, BasicFileAttributeView.class);
            BasicFileAttributes attrs = view.readAttributes();
            view.setTimes(attrs.creationTime(), attrs.creationTime(), null);

            setPermissions(p, zipEntry);
          }
        }
      }

      fsWalk.attribute("files", "%d", unzippedFiles);
      fsWalk.attribute("folders", "%d", unzippedFolders);
    }
  }

  private static void setPermissions(Path p, ZipArchiveEntry zipEntry) throws IOException {
    PosixFileAttributeView view = Files.getFileAttributeView(p, PosixFileAttributeView.class);
    if (view == null) {
      return;
    }

    Set<PosixFilePermission> s = new HashSet<>();
    int unixMode = zipEntry.getUnixMode();
    if ((unixMode & 0001) != 0) s.add(PosixFilePermission.OTHERS_EXECUTE);
    if ((unixMode & 0002) != 0) s.add(PosixFilePermission.OTHERS_WRITE);
    if ((unixMode & 0004) != 0) s.add(PosixFilePermission.OTHERS_READ);
    if ((unixMode & 0010) != 0) s.add(PosixFilePermission.GROUP_EXECUTE);
    if ((unixMode & 0020) != 0) s.add(PosixFilePermission.GROUP_WRITE);
    if ((unixMode & 0040) != 0) s.add(PosixFilePermission.GROUP_READ);
    if ((unixMode & 0100) != 0) s.add(PosixFilePermission.OWNER_EXECUTE);
    if ((unixMode & 0200) != 0) s.add(PosixFilePermission.OWNER_WRITE);
    if ((unixMode & 0400) != 0) s.add(PosixFilePermission.OWNER_READ);
    view.setPermissions(s);
  }

  private static void remove(Path p) throws IOException {
    Files.walkFileTree(
        p,
        new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
          }
        });
  }
}
