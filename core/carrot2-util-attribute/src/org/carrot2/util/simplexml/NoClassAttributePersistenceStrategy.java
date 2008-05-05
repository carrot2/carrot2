package org.carrot2.util.simplexml;

import java.util.Map;

import org.simpleframework.xml.graph.CycleStrategy;
import org.simpleframework.xml.stream.NodeMap;

/**
 * SimpleXML persister strategy that suppresses writing class attributes. We use
 * SimpleXML only to output XML, so we don't need information on entity classes.
 */
public final class NoClassAttributePersistenceStrategy extends CycleStrategy
{
    private static final String SIMPLE_XML_ENTITY_ID = "sid";
    private static final String SIMPLE_XML_ENTITY_REF_ID = "sidref";

    public static final NoClassAttributePersistenceStrategy INSTANCE = new NoClassAttributePersistenceStrategy(
        SIMPLE_XML_ENTITY_ID, SIMPLE_XML_ENTITY_REF_ID);

    private NoClassAttributePersistenceStrategy(String mark, String refer)
    {
        super(mark, refer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean setElement(Class field, Object value, NodeMap node, Map map)
    {
        boolean result = super.setElement(field, value, node, map);
        node.remove(SIMPLE_XML_ENTITY_ID);
        node.remove(SIMPLE_XML_ENTITY_REF_ID);
        node.remove("class");
        return result;
    }
}