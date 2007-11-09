package org.carrot2.core;

import java.util.Map;

/**
 * 
 */
public interface Controller
{
    /*
     * Just a note for the future. I guess it would make a lot of sense
     * to be able to specify injection dependencies as request parameters somehow.
     * Example: the value of a parameter "tokenizer" (of type "ImplClassTypeMetadata")
     * would point to the implementation class (or provider?) to use for the injection
     * container. 
     * 
     * My preliminary reading of pico's documentation indicates this would be
     * feasible and sensible. I'll write a draft solution in guice and pico
     * tomorrow, we will see what they are like.
     */
    
    
    // listener for documents
    ProcessingResult process(Map<String, Object> requestParameters,
        DocumentSource documentSource, ClusteringAlgorithm clusteringAlgorithm);
}
