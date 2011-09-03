package org.carrot2.text.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

/**
 * Tabular data dump with automatically adjusted column widths.
 */
public class Tabular
{
    /** Emit header every n-th line. */
    private final int HEADER_EVERY_NTH = 50;

    /** Column separator. */
    private final String COLUMN_SEPARATOR = " ";

    public static enum Alignment
    {
        LEFT,
        RIGHT,
        CENTER;

        public String align(String string, int size)
        {
            switch (this) {
                case LEFT:
                    return StringUtils.rightPad(string, size);
                case RIGHT:
                    return StringUtils.leftPad(string, size);
                default:
                    return StringUtils.center(string, size);
            }
        }
    }

    private static class ColumnSpec
    {
        final String name;
        Alignment alignment = Alignment.RIGHT;

        public ColumnSpec(String name) {
            this.name = name;
        }
        
        public String toString()
        {
            return name;
        }
    }

    final List<ColumnSpec> columns = Lists.newArrayList();
    final List<Object> currentRow = Lists.newArrayList();
    final List<List<Object>> data = Lists.newArrayList();

    /**
     * Adds a column to the tabular's layout. Columns must be added before adding data.
     */
    public Tabular addColumn(String name)
    {
        checkNoDataAdded();
        columns.add(new ColumnSpec(name));
        return this;
    }

    /**
     * Sets column flush on the last added column.
     */
    public Tabular flushLeft()
    {
        checkNoDataAdded();
        columns.get(columns.size() - 1).alignment = Alignment.LEFT;
        return this;
    }

    /**
     * Sets column flush on the last added column.
     */
    public Tabular flushRight()
    {
        checkNoDataAdded();
        columns.get(columns.size() - 1).alignment = Alignment.RIGHT;
        return this;
    }

    /**
     * Sets column flush on the last added column.
     */
    public Tabular flushCenter()
    {
        checkNoDataAdded();
        columns.get(columns.size() - 1).alignment = Alignment.CENTER;
        return this;
    }

    public Tabular nextRow()
    {
        data.add(Lists.newArrayList(currentRow));
        currentRow.clear();
        return this;
    }

    /**
     * Sequentially adds column data to the current row.
     */
    public Tabular rowData(Object... columnData)
    {
        currentRow.addAll(Arrays.asList(columnData));
        if (currentRow.size() > columns.size()) throw new IllegalStateException(
            "Row larger than the number of columns: " + columns.size() + ", row: "
                + currentRow.size());
        return this;
    }

    @Override
    public String toString()
    {
        return toString(new StringBuilder()).toString();
    }

    public StringBuilder toString(StringBuilder sb)
    {
        // Calculate column widths and prerender values.
        int [] widths = new int [columns.size()];
        for (int i = 0; i < columns.size(); i++) 
            widths[i] = columns.get(i).name.length();

        for (List<Object> row : data)
        {
            for (int i = 0; i < row.size(); i++)
            {
                String v = asString(row.get(i));
                row.set(i, v);
                widths[i] = Math.max(widths[i], v.length());
            }
        }

        final StringBuilder headerLine = new StringBuilder();
        for (int i = 0; i < columns.size(); i++)
        {
            headerLine.append(columns.get(i).alignment.align(columns.get(i).name, widths[i]));
            headerLine.append(COLUMN_SEPARATOR);
        }
        headerLine.append("\n");
        final String header = headerLine.toString();

        int emittedLines = 0;
        sb.append(header);
        for (List<Object> row : data)
        {
            if ((++emittedLines % HEADER_EVERY_NTH) == 0)
                sb.append(header);
            for (int i = 0; i < row.size(); i++)
            {
                sb.append(columns.get(i).alignment.align((String) row.get(i), widths[i]));
                sb.append(COLUMN_SEPARATOR);
            }
            sb.append("\n");
        }

        return sb;
    }

    private String asString(Object object)
    {
        if (object == null) return "";
        if (object instanceof char[]) 
            return new String((char[]) object);
        return object.toString();
    }

    private void checkNoDataAdded()
    {
        if (currentRow.size() != 0 || data.size() != 0) 
            throw new IllegalStateException("Data already added.");        
    }
}
