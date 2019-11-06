/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.infra.docattrs;

import com.carrotsearch.console.jcommander.Parameter;
import com.carrotsearch.console.jcommander.Parameters;
import com.carrotsearch.console.launcher.Command;
import com.carrotsearch.console.launcher.ExitCode;
import com.carrotsearch.console.launcher.ExitCodes;
import com.carrotsearch.console.launcher.Launcher;
import com.carrotsearch.console.launcher.ReportCommandException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import org.carrot2.attrs.AcceptingVisitor;
import org.carrot2.attrs.AliasMapper;
import org.carrot2.attrs.AliasMapperFactory;
import org.carrot2.attrs.AttrObject;
import org.carrot2.attrs.ClassNameMapper;
import org.carrot2.clustering.ClusteringAlgorithm;
import org.carrot2.clustering.ClusteringAlgorithmProvider;
import org.carrot2.util.SuppressForbidden;

@Parameters(
    commandNames = "write-descriptors",
    commandDescription = "Write attribute descriptors for a set of classes.")
public class WriteDescriptorsCommand extends Command<ExitCode> {
  @Parameter(
      names = {"--directory", "-d"},
      description =
          "Write descriptors as files in the provided directory. If empty, writes to standard output.")
  public Path output;

  @Parameter(
      description =
          "Names of all algorithms for which descriptors should"
              + " be generated (all service-loadable algorithms by defaults).")
  public List<String> types = new ArrayList<>();

  private ClassNameMapper aliasMapper = AliasMapper.SPI_DEFAULTS;

  @Override
  public ExitCode run() {
    BiConsumer<Class<? extends ClusteringAlgorithm>, ClassInfo> emitter = getOutputConsumer();

    // Collect attribute-holding class universe.
    Map<String, Object> aliasedTypes = new TreeMap<>();
    for (AliasMapperFactory factory : ServiceLoader.load(AliasMapperFactory.class)) {
      AliasMapper mapper = factory.mapper();
      mapper
          .aliases()
          .forEach(
              (key, alias) -> {
                Object value = mapper.fromName(key);
                aliasedTypes.put(key, value);
              });
    }

    // For each algorithm, collect class info and emit the descriptor.
    List<ClusteringAlgorithm> algorithms = collectAlgorithms();

    for (ClusteringAlgorithm c : algorithms) {
      ClassInfo ci = ClassInfoCollector.collect(c, aliasMapper);
      expandImplementations(ci, aliasedTypes);
      expandPaths(ci, "algorithm");
      emitter.accept(c.getClass(), ci);
    }

    return ExitCodes.SUCCESS;
  }

  private void expandPaths(ClassInfo ci, String path) {
    ci.attributes.forEach(
        (key, attr) -> {
          attr.path = path + "." + key;
          if (attr.implementations != null) {
            attr.implementations.forEach(
                (alias, impl) -> {
                  String newPath;
                  if (!Objects.equals(impl.type, attr.type)) {
                    newPath = String.format(Locale.ROOT, "((%s) %s)", impl.type, attr.path);
                  } else {
                    newPath = attr.path;
                  }
                  expandPaths(impl, newPath);
                });
          }
        });
  }

  private void expandImplementations(ClassInfo ci, Map<String, Object> aliasedTypes) {
    for (AttrInfo attr : ci.attributes.values()) {
      if (attr.attr instanceof AttrObject<?>) {
        attr.implementations = new TreeMap<>();

        Class<?> attrInterface = ((AttrObject<?>) attr.attr).getInterfaceClass();
        aliasedTypes.entrySet().stream()
            .filter(e -> attrInterface.isAssignableFrom(e.getValue().getClass()))
            .forEach(
                e -> {
                  ClassInfo nested =
                      ClassInfoCollector.collect((AcceptingVisitor) e.getValue(), aliasMapper);
                  attr.implementations.put(e.getKey(), nested);
                });

        if (attr.implementations.isEmpty()) {
          throw new RuntimeException("No implementations for attribute: " + attr.type);
        }
      }
    }

    // Apply recursively.
    ci.attributes.values().stream()
        .map(attr -> attr.implementations)
        .filter(Objects::nonNull)
        .flatMap(impls -> impls.values().stream())
        .forEach(classInfo -> expandImplementations(classInfo, aliasedTypes));
  }

  private List<ClusteringAlgorithm> collectAlgorithms() {
    Map<String, ClusteringAlgorithm> algorithms = new TreeMap<>();
    ServiceLoader.load(ClusteringAlgorithmProvider.class)
        .forEach(prov -> algorithms.put(prov.name(), prov.get()));

    if (!types.isEmpty()) {
      for (String name : types) {
        if (!algorithms.containsKey(name)) {
          throw new ReportCommandException(
              "Algorithm does not exist in the SPI list: " + name,
              ExitCodes.ERROR_INVALID_ARGUMENTS);
        }
      }

      algorithms.keySet().retainAll(types);
    }

    if (algorithms.isEmpty()) {
      throw new ReportCommandException(
          "At least one algorithm is required.", ExitCodes.ERROR_INVALID_ARGUMENTS);
    }

    return new ArrayList<>(algorithms.values());
  }

  static <T> T getInstance(Class<T> clazz) {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException
        | InvocationTargetException
        | NoSuchMethodException
        | IllegalArgumentException
        | IllegalAccessException e) {
      throw new ReportCommandException(
          "Class could not be instantiated: " + clazz.getName(), ExitCodes.ERROR_INTERNAL, e);
    }
  }

  @SuppressForbidden("Legitimate sysout")
  private BiConsumer<Class<? extends ClusteringAlgorithm>, ClassInfo> getOutputConsumer() {
    DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
    pp.indentArraysWith(new DefaultIndenter("  ", DefaultIndenter.SYS_LF));

    ObjectMapper om =
        new ObjectMapper()
            .configure(SerializationFeature.INDENT_OUTPUT, true)
            .setDefaultPrettyPrinter(pp);

    if (output != null) {
      output = output.toAbsolutePath().normalize();
      if (!Files.isDirectory(output)) {
        throw new ReportCommandException(
            "Directory does not exist: " + output, ExitCodes.ERROR_INVALID_ARGUMENTS);
      }

      return (clazz, json) -> {
        Path descriptorPath = output.resolve(clazz.getName() + ".json");
        try (Writer w = Files.newBufferedWriter(descriptorPath, StandardCharsets.UTF_8)) {
          om.writeValue(w, json);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      };
    } else {
      return (clazz, json) -> {
        try {
          System.out.println();
          System.out.println(om.writeValueAsString(json));
        } catch (JsonProcessingException e) {
          throw new UncheckedIOException(e);
        }
      };
    }
  }

  public static void main(String[] args) {
    new Launcher().runCommand(new WriteDescriptorsCommand(), args);
  }
}
