/**
 * 
 */
package carrot2.demo.swing.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;

/**
 * A combo box model which displays a set of values
 * of a given hashmap, but also remembers the currently selected key.
 * 
 * @author Dawid Weiss
 */
public final class MapComboModel extends DefaultComboBoxModel {
    private final List ids;
    private final List values;
    int selected = 0;

    public MapComboModel(Map processIdToProcessNameMap) {
        final int size = processIdToProcessNameMap.size();
        this.ids = new ArrayList(size);
        this.values = new ArrayList(size);
        for (Iterator i = processIdToProcessNameMap.entrySet().iterator(); i.hasNext();) {
            final Map.Entry entry = (Map.Entry) i.next();
            ids.add((String) entry.getKey());
            values.add((String) entry.getValue());
        }
    }

    public void setSelectedItem(Object anItem) {
        // We assume there is a small number of values,
        // so this inefficient lookup doesn't really matter.
        selected = values.indexOf(anItem);
    }

    public Object getSelectedItem() {
        return values.get(selected);
    }

    public int getSize() {
        return ids.size();
    }

    public Object getElementAt(int index) {
        return values.get(index);
    }

    public Object getSelectedKey() {
        return ids.get(selected);
    }
    
    public Object getSelectedValue() {
        return getSelectedItem();
    }
}