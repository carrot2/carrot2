package carrot2.util.attribute;

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
 * Maintains a collection of {@link AttributeValueSet}s.
 */
@Root(name = "attribute-sets")
public class AttributeValueSets
{
    @ElementMap(name = "attribute-sets", entry = "attribute-set", key = "id", inline = true, attribute = true, required = false)
    Map<String, AttributeValueSet> attributeValueSets;

    public AttributeValueSets()
    {
        this.attributeValueSets = Maps.newLinkedHashMap();
    }

    public void addAttributeValueSet(String id, AttributeValueSet attributeValueSet,
        String newLabel, String newDescription)
    {
        final AttributeValueSet newAttributeValueSet = new AttributeValueSet(newLabel,
            newDescription, attributeValueSet.baseAttributeValueSet);
        newAttributeValueSet.overridenAttributeValues
            .putAll(attributeValueSet.overridenAttributeValues);

        addAttributeValueSet(id, newAttributeValueSet);
    }

    public void addAttributeValueSet(String id, AttributeValueSet attributeValueSet)
    {
        if (attributeValueSets.containsKey(id))
        {
            throw new IllegalArgumentException("Attribute value set with id=" + id
                + " already exists");
        }

        attributeValueSets.put(id, attributeValueSet);
    }

    public Set<String> getAttributeValueSetIds()
    {
        return attributeValueSets.keySet();
    }

    public AttributeValueSet getAttributeValueSet(String id)
    {
        return attributeValueSets.get(id);
    }

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
     *
     */
    @Persist
    @SuppressWarnings("unused")
    private void updateBaseAttributeValueSetIds()
    {
        // There won't be too many attribute values sets, so nested loops should be fine
        outer: for (final AttributeValueSet attributeValueSet : attributeValueSets.values())
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
     *
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
     * @param outputStream the stream to serialize this {@link AttributeValueSets} to. The
     *            stream will <strong>not</strong> be closed.
     */
    public void serialize(OutputStream outputStream) throws Exception
    {
        new Persister().write(this, outputStream);
    }

    /**
     * @param inputStream the stream to deserialize a {@link AttributeValueSets} from. The
     *            stream will <strong>not</strong> be closed.
     */
    public static AttributeValueSets deserialize(InputStream inputStream)
        throws Exception
    {
        return new Persister().read(AttributeValueSets.class, inputStream);
    }
}
