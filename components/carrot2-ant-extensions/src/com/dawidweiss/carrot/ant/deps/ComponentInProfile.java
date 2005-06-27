package com.dawidweiss.carrot.ant.deps;

/**
 * This class represents a dependency on a particular
 * component in the given profile. If {@link #profile}
 * is <code>null</code> the default profile is assumed.
 */
public class ComponentInProfile {
    public final ComponentDependency component;
    public final String profile;
    
    public ComponentInProfile(ComponentDependency component, String profile) {
        this.component = component;
        this.profile = profile;
    }

    public boolean equals(Object o) {
        if (o instanceof ComponentInProfile) {
            final ComponentInProfile other = (ComponentInProfile) o;
            return this.component.equals(other.component) 
                && ((this.profile == null && other.profile == null)
                        || this.profile.equals(other.profile));
        } else {
            return false;
        }
    }

    public int hashCode() {
        if (profile == null) {
            return this.component.hashCode();
        } else {
            return this.component.hashCode() ^ profile.hashCode();
        }
    }

    public String toString() {
        return "'" + component.getName() + "'"
            + (profile != null ? " (profile: " + profile + ")": "");
    }
}
