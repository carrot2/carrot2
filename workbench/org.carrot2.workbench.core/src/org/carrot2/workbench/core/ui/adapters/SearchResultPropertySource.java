package org.carrot2.workbench.core.ui.adapters;

import java.util.Map;

import org.carrot2.core.ProcessingResult;
import org.carrot2.workbench.core.ui.SearchInput;
import org.carrot2.workbench.core.ui.SearchResult;

import com.google.common.collect.Maps;

public class SearchResultPropertySource extends MapPropertySource
{
    public SearchResultPropertySource(SearchResult searchResult)
    {
        final Map<String, Object> inputProperties = Maps.newHashMap();
        final SearchInput input = searchResult.getInput();
        inputProperties.put("source ID", input.getSourceId());
        inputProperties.put("algorithm ID", input.getAlgorithmId());
        inputProperties.put("request attributes", input.getAttributeValueSet().getAttributeValues());
        add(inputProperties, "Input");

        final Map<String, Object> resultProperties = Maps.newHashMap();
        final ProcessingResult output = searchResult.getProcessingResult();
        if (output != null)
        {
            resultProperties.put("response attributes", 
                searchResult.getProcessingResult().getAttributes());
            add(resultProperties, "Output");
        }
    }
}