/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.carrot2.attrs.Attrs;
import org.carrot2.internal.nanojson.JsonWriter;
import org.carrot2.util.SuppressForbidden;

/** Converts legacy {@code *.utf8} plain-text resources into corresponding JSON dictionaries. */
@SuppressForbidden("Command-line tool, legacy use of sysouts")
public class ConvertLegacyResources {
  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.err.println("Args: files or directories containing *.utf8 resources");
    }

    Set<Path> paths = new HashSet<>();
    for (String arg : args) {
      Path p = Paths.get(arg);
      if (Files.isDirectory(p)) {
        try (Stream<Path> s = Files.walk(p)) {
          for (Path subpath : s.collect(Collectors.toList())) {
            if (Files.isRegularFile(subpath)) {
              paths.add(subpath);
            }
          }
        }
      } else {
        paths.add(p);
      }
    }

    Pattern filePattern =
        Pattern.compile(
            "(?<lang>[^.]+)\\.(?<type>stopwords|stoplabels)\\.utf8", Pattern.CASE_INSENSITIVE);
    for (Path p : paths) {
      if (!Files.exists(p)) {
        System.err.println("File does not exist: " + p);
        continue;
      }

      Matcher matcher = filePattern.matcher(p.getFileName().toString());
      if (!matcher.matches()) {
        System.err.println("File does not match lexical resource pattern: " + p);
        continue;
      }

      String contents = Files.readString(p, StandardCharsets.UTF_8);

      String lang = matcher.group("lang");
      String type = matcher.group("type");

      DefaultDictionaryImpl dict = new DefaultDictionaryImpl();
      Set<String> lines =
          DefaultLexicalDataProvider.readLines(new BufferedReader(new StringReader(contents)));
      String target;
      switch (type) {
        case "stopwords":
          dict.exact.set(lines.toArray(String[]::new));
          target = lang + ".word-filters.json";
          break;
        case "stoplabels":
          dict.regexp.set(lines.toArray(String[]::new));
          target = lang + ".label-filters.json";
          break;
        default:
          throw new RuntimeException("Unknown type: " + type);
      }

      Map<String, Object> asMap = Attrs.extract(dict);
      String json = JsonWriter.indent("  ").string().object(asMap).done();
      Files.writeString(p.getParent().resolve(target), json, StandardCharsets.UTF_8);
    }
  }
}
