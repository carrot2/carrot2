/*
 * PrimaryTopicIndex.java Created on 2004-06-25
 */
package com.stachoodev.carrot.odp.index;

import java.util.*;

/**
 * Defines the interface of an ODP primary category index. This index has a few
 * special properties:
 * 
 * <ul>
 * <li>it is created for some <i>primary key </i> of the ODP data, e.g. the
 * contents of the <code>catid</code> element,
 * <li>during the process of creating the primary index, the appropriate
 * {@link PrimaryIndexBuilder}will also store the content of the ODP database
 * in a way matching the file locations returned by the
 * {@link PrimaryTopicIndex}it builds, thus,
 * <li>for a single ODP database only one {@link PrimaryTopicIndex}can be
 * created
 * <li>{@link TopicIndexBuilder}s will utilize a
 * {@link PrimaryTopicIndex}to access the ODP database and calculate all
 * data it needs, thus
 * <li>a {@link PrimaryTopicIndex}must be built before building any other
 * indices
 * </ul>
 * 
 * @author stachoo
 */
public interface PrimaryTopicIndex
{
    /**
     * Returns the relative location of the file corresponding to the category
     * identified by <code>id</code>. If no category has been found for given
     * <code>id</code>,<code>null</code> should be returned.
     * 
     * @param id
     * @return
     */
    public String getLocation(String id);

    /**
     * Returns an iterator for all file locations stored by this index. If the
     * index does not contain any data an empty iterator should be returned.
     * 
     * @return
     */
    public Iterator getAllLocations();
}