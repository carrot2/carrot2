package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.carrot.filter.cluster.rough.Snippet;


/**
 * Represent simle cluster
 */
public interface XCluster {


    /**
     * Return cluster's size (number of members)
     * @return
     */
    public int size();

    /**
     * Return cluster label/description
     * @return
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
         * @return
         */
        public Snippet getSnippet() {
            return snippet;
        }

        /**
         * Return degree of membership to cluster
         * @return
         */
        public double getMembership() {
            return membership;
        }
    }

}
