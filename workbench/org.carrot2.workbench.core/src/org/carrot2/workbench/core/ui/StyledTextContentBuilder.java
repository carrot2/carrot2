
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

package org.carrot2.workbench.core.ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;

import org.carrot2.shaded.guava.common.collect.Lists;

/**
 * A simple class for building content and style ranges for {@link StyledText}.
 */
final class StyledTextContentBuilder
{
    public final static int BOLD = SWT.BOLD;

    private final StringBuilder buffer = new StringBuilder();
    private final List<StyleRange> ranges = Lists.newArrayList();

    public String getText()
    {
        return buffer.toString();
    }

    public StyleRange [] getStyleRanges()
    {
        return ranges.toArray(new StyleRange[ranges.size()]);
    }

    public StyledTextContentBuilder print(String text)
    {
        buffer.append(text);
        return this;
    }

    public StyledTextContentBuilder println(String text)
    {
        print(text);
        print("\n");

        return this;
    }

    public StyledTextContentBuilder println()
    {
        print("\n");
        return this;
    }    

    public StyledTextContentBuilder print(String text, int fontStyle)
    {
        if (text.length() == 0)
        {
            return this;
        }

        print(text);

        final StyleRange range = new StyleRange();
        range.start = buffer.length() - text.length();
        range.length = text.length();
        range.fontStyle = fontStyle;
        ranges.add(range);

        return this;
    }

    public StyledTextContentBuilder println(String text, int fontStyle)
    {
        return print(text + "\n", fontStyle);
    }
}
