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

import com.carrotsearch.randomizedtesting.rules.TestRuleAdapter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class RestoreFolderStateRule extends TestRuleAdapter {
  /** The distribution under testing. Read-only! */
  private final Path source;

  /** A folder with unpacked distribution. Restored to pristine state before each test. */
  private Path mirror;

  public RestoreFolderStateRule(Path source, Path mirror) {
    this.source = source;
    this.mirror = mirror;
  }

  @Override
  protected void before() throws Throwable {
    super.before();
    restore();
  }

  @Override
  protected void afterAlways(List<Throwable> errors) throws Throwable {
    super.afterAlways(errors);
  }

  public void restore() throws IOException {
    new Sync().sync(source, mirror);
  }

  public Path getMirror() {
    return mirror;
  }
}
