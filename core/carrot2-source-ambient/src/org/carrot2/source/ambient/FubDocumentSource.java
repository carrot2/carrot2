
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.ambient;

import java.util.List;
import java.util.Set;

import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.IntRange;

/**
 * A base document source for test collections developed at Fondazione Ugo Bordoni. 
 */
@Bindable(prefix = "FubDocumentSource", inherit = AttributeNames.class)
public class FubDocumentSource extends ProcessingComponentBase implements IDocumentSource
{
    @Processing
    @Output
    @Attribute(key = AttributeNames.DOCUMENTS, inherit = true)
    @Internal
    public List<Document> documents;

    /**
     * Topics and subtopics covered in the output documents. The set is computed for the
     * output {@link #documents} and it may vary for the same main topic based e.g. on the
     * requested number of requested results or {@link #minTopicSize}.
     * 
     * @level Advanced
     * @group Topic ID
     */
    @Processing
    @Output
    @Attribute
    public Set<Object> topicIds;

    @Processing
    @Output
    @Attribute(key = AttributeNames.QUERY, inherit = true)
    public String query;

    /**
     * Minimum topic size. Documents belonging to a topic with fewer documents than
     * minimum topic size will not be returned.
     * 
     * @level Medium
     * @group Filtering
     */
    @Input
    @Processing
    @Attribute
    @IntRange(min = 1)
    public int minTopicSize = 1;

    /**
     * Include documents without topics.
     * 
     * @level Medium
     * @group Filtering
     */
    public boolean includeDocumentsWithoutTopic = false;

    protected void processInternal(FubTestCollection data, int topicId,
        int requestedResults) throws ProcessingException
    {
        this.documents = data.getDocumentsForTopic(topicId, requestedResults,
            minTopicSize, includeDocumentsWithoutTopic);
        
        for (Document document : documents)
        {
            document.setLanguage(LanguageCode.ENGLISH);
        }
    }
}
