package com.chilang.carrot.filter.cluster.rough.data;

import java.util.Set;

/**
 * Represent document in the information retrieval system
 */
public interface Document {


    Set getStrongTerms();

    void setStrongTerms(Set strongTerms);

    Set getTermSet();

    void setTermSet(Set termSet);

    int getInternalId();

    void setInternalId(int internalId);

}
