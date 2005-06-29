package com.dawidweiss.carrot.ant.deps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a dependency on a particular
 * component in the given profile. If {@link #profile}
 * is <code>null</code> the default profile is assumed.
 */
public class ComponentInProfile {
    public final ComponentDependency component;
    public final String profile;
    
    private ArrayList dependencies = new ArrayList(); 
    
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

    /**
     * Adds a dependency to this component-profile pair. 
     */
    void addDependency(ComponentInProfile dependency) {
        this.dependencies.add(dependency);
    }
    
    /**
     * Returns a list of {@link ComponentInProfile} objects
     * this component depends in the current profile. 
     */
    public List getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }
}
