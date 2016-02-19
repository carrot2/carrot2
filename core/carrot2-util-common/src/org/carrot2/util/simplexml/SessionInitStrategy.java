
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

package org.carrot2.util.simplexml;

import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.NodeMap;
import org.simpleframework.xml.stream.OutputNode;

import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * Proxy for initializing session values.
 */
final class SessionInitStrategy implements Strategy
{
    private final Strategy delegate;
    private final HashMap<Object, Object> sessionValues;

    private boolean sessionInitialized;

    public SessionInitStrategy(Strategy delegate, Map<Object, Object> sessionValues)
    {
        this.delegate = delegate;
        this.sessionValues = Maps.newHashMap(sessionValues);
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Value read(Type type, NodeMap node, Map session) throws Exception
    {
        if (!sessionInitialized)
        {
            sessionInitialized = true;
            session.putAll(sessionValues);
        }

        return delegate.read(type, node, session);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean write(Type type, Object target, NodeMap<OutputNode> nodeMap, Map session)
        throws Exception
    {
        return delegate.write(type, target, nodeMap, session);
    }
}
