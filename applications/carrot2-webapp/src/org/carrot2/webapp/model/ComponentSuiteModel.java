package org.carrot2.webapp.model;

import java.util.List;
import java.util.Map;

import org.simpleframework.xml.ElementList;

import com.google.common.collect.Maps;

/**
 *
 */
public class ComponentSuiteModel
{
    @ElementList(entry = "algorithm")
    public final List<ProcessingComponentModel> algorithms;

    @ElementList(entry = "source")
    public final List<DocumentSourceModel> sources;

    public final Map<String, ProcessingComponentModel> algorithmsById;
    public final Map<String, DocumentSourceModel> sourcesById;

    public ComponentSuiteModel(List<DocumentSourceModel> sources,
        List<ProcessingComponentModel> algorithms)
    {
        this.sources = sources;
        this.algorithms = algorithms;

        this.algorithmsById = Maps.newHashMap();
        for (ProcessingComponentModel algorithm : algorithms)
        {
            algorithmsById.put(algorithm.id, algorithm);
        }

        this.sourcesById = Maps.newHashMap();
        for (DocumentSourceModel source : sources)
        {
            sourcesById.put(source.id, source);
        }
    }
}
