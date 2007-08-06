
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.lingo.common;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Represents a result of clustering.
 */
public class Cluster implements Comparable {
    /**
     * Documents belonging to the cluster
     */
    protected ArrayList snippets;

    /**
     * Snippet scores
     */
    protected ArrayList snippetScores;

    /**
     * Clusters belonging to the cluster (subclusters)
     */
    protected ArrayList clusters;

    /** */

    /** DOCUMENT ME! */
    protected ArrayList labels;

    /** */

    /** DOCUMENT ME! */
    protected ArrayList tfidfs;

    /** */

    /** DOCUMENT ME! */
    protected double tfidfSum;

    /** */

    /** DOCUMENT ME! */
    protected double score = -1;

    /** */

    /** DOCUMENT ME! */
    protected boolean otherTopics = false;

    /**
     */
    public Cluster() {
        this(null);
    }

    /**
     * Creates a flat cluster.
     *
     * @param snippets the deocuments belonging to the new cluster
     */
    public Cluster(Snippet[] snippets) {
        this(snippets, (String[]) null);
    }

    /**
     * Creates a flat cluster.
     *
     * @param snippets the deocuments belonging to the new cluster
     */
    public Cluster(Snippet[] snippets, String[] labels) {
        this(snippets, null, labels);
    }

    /**
     * Creates a hierarchical cluster.
     *
     * @param snippets the deocuments belonging to the new cluster
     * @param clusters the clusters belonging to the new cluster
     */
    public Cluster(Snippet[] snippets, Cluster[] clusters) {
        this(snippets, clusters, null);
    }

    /**
     * Creates a hierarchical cluster.
     *
     * @param snippets the deocuments belonging to the new cluster
     * @param clusters the clusters belonging to the new cluster
     */
    public Cluster(Snippet[] snippets, Cluster[] clusters, String[] labels) {
        snippetScores = new ArrayList();

        // Snippets
        if (snippets == null) {
            this.snippets = new ArrayList();
        } else {
            this.snippets = new ArrayList(Arrays.asList(snippets));
        }

        // Clusters
        if (clusters == null) {
            this.clusters = new ArrayList();
        } else {
            this.clusters = new ArrayList(Arrays.asList(clusters));
        }

        // Labels
        if (labels == null) {
            this.labels = new ArrayList();
        } else {
            this.labels = new ArrayList(Arrays.asList(labels));
        }
    }

    /**
     * Returns the snippets belonging to this cluster.
     *
     * @return Document [] the snippets belonging to this cluster
     */
    public Snippet[] getSnippets() {
        return (Snippet[]) snippets.toArray(new Snippet[snippets.size()]);
    }

    /**
     * Returns the snippets belonging to this cluster.
     *
     * @return Snippet [] the snippets belonging to this cluster
     */
    public ArrayList getSnippetsAsArrayList() {
        return snippets;
    }

    /**
     * Returns the clusters belonging to this cluster.
     *
     * @return Cluster [] the clusters belonging to this cluster
     */
    public Cluster[] getClusters() {
        return (Cluster[]) clusters.toArray(new Cluster[clusters.size()]);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ArrayList getClustersAsList() {
        return this.clusters;
    }

    /**
     * Returns the labels.
     *
     * @return String[]
     */
    public String[] getLabels() {
        return (String[]) labels.toArray(new String[labels.size()]);
    }

    /**
     * Returns the labels as a list.
     */
    public ArrayList getLabelsAsList() {
        return labels;
    }

    /**
     * Method addCluster.
     *
     * @param cluster
     */
    public void addCluster(Cluster cluster) {
        clusters.add(cluster);
    }

    /**
     * Method addLabel.
     *
     * @param label
     */
    public void addLabel(String label) {
        labels.add(label);
    }

    /**
     * Method addSnippet.
     *
     * @param snippet
     */
    public void addSnippet(Snippet snippet) {
        addSnippet(snippet, 0);
    }

    /**
     * Method addSnippet.
     *
     * @param snippet
     */
    public void addSnippet(Snippet snippet, double score) {
        // Find appropriate position
        int i;

        for (i = 0; i < snippetScores.size(); i++) {
            double currentScore = ((Double) snippetScores.get(i)).doubleValue();

            if (currentScore < score) {
                break;
            }
        }

        if (i == snippetScores.size()) {
            snippets.add(snippet);
            snippetScores.add(new Double(score));
        } else {
            snippets.add(i, snippet);
            snippetScores.add(i, new Double(score));
        }
    }

    /**
     * @param snippet
     */
    public void removeSnippet(Snippet snippet) {
        snippets.remove(snippet);
    }

    /**
     * @param cluster
     */
    public void removeCluster(Cluster cluster) {
        clusters.remove(cluster);
    }

    /**
     * Method addLabel.
     *
     * @param label
     * @param tfidf
     */
    public void addLabel(String label, double tfidf) {
        addLabel(label);
        tfidfs.add(new Double(tfidf));
        tfidfSum += tfidf;
    }

    /**
     * @param snippet
     *
     * @return double
     */
    public double getSnippetScore(int snippet) {
        return ((Double) snippetScores.get(snippet)).doubleValue();
    }

    /**
     * Method getScore.
     *
     * @return double
     */
    public double getScore() {
        return score;
    }

    /**
     * Sets the score.
     *
     * @param score The score to set
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        if (!(o instanceof Cluster)) {
            throw new ClassCastException(o.getClass().toString());
        }

        Cluster c2 = (Cluster) o;

        // Sort descendingly
        if (c2.getScore() < getScore()) {
            return -1;
        } else if (c2.getScore() > getScore()) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);

        StringBuffer string = new StringBuffer();

        String[] labels = getLabels();
        string.append("[ ");

        for (int i = 0; i < labels.length; i++) {
            string.append(labels[i] + " ");
        }

        string.append("]\n\n");

        Cluster[] clusters = getClusters();

        for (int i = 0; i < clusters.length; i++) {
            string.append(i + "." + clusters[i].toString());
        }

        Snippet[] snippets = getSnippets();

        for (int i = 0; i < snippets.length; i++) {
            string.append("[" + numberFormat.format(snippetScores.get(i)) +
                "] ");
            string.append(snippets[i].getText());
            string.append("\n");
        }

        string.append("----\n");

        return string.toString();
    }

    public boolean isOtherTopics() {
        return otherTopics;
    }

    /**
     * @param b
     */
    public void setOtherTopics(boolean b) {
        otherTopics = b;
    }

	public boolean isJunk() {
		return this.otherTopics;
	}
}
