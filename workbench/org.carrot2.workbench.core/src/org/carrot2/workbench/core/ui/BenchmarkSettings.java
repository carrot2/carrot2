package org.carrot2.workbench.core.ui;

/**
 * Settings for {@link BenchmarkViewPage}.
 * 
 * TODO: these settings should be configurable in the user interface (bindable/attributeGroups component).
 * TODO: add multi-thread execution (threads count).
 * TODO: add benchmark log data dump (XML or plain text?)
 * TODO: add confidence interval for the avg?
 */
final class BenchmarkSettings
{
    public int benchmarksRounds = 10;
    public int warmupRounds = 5;
}
