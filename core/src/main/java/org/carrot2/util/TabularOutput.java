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
package org.carrot2.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/** Tabular output data dump with automatically adjusted column widths and some other utilities. */
public final class TabularOutput {
  /** Column separator. */
  private final String columnSeparator;

  /** Values for the current row. */
  private final List<Object> currentRow = new ArrayList<>();

  /** Buffered rows. */
  private final List<List<Object>> data = new ArrayList<>();

  /** A writer to write the output to. */
  private final Writer writer;

  /** Flush rows every n-th line. */
  private final int flushCount;

  /** */
  private final boolean outputHeader;

  private final List<ColumnData> columns;

  private static final class ColumnData {
    final String name;
    final ColumnSpec spec;
    int width;

    public ColumnData(String name, ColumnSpec spec) {
      this.name = name;
      this.spec = spec;
    }
  }

  /** Column alignment. */
  public static enum Alignment {
    LEFT,
    RIGHT,
    CENTER;
  }

  /** Column specification. */
  public static final class ColumnSpec {
    /** Alignment. */
    Alignment alignment = Alignment.LEFT;

    /** Formatter for the value. */
    String format = "%s";

    /** Sets column flush on the last added column. */
    public ColumnSpec alignLeft() {
      this.alignment = Alignment.LEFT;
      return this;
    }

    /** Sets column flush on the last added column. */
    public ColumnSpec alignRight() {
      this.alignment = Alignment.RIGHT;
      return this;
    }

    /** Sets column flush on the last added column. */
    public ColumnSpec alignCenter() {
      this.alignment = Alignment.CENTER;
      return this;
    }

    public ColumnSpec format(String formatString) {
      format = Objects.requireNonNull(formatString);
      return this;
    }
  }

  public static class Builder {
    private Writer writer;
    private boolean outputHeader = true;
    private int flushCount = 1;
    private String columnSeparator = " ";
    private LinkedHashMap<String, ColumnSpec> columnsByName = new LinkedHashMap<>();

    public Builder(Writer writer) {
      this.writer = Objects.requireNonNull(writer);
    }

    /** Emit or skip the header. */
    public Builder outputHeaders(boolean outputHeader) {
      this.outputHeader = outputHeader;
      return this;
    }

    /**
     * Flush automatically every n-lines.
     *
     * @see #flush()
     */
    public Builder flushEvery(int n) {
      this.flushCount = n;
      return this;
    }

    /** Don't flush lines automatically. */
    public Builder noAutoFlush() {
      return flushEvery(Integer.MAX_VALUE);
    }

    public Builder columnSeparator(String separator) {
      this.columnSeparator = separator;
      return this;
    }

    /** Adds a column to the tabular's layout. */
    public Builder addColumn(String name, Consumer<ColumnSpec> columnConfig) {
      if (columnsByName.containsKey(name)) {
        throw new IllegalArgumentException("Two columns with the same name: " + name);
      }

      ColumnSpec cs = new ColumnSpec();
      columnConfig.accept(cs);
      columnsByName.put(name, cs);
      return this;
    }

    public Builder addColumn(String name) {
      return addColumn(name, t -> {});
    }

    public TabularOutput build() {
      return new TabularOutput(writer, columnsByName, columnSeparator, flushCount, outputHeader);
    }

    public Builder addColumns(String... names) {
      Arrays.stream(names).forEach(name -> addColumn(name));
      return this;
    }
  }

  public static Builder to(Writer writer) {
    return new Builder(writer);
  }

  /** Where to write the output to. */
  public TabularOutput(
      Writer writer,
      LinkedHashMap<String, ColumnSpec> columns,
      String colSeparator,
      int flushCount,
      boolean outputHeader) {
    this.writer = writer;
    this.outputHeader = outputHeader;
    this.flushCount = flushCount;
    this.columnSeparator = colSeparator;
    this.columns =
        columns.entrySet().stream()
            .map((e) -> new ColumnData(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
  }

  public TabularOutput append(Object... values) {
    this.currentRow.addAll(Arrays.asList(values));
    if (currentRow.size() > columns.size()) {
      throw new RuntimeException(
          "Current row has more values than declared columns: " + currentRow);
    }
    return this;
  }

  public TabularOutput nextRow() {
    while (currentRow.size() < columns.size()) {
      currentRow.add(null);
    }
    data.add(new ArrayList<>(currentRow));
    currentRow.clear();
    if (data.size() >= flushCount) {
      flush();
    }
    return this;
  }

  public Writer getWriter() {
    return writer;
  }

  public TabularOutput flush() {
    try {
      flush0();
      getWriter().flush();
      return this;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void flush0() throws IOException {
    if (!currentRow.isEmpty()) {
      throw new RuntimeException("Unflushed data in the current row. Very likely a bug.");
    }

    if (data.isEmpty()) {
      return;
    }

    // Check if column widths have changed.
    List<List<String>> formatted = new ArrayList<>();
    boolean columnWidthsChanged = false;
    for (List<Object> row : data) {
      List<String> formattedRow = new ArrayList<>();
      formatted.add(formattedRow);

      assert row.size() == columns.size();
      for (int i = 0; i < row.size(); i++) {
        ColumnData colData = columns.get(i);
        String value = formatValue(colData, row.get(i));
        formattedRow.add(value);

        int vlen = value.length();
        if (vlen > colData.width) {
          colData.width = vlen;
          columnWidthsChanged = true;
        }
      }
    }
    data.clear();

    if (columnWidthsChanged && outputHeader) {
      for (ColumnData cd : columns) {
        cd.width = Math.max(cd.name.length(), cd.width);
      }

      writer.write(
          String.join(
              columnSeparator,
              columns.stream()
                  .map((d) -> align(d.spec.alignment, d.name, d.width))
                  .collect(Collectors.toList())));
      writer.write("\n");
    }

    for (List<String> row : formatted) {
      for (int i = 0; i < columns.size(); i++) {
        ColumnData cd = columns.get(i);
        row.set(i, align(cd.spec.alignment, row.get(i), cd.width));
      }

      writer.write(String.join(columnSeparator, row));
      writer.write("\n");
    }
  }

  private String align(Alignment alignment, String string, int width) {
    switch (alignment) {
      case LEFT:
        return padEnd(string, width, ' ');
      case RIGHT:
        return padStart(string, width, ' ');
      default:
        return center(string, width);
    }
  }

  private String formatValue(ColumnData columnData, Object v) {
    v = toStringAdapter(v);

    String value;
    if (v == null) {
      value = "--";
    } else {
      value = String.format(Locale.ROOT, columnData.spec.format, v);
    }

    return value;
  }

  private Object toStringAdapter(Object object) {
    if (object == null) return object;
    if (object instanceof char[]) return new String((char[]) object);
    if (object instanceof byte[]) return Arrays.toString((byte[]) object);
    if (object instanceof short[]) return Arrays.toString((short[]) object);
    if (object instanceof int[]) return Arrays.toString((int[]) object);
    if (object instanceof long[]) return Arrays.toString((long[]) object);
    if (object instanceof float[]) return Arrays.toString((float[]) object);
    if (object instanceof double[]) return Arrays.toString((double[]) object);
    return object;
  }

  private static String padStart(String string, int minLength, char padChar) {
    Objects.requireNonNull(string);
    if (string.length() >= minLength) {
      return string;
    }
    StringBuilder sb = new StringBuilder(minLength);
    for (int i = string.length(); i < minLength; i++) {
      sb.append(padChar);
    }
    sb.append(string);
    return sb.toString();
  }

  private static String padEnd(String string, int minLength, char padChar) {
    Objects.requireNonNull(string); // eager for GWT.
    if (string.length() >= minLength) {
      return string;
    }
    StringBuilder sb = new StringBuilder(minLength);
    sb.append(string);
    for (int i = string.length(); i < minLength; i++) {
      sb.append(padChar);
    }
    return sb.toString();
  }

  private String center(String s, int size) {
    StringBuilder sb = new StringBuilder(size);
    for (int i = (size - s.length()) / 2; i > 0; i--) {
      sb.append(' ');
    }
    sb.append(s);
    while (sb.length() < size) {
      sb.append(' ');
    }
    return sb.toString();
  }
}
