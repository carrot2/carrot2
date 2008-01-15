package org.carrot2.source;

import java.util.HashSet;

import org.carrot2.core.Document;

import com.google.common.base.Predicate;

/**
 * This predicate allows one to filter out documents with non-unique 
 * field values.
 */
@SuppressWarnings("unchecked")
public final class UniqueFieldPredicate implements Predicate<Document>
{
    private final HashSet unique = new HashSet();

    private final String fieldName;
    
    public UniqueFieldPredicate(String fieldName)
    {
        this.fieldName = fieldName;
    }
    
    @Override
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
