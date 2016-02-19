
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

package org.carrot2.source;

import java.util.HashSet;

import org.carrot2.core.Document;

import org.carrot2.shaded.guava.common.base.Predicate;

/**
 * This predicate allows one to filter out documents with non-unique
 * field values.
 */
final class UniqueFieldPredicate implements Predicate<Document>
{
    private final HashSet<Object> unique = new HashSet<Object>();

    private final String fieldName;

    public UniqueFieldPredicate(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public boolean apply(Document document)
    {
        final Object fieldValue = document.getField(fieldName);

        if (fieldValue == null)
        {
            return false;
        }
        else
        {
            return unique.add(fieldValue);
        }
    }
}
