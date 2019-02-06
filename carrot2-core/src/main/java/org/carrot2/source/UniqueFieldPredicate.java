
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source;

import java.util.HashSet;
import java.util.function.Predicate;

import org.carrot2.core.Document;

/**
 * This predicate allows one to filter out documents with non-unique
 * field values.
 */
final class UniqueFieldPredicate implements Predicate<Document>
{
    private final HashSet<Object> unique = new HashSet<>();

    private final String fieldName;

    public UniqueFieldPredicate(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public boolean test(Document document)
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
