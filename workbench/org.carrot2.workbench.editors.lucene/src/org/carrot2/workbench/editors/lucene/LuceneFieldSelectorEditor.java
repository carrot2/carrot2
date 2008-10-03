package org.carrot2.workbench.editors.lucene;

import java.util.ArrayList;
import java.util.Map;

import org.carrot2.workbench.editors.*;
import org.carrot2.workbench.editors.impl.MappedValueComboEditor;

import com.google.common.collect.*;

/**
 * Editor for mapped values (enumerated types and unrestricted strings with enum hints).
 */
public final class LuceneFieldSelectorEditor extends MappedValueComboEditor
{
    /*
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    public AttributeEditorInfo init(Map<String,Object> defaultValues)
    {
        valueRequired = false;
        anyValueAllowed = true;

        BiMap<Object, String> valueToName = Maps.newHashBiMap();
        ArrayList<Object> valueOrder = Lists.newArrayList();
        
        super.setValues(valueToName, valueOrder);
        
        /*
         * 
         */
        super.eventProvider.addAttributeListener(new AttributeListenerAdapter() {
            @Override
            public void valueChanged(AttributeEvent event)
            {
                System.out.println("Attr changed: " + event.key + " -> " + event.value);
            }
        });

        return new AttributeEditorInfo(1, false);
    }
    
    @Override
    public void setValue(Object newValue)
    {
        super.setValue(newValue);
    }
}
