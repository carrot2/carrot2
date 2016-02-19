
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

package org.carrot2.util.attribute;

import java.io.*;
import java.util.*;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.*;

import org.carrot2.shaded.guava.common.collect.Maps;
import org.carrot2.shaded.guava.common.collect.Sets;

/**
 * Maintains a collection of {@link AttributeValueSet}s and provides methods for
 * serializing and deserializing attribute value sets from XML streams.
 */
@Root(name = "attribute-sets")
public class AttributeValueSets
{
    @ElementMap(name = "attribute-sets", entry = "attribute-set", key = "id", inline = true, attribute = true, required = false)
    Map<String, AttributeValueSet> attributeValueSets;

    @org.simpleframework.xml.Attribute(name = "default", required = false)
    String defaultAttributeValueSetId;

    /**
     * Creates an empty collection of attribute value sets.
     */
    public AttributeValueSets()
    {
        this.attributeValueSets = Maps.newLinkedHashMap();
    }

    /**
     * Adds an {@link AttributeValueSet} to this collection.
     * 
     * @param id unique identifier of the {@link AttributeValueSet} within the collection.
     * @param attributeValueSet {@link AttributeValueSet} to be added
     */
    public void addAttributeValueSet(String id, AttributeValueSet attributeValueSet)
    {
        if (attributeValueSets.containsKey(id))
        {
            throw new IllegalArgumentException("Attribute value set with id=" + id
                + " already exists");
        }

        attributeValueSets.put(id, attributeValueSet);
    }

    /**
     * Adds an {@link AttributeValueSet} to this collection replacing its label and
     * description. This method may sometimes be useful because {@link AttributeValueSet}s
     * are immutable with respect to their label and description.
     * 
     * @param id unique identifier of the {@link AttributeValueSet} within the collection.
     * @param attributeValueSet {@link AttributeValueSet} to be added
     * @param newLabel new label for the {@link AttributeValueSet}
     * @param newDescription new description for the {@link AttributeValueSet}
     */
    public void addAttributeValueSet(String id, AttributeValueSet attributeValueSet,
        String newLabel, String newDescription)
    {
        final AttributeValueSet newAttributeValueSet = new AttributeValueSet(newLabel,
            newDescription, attributeValueSet.baseAttributeValueSet);
        newAttributeValueSet.overridenAttributeValues
            .putAll(attributeValueSet.overridenAttributeValues);

        addAttributeValueSet(id, newAttributeValueSet);
    }

    /**
     * Returns identifiers of all {@link AttributeValueSet}s in this collection.
     * 
     * @return identifiers of all {@link AttributeValueSet}s in this collection.
     */
    public Set<String> getAttributeValueSetIds()
    {
        return attributeValueSets.keySet();
    }

    /**
     * Returns the identifier of the default {@link AttributeValueSet} within this
     * {@link AttributeValueSets}. The default identifier can be <code>null</code>.
     */
    public String getDefaultAttributeValueSetId()
    {
        return defaultAttributeValueSetId;
    }

    /**
     * Sets the default attribute value set id for this {@link AttributeValueSets}. An
     * {@link AttributeValueSet} with this id must exist in this
     * {@link AttributeValueSets}.
     */
    public void setDefaultAttributeValueSetId(String defaultAttributeValueSetId)
    {
        if (defaultAttributeValueSetId != null
            && !attributeValueSets.containsKey(defaultAttributeValueSetId))
        {
            throw new IllegalArgumentException("Attribute value set with id: "
                + defaultAttributeValueSetId + " does not exist.");
        }
        this.defaultAttributeValueSetId = defaultAttributeValueSetId;
    }

    /**
     * Returns the {@link AttributeValueSet} corresponding to the provided <code>id</code>
     * or <code>null</code> if no {@link AttributeValueSet} corresponds to the
     * <code>id</code>.
     * 
     * @return the {@link AttributeValueSet} corresponding to the provided <code>id</code>
     *         or <code>null</code> if no {@link AttributeValueSet} corresponds to the
     *         <code>id</code>.
     */
    public AttributeValueSet getAttributeValueSet(String id)
    {
        return getAttributeValueSet(id, false);
    }

    /**
     * Returns the {@link AttributeValueSet} corresponding to the provided <code>id</code>
     * or the default {@link AttributeValueSet} (possibly <code>null</code>) if no
     * {@link AttributeValueSet} corresponds to the <code>id</code>.
     * 
     * @param id identifier of the {@link AttributeValueSet} to return
     * @param useDefault if <code>true</code>, the default {@link AttributeValueSet} will
     *            be returned if the {@link AttributeValueSet} with the provided id does
     *            not exist.
     * @return the {@link AttributeValueSet} corresponding to the provided <code>id</code>
     *         or the default {@link AttributeValueSet} (possibly <code>null</code>) if no
     *         {@link AttributeValueSet} corresponds to the <code>id</code>.
     */
    public AttributeValueSet getAttributeValueSet(String id, boolean useDefault)
    {
        if (attributeValueSets.containsKey(id))
        {
            return attributeValueSets.get(id);
        }
        else
        {
            return (useDefault ? getDefaultAttributeValueSet() : null);
        }
    }

    /**
     * Returns the default {@link AttributeValueSet} of this {@link AttributeValueSets} or
     * the first available {@link AttributeValueSet} if
     * {@link #getDefaultAttributeValueSetId()} is <code>null</code>. If this
     * {@link AttributeValueSets} is empty, <code>null</code> will be returned.
     */
    public AttributeValueSet getDefaultAttributeValueSet()
    {
        AttributeValueSet result = null;
        if (defaultAttributeValueSetId != null)
        {
            result = getAttributeValueSet(defaultAttributeValueSetId);
        }

        // Try the first attribute set
        if (result == null)
        {
            final Iterator<AttributeValueSet> iterator = attributeValueSets.values()
                .iterator();
            if (iterator.hasNext())
            {
                result = iterator.next();
            }
        }

        return result;
    }

    /**
     * Removes the {@link AttributeValueSet} corresponding to the provided <code>id</code>
     * from this collection. If any other {@link AttributeValueSet}s in this collection
     * are based on the set being removed, the associations are corrected accordingly,
     * i.e. sets based on the set being removed become based on the set the removed set is
     * based on. Also, if the removed {@link AttributeValueSet} was the default,
     * <code>null</code> will be set as this {@link AttributeValueSets}' default
     * {@link AttributeValueSet}. If no {@link AttributeValueSet} corresponds to the
     * provided <code>id</code>, no action is taken.
     * 
     * @param id identifier of the {@link AttributeValueSet} to be removed
     */
    public void removeAttributeValueSet(String id)
    {
        final AttributeValueSet attributeValueSet = attributeValueSets.get(id);
        if (attributeValueSet == null)
        {
            return;
        }

        attributeValueSets.remove(id);

        // Also, we need to fix the "based on" hierarchy here
        final AttributeValueSet newBaseAttributeValueSet = attributeValueSet.baseAttributeValueSet;
        for (final AttributeValueSet set : attributeValueSets.values())
        {
            set.baseAttributeValueSet = newBaseAttributeValueSet;
        }

        if (id.equals(defaultAttributeValueSetId))
        {
            defaultAttributeValueSetId = null;
        }
    }

    /**
     * Returns all {@link AttributeValueSet}s from this collection that are based,
     * directly or indirectly, on the provided <code>baseAttributeValueSet</code>.
     * 
     * @param baseAttributeValueSet the base attribute value set
     */
    public Set<AttributeValueSet> getAttributeValueSetsBasedOn(
        AttributeValueSet baseAttributeValueSet)
    {
        final Set<AttributeValueSet> result = Sets.newHashSet();
        for (final AttributeValueSet set : attributeValueSets.values())
        {
            if (set.baseAttributeValueSet == baseAttributeValueSet)
            {
                result.add(set);
                result.addAll(getAttributeValueSetsBasedOn(set));
            }
        }

        return result;
    }

    /**
     * Updates base attribute value set ids before persisting.
     */
    @Persist
    private void updateBaseAttributeValueSetIds()
    {
        // There won't be too many attribute values sets, so nested loops should be fine
        outer: for (final AttributeValueSet attributeValueSet : attributeValueSets
            .values())
        {
            if (attributeValueSet.baseAttributeValueSet == null)
            {
                continue;
            }

            for (final Map.Entry<String, AttributeValueSet> entry : attributeValueSets
                .entrySet())
            {
                if (attributeValueSet.baseAttributeValueSet == entry.getValue())
                {
                    attributeValueSet.baseAttributeValueSetId = entry.getKey();
                    continue outer;
                }
            }
        }
    }

    /**
     * Restores base attribute value set references based on ids on deserialization.
     */
    @Commit
    private void restoreBaseAttributeValueSets()
    {
        for (final AttributeValueSet attributeValueSet : attributeValueSets.values())
        {
            attributeValueSet.baseAttributeValueSet = attributeValueSets
                .get(attributeValueSet.baseAttributeValueSetId);
        }
    }

    /**
     * Serializes this collection of {@link AttributeValueSet}s to an XML stream.
     * 
     * @param stream the stream to serialize this {@link AttributeValueSets} to. The stream
     *            will <strong>not</strong> be closed.
     * @throws Exception in case of any problems with serialization
     */
    public void serialize(OutputStream stream) throws Exception
    {
        new Persister().write(this, stream);
    }

    /**
     * Deserializes a collection of {@link AttributeValueSet}s from an XML stream.
     * 
     * @param inputStream the {@link InputStream} to deserialize a
     *            {@link AttributeValueSets} from. The stream will <strong>not</strong> be
     *            closed.
     * @return Deserialized collection of {@link AttributeValueSet}s
     * @throws Exception is case of any problems with deserialization.
     */
    public static AttributeValueSets deserialize(InputStream inputStream)
        throws Exception
    {
        final AttributeValueSets attributeValueSet = new Persister().read(
            AttributeValueSets.class, inputStream);

        checkDefaultAttributeValueSetExists(attributeValueSet);

        return attributeValueSet;
    }

    private static void checkDefaultAttributeValueSetExists(
        final AttributeValueSets attributeValueSet)
    {
        if (attributeValueSet.defaultAttributeValueSetId != null
            && !attributeValueSet.attributeValueSets
                .containsKey(attributeValueSet.defaultAttributeValueSetId))
        {
            throw new RuntimeException("Default attribute value set not found: "
                + attributeValueSet.defaultAttributeValueSetId);
        }
    }

    /*
     * 
     */
    @Override
    public String toString()
    {
        return "AttributeValueSet [set IDs: "
            + Arrays.toString(this.getAttributeValueSetIds().toArray()) + "]";
    }
}
