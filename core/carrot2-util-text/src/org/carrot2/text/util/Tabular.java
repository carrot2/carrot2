package org.carrot2.text.util;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Tabular data dump with automatically adjusted column widths.
 */
public class Tabular
{
    final List<String> columns = Lists.newArrayList();
    final List<Object> currentRow = Lists.newArrayList();
    final List<List<Object>> data = Lists.newArrayList();

    /**
     * Adds a column to the tabular's layout. Columns must be added before adding data.
     */
    public Tabular addColumn(String name)
    {
        if (currentRow.size() != 0 || data.size() != 0) throw new IllegalStateException(
            "Data already added.");
        columns.add(name);
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
    public void rowData(Object... columnData)
    {
        currentRow.addAll(Arrays.asList(columnData));
        if (currentRow.size() > columns.size()) throw new IllegalStateException(
            "Row larger than the number of columns: " + columns.size() + ", row: "
                + currentRow.size());
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
            widths[i] = columns.get(i).length();

        for (List<Object> row : data)
        {
            for (int i = 0; i < row.size(); i++)
            {
                String v = asString(row.get(i));
                row.set(i, v);
                widths[i] = Math.max(widths[i], v.length());
            }
        }

        for (int i = 0; i < columns.size(); i++)
        {
            sb.append(Strings.padStart(columns.get(i), 1 + widths[i], ' '));
        }
        sb.append("\n");

        for (List<Object> row : data)
        {
            for (int i = 0; i < row.size(); i++)
            {
                sb.append(Strings.padStart((String) row.get(i), 1 + widths[i], ' '));
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
}
