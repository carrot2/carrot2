
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.util;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Tabular output data dump with automatically adjusted column widths and some other
 * utilities.
 */
public final class TabularOutput
{
    /** Column separator. */
    private String columnSeparator = " ";
    
    /** Declared columns (in order). */
    final List<ColumnSpec> columns = Lists.newArrayList();

    /** */
    final HashMap<String,ColumnSpec> columnsByName = Maps.newHashMap();

    /** Currently buffered row. */
    final List<Object> currentRow = Lists.newArrayList();

    /** Currently buffered rows. */
    final List<List<Object>> data = Lists.newArrayList();

    /**
     * A writer to write the output to.
     */
    private Writer writer;

    /**
     * Flush every n-lines.
     * @see #flush()
     */
    private int flushCount = 1;

    /**
     * Row count since the last flush.
     */
    private int rowCount;

    /**
     * Any committed and emitted output?
     */
    private boolean outputEmitted;

    /**
     * Row listeners.
     */
    private ArrayList<RowListener> listeners = new ArrayList<RowListener>();

    /**
     * Where to write the output to.
     */
    public TabularOutput(Writer writer)
    {
        this.writer = writer;
    }

    /**
     * An output to {@link System#out}.
     */
    public TabularOutput()
    {
        this(new PrintWriter(System.out));
    }
    
    /**
     * A listener notified when a row is complete and being emitted.
     */
    public static interface RowListener
    {
        public void newRow(List<ColumnSpec> columns, List<Object> values);
    }

    /**
     * Column alignment.
     */
    public static enum Alignment
    {
        LEFT, RIGHT, CENTER;

        public String align(String string, int size)
        {
            switch (this)
            {
                case LEFT:
                    return StringUtils.rightPad(string, size);
                case RIGHT:
                    return StringUtils.leftPad(string, size);
                default:
                    return StringUtils.center(string, size);
            }
        }
    }

    /**
     * Column specification.
     */
    public final class ColumnSpec
    {
        /** Column name. */
        public final String name;

        /** Alignment. */
        private Alignment alignment;

        /** Formatter for the value. */
        String format;

        /** Max width of this column observed so far. */
        int maxWidth;

        /** Positional index of this column. */
        public final int index;

        public ColumnSpec(String name, int index)
        {
            this.name = name;
            this.index = index;
        }

        public TabularOutput tabularOutput()
        {
            return TabularOutput.this;
        }

        /**
         * Sets column flush on the last added column.
         */
        public ColumnSpec alignLeft()
        {
            checkNoDataAdded();
            this.alignment = Alignment.LEFT;
            return this;
        }

        /**
         * Sets column flush on the last added column.
         */
        public ColumnSpec alignRight()
        {
            checkNoDataAdded();
            this.alignment = Alignment.RIGHT;
            return this;
        }

        /**
         * Sets column flush on the last added column.
         */
        public ColumnSpec alignCenter()
        {
            checkNoDataAdded();
            this.alignment = Alignment.CENTER;
            return this;
        }

        public String toString()
        {
            return name;
        }

        public ColumnSpec format(String formatString)
        {
            if (format != null) {
                throw new IllegalStateException("Format already initialized to: " + format);
            }
            if (formatString == null) {
                formatString = "%s";
            }
            format = formatString;
            return this;
        }

        boolean updateWidth(int newLength)
        {
            if (newLength > maxWidth) {
                maxWidth = newLength;
                return true;
            }
            return false;
        }

        private String formatValue(Object object)
        {
            String formatted;
            if (object == null) {
                formatted = "--";
            } else {
                object = toStringAdapters(object);

                if (format == null) {
                    format(defaultSpec(object.getClass()).format);
                }
                if (alignment == null) {
                    alignment = defaultSpec(object.getClass()).alignment;
                }
                formatted = String.format(format, object);
            }
            return formatted;
        }

        public String align(String text)
        {
            Alignment alignment = this.alignment;
            if (alignment == null) {
                alignment = Alignment.RIGHT;
            }
            return alignment.align(text, maxWidth);
        }
    }

    /**
     * Apply toString adapters to an object, if any.
     */
    private Object toStringAdapters(Object object)
    {
        if (object instanceof char[]) return new String((char[]) object);
        if (object instanceof byte[]) return Arrays.toString((byte[]) object);
        if (object instanceof short[]) return Arrays.toString((short[]) object);
        if (object instanceof int[]) return Arrays.toString((int[]) object);
        if (object instanceof long[]) return Arrays.toString((long[]) object);
        if (object instanceof float[]) return Arrays.toString((float[]) object);
        if (object instanceof double[]) return Arrays.toString((double[]) object);

        return object;
    }

    /**
     * Adds a listener to receive information about each row. 
     */
    public TabularOutput addListener(RowListener listener)
    {
        this.listeners.add(listener);
        return this;
    }
    
    /**
     * Provide a default column spec for a given object class.
     */
    private final LinkedHashMap<Class<?>, ColumnSpec> defaultFormats = 
        new LinkedHashMap<Class<?>, ColumnSpec>();

    {
        defaultFormats.put(Object.class, 
            defaultFormat(Object.class)
                .format("%s"));
    }

    /**
     * Return default format for a given class.
     */
    ColumnSpec defaultSpec(Class<?> clazz)
    {
        ArrayList<Entry<Class<?>, ColumnSpec>> arrayList = 
            new ArrayList<Map.Entry<Class<?>,ColumnSpec>>(defaultFormats.entrySet());
        Collections.reverse(arrayList);

        for (Map.Entry<Class<?>,ColumnSpec> e : arrayList) {
            if (e.getKey().isAssignableFrom(clazz)) {
                return e.getValue();
            }
        }
        throw new IllegalStateException("No default column spec?");
    }

    /**
     * Default column specification for a given class.
     */
    public ColumnSpec defaultFormat(Class<?> valueClass)
    {
        ColumnSpec colSpec = defaultFormats.get(valueClass);
        if (colSpec == null) {
            colSpec = new ColumnSpec("default-" + valueClass.getSimpleName(), -1);
            defaultFormats.put(valueClass, colSpec);
        }
        return colSpec;
    }

    /**
     * Adds a column to the tabular's layout. Columns must be added before adding data.
     */
    public ColumnSpec addColumn(String name)
    {
        if (!autoAddColumns) checkNoDataAdded();
        ColumnSpec cs = new ColumnSpec(name, columns.size());
        columns.add(cs);
        if (columnsByName.put(cs.name, cs) != null) {
            throw new IllegalArgumentException("Two columns with the same name: " + name);
        }
        columnsChanged = true;
        return cs;
    }

    public TabularOutput columnSeparator(String separator)
    {
        checkNoDataAdded();
        this.columnSeparator = separator;
        return this;
    }
    
    public TabularOutput nextRow()
    {
        data.add(Lists.newArrayList(currentRow));
        currentRow.clear();
        if (++rowCount >= flushCount) {
            rowCount = 0;
            flush();
        }
        return this;
    }

    /**
     * Sequentially adds column data to the current row.
     */
    public TabularOutput rowData(Object... columnData)
    {
        if (currentRow.size() + columnData.length > columns.size())
        {
            throw new IllegalStateException(
                "Row would be larger than the number of columns: " + columns.size() 
                    + ", row: " + currentRow.size() + " attempted add: " + columnData.length);
        }

        currentRow.addAll(Arrays.asList(columnData));
        return this;
    }

    /**
     * Add new columns automatically as they're added? 
     */
    private boolean autoAddColumns = true;
    private boolean columnsChanged = false;

    public TabularOutput rowData(String columnName, Object value)
    {
        ColumnSpec columnSpec = columnsByName.get(columnName);
        if (columnSpec == null) {
            if (autoAddColumns) {
                columnSpec = addColumn(columnName);
            } else {
                throw new IllegalArgumentException("No such column: " + columnName);
            }
        }

        int index = columnSpec.index;
        while (index >= currentRow.size())
        {
            currentRow.add(null);
        }
        currentRow.set(index, value);
        return this;
    }

    /**
     * Flush automatically every n-lines.
     * @see #flush()
     */
    public TabularOutput flushEvery(int n)
    {
        this.flushCount = n;
        return this;
    }

    /**
     * Flush current data rows. May emit header.
     */
    public TabularOutput flush()
    {
        try {
            return flush0();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TabularOutput flush0() throws IOException
    {
        if (data.size() > 0) {
            // Update column widths.
            boolean widthsChanged = false;
            for (List<Object> row : data)
            { 
                for (int i = 0; i < columns.size(); i++)
                {
                    final ColumnSpec col = columns.get(i);
                    final String formatted = col.formatValue(i < row.size() ? row.get(i): null);
                    widthsChanged |= col.updateWidth(formatted.length());
                    if (i >= row.size()) {
                        row.add(null);
                    }
                }
            }

            // If any changes have been detected, re-emit headers.
            if (widthsChanged || columnsChanged)
            {
                if (outputEmitted) {
                    writer.write("\n");
                }
                outputEmitted = true;

                for (int i = 0; i < columns.size(); i++)
                {
                    ColumnSpec col = columns.get(i);
                    String header = col.align(col.name);
                    col.updateWidth(header.length());
                    writer.write(header);
                    writer.write(columnSeparator);
                }
                writer.write("\n");

                columnsChanged = false;
            }

            // Finally, emit the data so far and clear it.
            for (List<Object> row : data)
            {
                for (RowListener listener : listeners)
                {
                    listener.newRow(columns, row);
                }

                for (int i = 0; i < row.size(); i++)
                {
                    final ColumnSpec col = columns.get(i);
                    final String formatted = col.formatValue(i < row.size() ? row.get(i): null);

                    writer.write(col.align(formatted));
                    writer.write(columnSeparator);
                }
                writer.write("\n");
            }
            data.clear();
            
            writer.flush();
        }
        return this;
    }

    private void checkNoDataAdded()
    {
        if (currentRow.size() != 0 || data.size() != 0) throw new IllegalStateException(
            "Data already added.");
    }

    public List<ColumnSpec> getColumns()
    {
        return Collections.unmodifiableList(columns);
    }
}
