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
package org.carrot2;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

class Sync {
  private static class Entry {
    String name;
    Path path;

    public Entry(Path path) {
      this.path = path;
      this.name = path.getFileName().toString();
    }
  }

  public void sync(Path source, Path target) throws IOException {
    List<Entry> sourceEntries = files(source);
    List<Entry> targetEntries = files(target);

    for (Entry src : sourceEntries) {
      Path dst = target.resolve(src.name);
      if (Files.isDirectory(src.path)) {
        Files.createDirectories(dst);
        sync(src.path, dst);
      } else {
        if (!Files.exists(dst)
            || Files.size(dst) != Files.size(src.path)
            || Files.getLastModifiedTime(dst).compareTo(Files.getLastModifiedTime(src.path)) != 0) {
          Files.copy(
              src.path,
              dst,
              StandardCopyOption.COPY_ATTRIBUTES,
              StandardCopyOption.REPLACE_EXISTING);
        }
      }
    }

    Set<String> atSource = sourceEntries.stream().map(e -> e.name).collect(Collectors.toSet());
    targetEntries.stream().filter(v -> !atSource.contains(v.name)).forEach(e -> remove(e.path));
  }

  private List<Entry> files(Path source) throws IOException {
    ArrayList<Entry> entries = new ArrayList<>();
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(source)) {
      ds.forEach(p -> entries.add(new Entry(p)));
    }
    return entries;
  }

  private static void remove(Path p) {
    try {
      Files.walkFileTree(
          p,
          new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
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
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
