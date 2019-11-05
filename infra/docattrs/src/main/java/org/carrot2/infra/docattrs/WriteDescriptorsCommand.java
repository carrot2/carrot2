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
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.carrot2.attrs.AcceptingVisitor;
import org.carrot2.attrs.AliasMapper;
import org.carrot2.attrs.AliasMapperFactory;
import org.carrot2.attrs.ClassNameMapper;
import org.carrot2.clustering.ClusteringAlgorithmProvider;
import org.carrot2.internal.nanojson.JsonWriter;
import org.carrot2.util.SuppressForbidden;

@Parameters(
    commandNames = "write-descriptors",
    commandDescription = "Write attribute descriptors for a set of classes.")
public class WriteDescriptorsCommand extends Command<ExitCode> {
  @Parameter(
      names = {"--recursive", "-r"},
      description = "Generate descriptors recursively if default values are present.")
  public boolean recursive;

  @Parameter(
      names = {"--algorithms"},
      description = "Collect types from all available ClusteringAlgorithm implementations.")
  public boolean algorithms;

  @Parameter(
      names = {"--aliases"},
      description = "Collect types from all available class alias mappings.")
  public boolean aliases;

  @Parameter(
      names = {"--directory", "-d"},
      description =
          "Write descriptors as files in the provided directory. If empty, writes to standard output.")
  public Path output;

  @Parameter(description = "Names of all types for which descriptors should be generated.")
  public List<String> types = new ArrayList<>();

  @Override
  public ExitCode run() {
    BiConsumer<Class<?>, String> consumer = getOutputConsumer();

    List<AcceptingVisitor> types = collectTypes();

    if (recursive) {
      NestedTypeCollector typeCollector = new NestedTypeCollector();
      types.forEach(typeCollector::accept);
      types = new ArrayList<>(typeCollector.collectedTypes());
    }

    List<Class<?>> typeClasses =
        types.stream().map(AcceptingVisitor::getClass).collect(Collectors.toList());

    ClassNameMapper aliasMapper = AliasMapper.SPI_DEFAULTS;

    for (AcceptingVisitor v : types) {
      Map<String, Object> classInfo = new AttrInfoCollector(aliasMapper, typeClasses).collect(v);
      String json = JsonWriter.indent("  ").string().object(classInfo).done();
      consumer.accept(v.getClass(), json);
    }

    return ExitCodes.SUCCESS;
  }

  private List<AcceptingVisitor> collectTypes() {
    List<AcceptingVisitor> types = new ArrayList<>();
    if (algorithms) {
      for (ClusteringAlgorithmProvider provider :
          ServiceLoader.load(ClusteringAlgorithmProvider.class)) {
        types.add(provider.get());
      }
    }

    if (aliases) {
      for (AliasMapperFactory factory : ServiceLoader.load(AliasMapperFactory.class)) {
        AliasMapper mapper = factory.mapper();
        mapper
            .aliases()
            .forEach(
                (key, alias) -> {
                  Object value = mapper.fromName(key);
                  if (value instanceof AcceptingVisitor) {
                    types.add((AcceptingVisitor) value);
                  }
                });
      }
    }

    if (types.isEmpty()) {
      throw new ReportCommandException(
          "At least one type is required.", ExitCodes.ERROR_INVALID_ARGUMENTS);
    }

    for (String typeName : this.types) {
      try {
        types.add(getInstance(Class.forName(typeName).asSubclass(AcceptingVisitor.class)));
      } catch (ClassNotFoundException e) {
        throw new ReportCommandException(
            "Class not found: " + typeName, ExitCodes.ERROR_INVALID_ARGUMENTS);
      }
    }
    return types;
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
  private BiConsumer<Class<?>, String> getOutputConsumer() {
    if (output != null) {
      output = output.toAbsolutePath().normalize();
      if (!Files.isDirectory(output)) {
        throw new ReportCommandException(
            "Directory does not exist: " + output, ExitCodes.ERROR_INVALID_ARGUMENTS);
      }
      return (clazz, json) -> {
        Path descriptorPath = output.resolve(clazz.getName() + ".json");
        try {
          Files.writeString(descriptorPath, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      };
    } else {
      return (clazz, json) -> {
        System.out.println(json);
      };
    }
  }

  public static void main(String[] args) {
    new Launcher().runCommand(new WriteDescriptorsCommand(), args);
  }
}
