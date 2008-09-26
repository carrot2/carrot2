package org.carrot2.util.attribute.constraint;


/**
 * This interface provides secondary mapping between enum constants, user-interface
 * (user-friendly) names and attribute values. All these elements have their own
 * constraints (enum constants must be valid Java identifiers, for example) and just
 * overriding {@link Enum#toString()} method is not enough to provide this functionality.
 */
public interface ValueHintMapping
{
    public String getUserFriendlyName();
    public String getAttributeValue();
}
