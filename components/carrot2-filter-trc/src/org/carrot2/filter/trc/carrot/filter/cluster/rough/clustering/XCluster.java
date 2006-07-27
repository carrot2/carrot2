
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

import org.carrot2.filter.trc.carrot.filter.cluster.rough.Snippet;


/**
 * Represent simle cluster
 */
public interface XCluster {


    /**
     * Return cluster's size (number of members)
     */
    public int size();

    /**
     * Return cluster label/description
     */
    public String[] getLabel();


    /**
     * Return cluster's members in form of array of Member
     */
    public Member[] getMembers();


    /**
     * Member of a cluster (a id/membership pair)
     */
    public static class Member {

        private Snippet snippet;
        private double membership;

        public Member(Snippet snippet, double membership) {
            this.snippet = snippet;
            this.membership = membership;
        }

        /**
         * Return member snippet
         */
        public Snippet getSnippet() {
            return snippet;
        }

        /**
         * Return degree of membership to cluster
         */
        public double getMembership() {
            return membership;
        }
    }

}
