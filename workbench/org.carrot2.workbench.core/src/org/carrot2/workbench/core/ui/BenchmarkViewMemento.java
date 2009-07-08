package org.carrot2.workbench.core.ui;

import java.util.Map;

import org.simpleframework.xml.*;

/**
 * Persistent state for {@link BenchmarkView}.
 */
@Root
public final class BenchmarkViewMemento
{
    @Element
    public BenchmarkSettings settings;

    @ElementMap
    public Map<String, Boolean> sectionsExpansionState;
}
