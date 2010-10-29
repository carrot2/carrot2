package org.carrot2.util.simplexml;

import java.util.Map;

import org.carrot2.util.resource.ResourceLookup;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import com.google.common.collect.ImmutableMap;

/**
 * Simple XML session context helpers.
 */
public final class PersisterHelpers
{
    /**
     * Resource lookup key in the serialization/ deserialization session.
     */
    private static final String RESOURCE_LOOKUP_KEY = ResourceLookup.class.getName();

    /**
     * Acquire {@link ResourceLookup} from a serialization/ deserialization session. 
     */
    public static ResourceLookup getResourceLookup(Map<Object, Object> session)
    {
        ResourceLookup resourceLookup = (ResourceLookup) session.get(RESOURCE_LOOKUP_KEY);
        if (resourceLookup == null)
        {
            throw new RuntimeException("Session does not carry resource lookup context.");
        }
        return resourceLookup;
    }

    /**
     * Create a persister with the given {@link ResourceLookup} key.
     */
    public static Persister createPersister(
        final ResourceLookup resourceLookup, Strategy strategy)
    {
        return createPersister(
            ImmutableMap.<Object, Object> of(RESOURCE_LOOKUP_KEY, resourceLookup),
            strategy);
    }

    /**
     * Create a persister with an arbitrary session map and deserialization strategy.
     */
    private static Persister createPersister(
        Map<Object, Object> attributes, Strategy strategy)
    {
        return new Persister(new SessionInitStrategy(strategy, attributes));
    }
}
