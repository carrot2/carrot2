/**
 * 
 * @author chilang
 * Created 2003-07-22, 17:37:33.
 */
package com.chilang.carrot.filter.cluster.rough.transformer;


import com.chilang.carrot.filter.cluster.rough.data.Document;
import com.chilang.carrot.filter.cluster.rough.data.Term;
import com.chilang.carrot.filter.cluster.rough.data.TermExtractor;

import java.util.*;

public class Snippet2DocumentTransformer implements Transformer {




    TermExtractor extractor;
    public Snippet2DocumentTransformer(TermExtractor termExtractor) {

        extractor = termExtractor;

    }

    public Object transform(Object obj) {
        if (!(obj instanceof Document))
            return obj;
        Document doc = (Document)obj;
        doc.setTermSet(new HashSet((Collection)extractor.extractFromSnippet(doc)));
        return doc;
    }

    private Set extractTermContent(Set terms) {
        Set strong = new HashSet();
        for (Iterator i = terms.iterator(); i.hasNext(); ) {
            strong.add(((Term)i.next()).getStemmedTerm());
        }
        return strong;
    }
    /**
     * Merge two map by merging keys, values for the same key are added together
     * First argument is modified.
     * @param map1
     * @param map2
     * @return
     */
    private Map merge(Map map1, Map map2) {
        for (Iterator iter = map2.keySet().iterator(); iter.hasNext(); ) {
            Term term = (Term) iter.next();
            Integer freq = (Integer) map1.get(term);
            //first map doesn't not contain given key
            if (freq == null) {
                //add key,value mapping
                map1.put(term, new Integer(1));
            } else {
                //else add values together
                map1.put(term, new Integer(((Integer)map1.get(term)).intValue() + freq.intValue()));
            }
        }
        return map1;
    }


}
