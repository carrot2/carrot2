
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.filter.cluster.rough.transformer;


import org.carrot2.filter.trc.carrot.filter.cluster.rough.data.Document;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.data.Term;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.data.TermExtractor;

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
