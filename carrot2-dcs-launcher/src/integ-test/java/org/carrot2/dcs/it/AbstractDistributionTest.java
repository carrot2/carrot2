package org.carrot2.dcs.it;

import com.carrotsearch.randomizedtesting.LifecycleScope;
import com.carrotsearch.randomizedtesting.RandomizedTest;
import com.carrotsearch.randomizedtesting.rules.TestRuleAdapter;
import org.carrot2.RestoreFolderStateRule;
import org.carrot2.TestBase;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.RuleChain;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;

public abstract class AbstractDistributionTest extends TestBase {
  private static Path mirrorPath;
  private static RestoreFolderStateRule restoreDistRule;

  @ClassRule
  public static RuleChain classChain = RuleChain.outerRule(new TestRuleAdapter() {

    protected void before() throws Throwable {
      Path distDir =
          Paths.get(System.getProperty("dist.dir", "./build/carrot2-dcs-launcher"))
          .normalize()
          .toAbsolutePath();

      if (!Files.isDirectory(distDir)) {
        throw new AssertionError("Distribution not assembled at path: " + distDir);
      }

      // Sometimes add awkward or problematic characters to distribution path.
      mirrorPath = RandomizedTest.randomFrom(Arrays.<Function<Path, Path>> asList(
          (path) -> path.resolve("New Folder (2)"),
          (path) -> path
      )).apply(newTempDir(LifecycleScope.SUITE).toAbsolutePath().normalize());

      Files.createDirectories(mirrorPath);
      restoreDistRule = new RestoreFolderStateRule(distDir, mirrorPath);
    }
  });

  @Rule
  public final RuleChain testChain = RuleChain.outerRule(restoreDistRule);

  protected final DcsService startDcs() {
    try {
      return new EmbeddedDcs(getDistributionDir());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected final Path getDistributionDir() {
    return mirrorPath;
  }
}
