package com.chilang.carrot.filter.cluster.rough.clustering;

import com.chilang.carrot.filter.cluster.rough.FeatureVector;
import com.chilang.carrot.filter.cluster.rough.Snippet;
import com.chilang.carrot.filter.cluster.rough.SparseFeatureVector;
import com.chilang.carrot.filter.cluster.rough.data.Term;
import com.chilang.util.ArrayUtils;

import java.text.NumberFormat;
import java.util.*;

public class SimpleCluster implements Cluster {

    /**
     * Object representating cluster
     */
    protected Clusterable representative;
    /**
     * Map of members of cluster -> membership ratio
     */
    protected Snippet[] members;

    /**
     * Vector of membership values (size = total number of clustering items).
     * cell[i] = membership of item i in this cluster
     */
    protected FeatureVector membership;

    protected Map representativeTermMap;

    protected String[] labels;

    private static final NumberFormat nf = NumberFormat.getNumberInstance();

    static {
        nf.setMaximumFractionDigits(4);
    }

    public int size() {
        return membership.asBitVector().cardinality();
    }

    public SimpleCluster(Clusterable cluster) {
        this.representative = cluster;
        this.members = new Snippet[0];
        this.membership = new SparseFeatureVector(100);
        this.labels = new String[0];
    }

    public Snippet[] getMembers() {
        return members;
    }

    public void setMembers(Snippet[] objs) {
        members = objs;
    }

    public Clusterable getRepresentative() {
        return representative;
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
//        b.append("[  " + extractTerm(representativeTermMap, 5) + "  ]{" + representativeTermMap.size() + "}\n");
        b.append("[  " + ArrayUtils.toString(labels) + "  ]\n");

        Arrays.sort(members, new Comparator() {
            public int compare(Object o1, Object o2) {
                return membership.getWeight(((Snippet) o1).getInternalId()) > membership.getWeight(((Snippet) o2).getInternalId()) ? -1 : 1;
            }
        });
        for (int i = 0; i < members.length; i++) {
            Snippet member = members[i];
            b.append("  ( " + nf.format(membership.getWeight(member.getInternalId())) + " )  " + member.getTitle() + "\n");
        }
//        b.append("\n M["+membership.asBitVector().cardinality()+"] : "+membership.asBitVector()+"]");
        b.append("\n----------------\n");
        return b.toString();
    }

    private String extractTerm(final Map terms, int n) {
        Set sorted = new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                return (((Double) terms.get(o1)).doubleValue() > ((Double) terms.get(o2)).doubleValue()) ? -1 : 1;
            }
        });
        sorted.addAll(terms.keySet());

        StringBuffer b = new StringBuffer();
        Iterator iter = sorted.iterator();
        for (int i = 0; i < n && iter.hasNext(); i++) {
            Term term = (Term) iter.next();
            b.append(term.getOriginalTerm() + "(" + nf.format(terms.get(term)) + "), ");
        }
        return b.toString();
    }

    public FeatureVector getMembership() {
        return membership;
    }

    public void setMembership(FeatureVector featureVector) {
        this.membership = featureVector;
    }


    public Map getRepresentativeTerm() {
        return representativeTermMap;
    }

    public void setRepresentativeTerm(Map termWeightMap) {
        representativeTermMap = termWeightMap;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }
}
