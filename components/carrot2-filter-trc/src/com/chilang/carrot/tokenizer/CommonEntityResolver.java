/**
 * 
 * @author chilang
 * Created 2003-08-22, 01:48:34.
 */
package com.chilang.carrot.tokenizer;

/**
 * Resolve HTML Entity to its text form (&nbsp -> " ", &amp; -> "&", etc)
 */
public class CommonEntityResolver implements HTMLEntityResolver {

    protected static final String[] ENTITIES =
            {"&nbsp;", "&amp;", "&quot;", "&lt;", "&gt;"};
    protected static final String[] TEXT =
            {" ", "&", "\"", "<", ">"};

    /**
     * Resolve most common entities to its form
     * @param entity
     * @return
     */
    public String resolve(String entity) {
        for (int i=0; i<ENTITIES.length; i++) {
            if (entity.equals(ENTITIES[i]))
                return TEXT[i];
        }
        //ignore other entities
        return ".";
    }
}
