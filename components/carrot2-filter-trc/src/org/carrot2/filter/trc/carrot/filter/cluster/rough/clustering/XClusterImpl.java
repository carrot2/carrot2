
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering;

public class XClusterImpl implements XCluster {

    private String[] label;
    private Member[] members;


    public XClusterImpl(String[] label, Member[] members) {
        this.label = label;
        this.members = members;

    }

    public int size() {
        return members.length;
    }

    public String[] getLabel() {
        return label;
    }

    public Member[] getMembers() {
        return members;
    }

}
