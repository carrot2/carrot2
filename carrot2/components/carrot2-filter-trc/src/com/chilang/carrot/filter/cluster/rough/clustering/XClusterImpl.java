package com.chilang.carrot.filter.cluster.rough.clustering;

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
