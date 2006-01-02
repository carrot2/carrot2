
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.core.local.profiling;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;

/**
 * An implmementaion of the
 * {@link com.dawidweiss.carrot.core.local.RequestContext}interface that
 * enables components to gather and store profiling and debugging information.
 * Each component in the processing chain will have a separate instance of
 * {@link Profile}obtainable by a call to {@link #getProfile(LocalComponent)}.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class ProfiledRequestContext extends RequestContextBase
{
    /** A mapping between component instances (keys) and their profiles (values) */
    private Map profiles;

    /**
     * References to profiles ordered (hopefully) according to the data flow.
     * TODO: replace this with a LinkedHashMap for profiles?
     */
    private List profileList;

    /**
     * Creates a new instance with given local controller instance and given
     * request parameters.
     * 
     * @param controller the instance of {@link LocalControllerBase}that
     *            created this context
     * @param requestParameters request parameters for this context
     */
    public ProfiledRequestContext(LocalControllerBase controller,
        Map requestParameters)
    {
        super(controller, requestParameters);
        profiles = new HashMap();
    }

    /**
     * Returns a profile for given component instance.
     * 
     * @param instance local component instance
     * @return profile
     */
    public Profile getProfile(LocalComponent instance)
    {
        return (Profile) profiles.get(instance);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.RequestContext#getComponentInstance(java.lang.String)
     */
    public LocalComponent getComponentInstance(String key)
        throws MissingComponentException, Exception
    {
        LocalComponent localComponent = super.getComponentInstance(key);
        profiles.put(localComponent, new Profile(localComponent.getName()));
        return localComponent;
    }

    /**
     * Returns a list of all profiles. The list, <i>should </i> be ordered
     * according to the order of components in the chain. The ordering behaviour
     * depends on the implementation of {@link LocalProcessBase}.
     * 
     * @return
     */
    public List getProfiles()
    {
        synchronized (this)
        {
            if (profileList == null)
            {
                profileList = new ArrayList();

                // Local component references in the borrowedComponents list
                // determine the order in which the profiles will be returned.
                // This little hack depends on the implementation of
                // LocalProcessBase. The worst thing that can happen if that
                // implementation changes, however, is that the order of the
                // returned profiles will not match the order of the data flow.
                for (int i = 0; i < borrowedComponents.size(); i += 2)
                {
                    profileList
                        .add(profiles.get(borrowedComponents.get(i + 1)));
                }

                // LocalProcessBase adds the output component as second. Move
                // the corresponding profile the the end of the list.
                if (profileList.size() > 2)
                {
                    profileList.add(profileList.remove(1));
                }
            }
        }

        return profileList;
    }
}