package org.carrot2.util.attribute;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.load.*;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Maintains a collection of {@link AttributeValueSet}s and provides methods for
 * serializing and deserializing attribute value sets from XML streams.
 */
@Root(name = "attribute-sets")
public class AttributeValueSets
{
    @ElementMap(name = "attribute-sets", entry = "attribute-set", key = "id", inline = true, attribute = true, required = false)
    Map<String, AttributeValueSet> attributeValueSets;

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
        return attributeValueSets.get(id);
    }

    /**
     * Removes the {@link AttributeValueSet} corresponding to the provided <code>id</code>
     * from this collection. If any other {@link AttributeValueSet}s in this collection
     * are based on the set being removed, the associations are corrected accordingly,
     * i.e. sets based on the set being removed become based on the set the removed set is
     * based on. If no {@link AttributeValueSet} corresponds to the provided
     * <code>id</code>, no action is taken.
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

        attributeValueSets.remove(attributeValueSet);

        // Also, we need to fix the "based on" hierarchy here
        final AttributeValueSet newBaseAttributeValueSet = attributeValueSet.baseAttributeValueSet;
        for (final AttributeValueSet set : attributeValueSets.values())
        {
            set.baseAttributeValueSet = newBaseAttributeValueSet;
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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
     * @param outputStream the stream to serialize this {@link AttributeValueSets} to. The
     *            stream will <strong>not</strong> be closed.
     * @throws Exception in case of any problems with serialization
     */
    public void serialize(OutputStream outputStream) throws Exception
    {
        new Persister().write(this, outputStream);
    }

    /**
     * Deserializes a collection of {@link AttributeValueSet}s from an XML stream.
     * 
     * @param inputStream the stream to deserialize a {@link AttributeValueSets} from. The
     *            stream will <strong>not</strong> be closed.
     * @return deserialized collection of {@link AttributeValueSet}s
     * @throws Exception is case of any problems with deserialization
     */
    public static AttributeValueSets deserialize(InputStream inputStream)
        throws Exception
    {
        return new Persister().read(AttributeValueSets.class, inputStream);
    }
}
