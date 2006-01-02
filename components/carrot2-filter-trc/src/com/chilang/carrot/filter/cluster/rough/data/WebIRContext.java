package com.chilang.carrot.filter.cluster.rough.data;

import cern.colt.bitvector.BitVector;
import com.chilang.carrot.filter.cluster.rough.CommonFactory;
import com.chilang.carrot.filter.cluster.rough.FeatureVector;
import com.chilang.carrot.filter.cluster.rough.Snippet;
import com.chilang.carrot.filter.cluster.rough.SparseFeatureVector;
import com.chilang.carrot.filter.cluster.rough.clustering.ClusterRepresentative;
import com.chilang.carrot.filter.cluster.rough.clustering.Clusterable;
import com.chilang.carrot.filter.cluster.rough.filter.StopWordFilter;
import com.chilang.carrot.filter.cluster.rough.filter.TermFilter;
import com.chilang.carrot.filter.cluster.rough.transformer.Snippet2DocumentTransformer;
import com.chilang.carrot.filter.cluster.rough.transformer.Transformer;
import com.chilang.carrot.filter.cluster.rough.weighting.TfIdfWeighting;
import com.chilang.carrot.filter.cluster.rough.weighting.WeightingScheme;
import com.chilang.util.ArrayUtils;
import com.chilang.util.MathUtils;
import com.chilang.util.StringUtils;

import java.util.*;


public class WebIRContext implements IRContext {

//    protected static Logger log = Logger.getLogger(WebIRContext.class);
//    protected static Timer timer = new Timer();


    protected TermSelectionStrategy termSelectionStrategy;

    protected WeightingScheme weightingScheme;

    protected Collection documents = new ArrayList();;

    //cell[i] contains number of documents where term i occurs
    protected int[] documentFreq;
    //matrix of document X term,
    //cell[i][j] contains number of occurences of term j in document i
    protected int[][] termFrequency;

    public static final double STRONG_TERM_SCALING_FACTOR = 2.5;


    /**
     * Map from snippet id to snippet
     */
    protected Map snippetIdMap = new HashMap();

    protected int nterm;    //number of terms
    protected int ndoc;     //number of documents


    protected double[][] termWeight;

    /**
     * cell[i] store references to term with largest weight in document i
     */
    protected int[] maxWeightIndices;

    /**
     * cell[i] store smallest weight of term for document i
     */
    double[] minTermWeight;

    /**
     * Map of (stem -> Term)
     */
    protected Map stemIndex = new HashMap();;

    protected int currentDocumentIndex = 0;

    Term[] termIndex;


    StopWordFilter filter;

    protected Transformer transformer;


    Clusterable[] clusteringItems;

    Snippet[] snippetIndex;


    String query;


    String[] queryWords = new String[0];




    /** Convert query string to array of lower-cased words */
    private String[] splitQueryIntoWords(String query) {
        return StringUtils.trim(query, '\"').trim().toLowerCase().split(" ");
    }

    public void setQuery(String query) {
        this.query = query;

        queryWords = splitQueryIntoWords(query);


    }

    //TODO deprecate
    public String[] getQueryWords() {
        return queryWords;
    }

    public String getQuery() {
        return query;
    }


    public StopWordFilter getFilter() {
        return filter;
    }


    public WebIRContext(StopWordFilter filter, Transformer transformer, TermSelectionStrategy termSelectionStrategy) {
        this(filter, transformer);
        this.termSelectionStrategy = termSelectionStrategy;
    }

    public WebIRContext(StopWordFilter filter, Transformer transformer) {
        this.filter = filter;
        this.transformer = transformer;

    }

    public WebIRContext() {
        //TODO REFACTOR !!! UGLY !!!
//        termDocumentFreq = CollectionFactory.getHashMap();
        this.filter = CommonFactory.createStopWordsSet();
        transformer = new Snippet2DocumentTransformer(
//                new StemmedTermExtractor(
                CommonFactory.createSimpleTermExtractor(CommonFactory.createDefaultStemmer(), (TermFilter) filter));

    }

    public WebIRContext(String query, Collection snips) {
        this();
        setQuery(query);
        setDocuments(snips);
    }

    public WebIRContext(Collection snips) {
        this();
        setDocuments(snips);
    }

    public void addDocument(Document document) {
        document.setInternalId(currentDocumentIndex++);
        snippetIndex[document.getInternalId()] = (Snippet) document;
        snippetIdMap.put(((Snippet) document).getId(), document);

        documents.add(transformer.transform(document));
//        addTerms(document.getTerms());
        addTerms(document.getTermSet());
    }

    private void addTerms(Collection documentTerms) {
//        terms.addAll(documentTerms);
        for (Iterator iter = documentTerms.iterator(); iter.hasNext();) {
            Term term = (Term) iter.next();
            if (stemIndex.containsKey(term.getStemmedTerm())) {
                Term currentTerm = (Term) stemIndex.get(term.getStemmedTerm());
                currentTerm = mergeTerm(currentTerm, term);
            } else {
                int size = stemIndex.size();
                term.setId(size);
                stemIndex.put(term.getStemmedTerm(), term);
            }

        }
    }

    public static Term mergeTerm(Term t1, Term t2) {
        Map m2 = t2.getTfMap();
        for (Iterator i = m2.keySet().iterator(); i.hasNext();) {
            String doc = (String) i.next();
            t1.increaseTf(doc, ((Integer) m2.get(doc)).intValue());
        }
        return t1;
    }

    public Clusterable[] getDocuments() {
        return clusteringItems;
    }

    public Collection getTermIndex() {
        return stemIndex.values();
    }

    public void setDocuments(Collection documents) {

        this.ndoc = documents.size();
        this.clusteringItems = new Clusterable[ndoc];
        this.snippetIndex = new Snippet[ndoc];
        for (Iterator iter = documents.iterator(); iter.hasNext();) {
            Document doc = (Document) iter.next();
            addDocument(doc);
        }

    }

    private int[] findMaxWeightTerm(int[][] tf) {
        int[] maxIndices = new int[tf.length];
        for (int i = 0; i < tf.length; i++) {
            maxIndices[i] = ArrayUtils.maxIndex(tf[i]);
        }
        return maxIndices;
    }


    /**
     * Must be executed before normal usage of corpus
     */
    public void buildDocumentTermMatrix() {
//        timer.start();
//        System.out.println("termIndex size="+stemIndex.size());


        //calculate document x term occurences matrix
        // and document frequency matrix (no of document in collection in which term occur)


        /**
         * By default select only terms that appears in at least 2 documents
         * and not query words
         */

        if (termSelectionStrategy == null)
            termSelectionStrategy =
                    new QuerySensitiveSelectionStrategy(2, query);
//                    new DocumentFrequencySelectionStrategy(2);
        int termCurrentIndex = 0;


        Collection selectedTerms = new ArrayList();

//        System.out.println("All terms : "+stemIndex.values().size());

        for (Iterator iterator = stemIndex.values().iterator(); iterator.hasNext();) {
            Term term = (Term) iterator.next();
            if (termSelectionStrategy.accept(term)) {
                term.setId(termCurrentIndex++);
                selectedTerms.add(term);
            } else {
//                System.out.println("-- " + term.getStemmedTerm()+" - "+term.getOriginalTerm());
            }
        }

//        System.out.println("Selected terms : "+selectedTerms.size());
        //Remove term that are not indexed
        //TODO : refactor to more elegant solution
        for (Iterator iterator = documents.iterator(); iterator.hasNext();) {
            Document doc = (Document) iterator.next();
            doc.getTermSet().retainAll(selectedTerms);
        }

        nterm = selectedTerms.size();
        termFrequency = new int[ndoc][nterm];
        documentFreq = new int[nterm];
        termIndex = new Term[nterm];
//        System.out.println("term index "+termCurrentIndex);
//        System.out.println("Documents : " + ndoc + "; Terms : " + nterm);

        for (Iterator iter = selectedTerms.iterator(); iter.hasNext();) {
            Term term = (Term) iter.next();

            //count document occurences of term
            documentFreq[term.getId()] = term.getTfMap().size();

            for (Iterator i = term.getTfMap().entrySet().iterator(); i.hasNext();) {
                Map.Entry docEntry = (Map.Entry) i.next();
                String docId = (String) docEntry.getKey();
                int tf = ((Integer) docEntry.getValue()).intValue();

                Document doc = (Document) snippetIdMap.get(docId);


                if (doc.getStrongTerms().contains(term.getStemmedTerm())) {
                    tf = (int) ((double) tf * STRONG_TERM_SCALING_FACTOR);
                }

                termFrequency[doc.getInternalId()][term.getId()] = tf;


            }
            //store term in the index
            termIndex[term.getId()] = term;

        }

//        System.out.println("Building frequency matrix " + timer.elapsedAsStringAndStart());
//        termCooccurences = calculateCooccurenceMatrix(termFrequency);
//        System.out.println("Building cooccurences matrix "+timer.elapsedAsStringAndStart());
//        System.out.println("term*term matrix dimension : "+termCooccurences.length +" * " +termCooccurences[0].length);
        calculateTermWeight();

//        System.out.println("Building weight matrix " + timer.elapsedAsStringAndStart());

        maxWeightIndices = findMaxWeightTerm(termFrequency);

        minTermWeight = new double[ndoc];
        for (int i = 0; i < ndoc; i++) {
            minTermWeight[i] = ArrayUtils.min(termWeight[i]);
        }


        generateClusteringItems();
//        System.out.println("Doing misc task " + timer.elapsedAsStringAndStart());
    }

    public int[][] getTermFrequency() {
        return termFrequency;
    }

    public int[] getMaxWeightIndices() {
        return maxWeightIndices;
    }

    private void calculateTermWeight() {
        weightingScheme = new TfIdfWeighting(termFrequency);

        termWeight = weightingScheme.getTermWeight();

        //Populating term with its weight
        for (Iterator iter = documents.iterator(); iter.hasNext();) {
            Document doc = (Document) iter.next();

            //NOTE. weight[i][j] for term outside given document are 0
            for (Iterator t = doc.getTermSet().iterator(); t.hasNext();) {
                WeightedTerm term = (WeightedTerm) t.next();

                //set weight for terms
                term.setWeight(termWeight[doc.getInternalId()][term.getId()]);

            }
        }

    }


    private void generateClusteringItems() {
        for (int i = 0; i < clusteringItems.length; i++) {
            clusteringItems[i] = new ClusterRepresentative(i, new SparseFeatureVector(termWeight[i]));
        }
    }

    public Clusterable[] getClusteringItems() {
        return clusteringItems;
    }


    public FeatureVector recalculateWeightUpper(FeatureVector upper, Clusterable doc) {


        FeatureVector documentFeatures = doc.getFeatures();
        BitVector upperBit = upper.asBitVector();
        for (int i = 0; i < documentFeatures.size(); i++) {

            double weight;
            //if given term in upper approximation doesn't exist in document
            //set it's term weight to value smaller than any weight in given document

            if (!upperBit.getQuick(i)) {
                double ratio = MathUtils.log(10, (double) ndoc / (double) documentFreq[i]);
                weight =
                        minTermWeight[doc.getIdentifier()] * (ratio / (1 + ratio));

            } else {
                //calculate weight as for normal document's term
                //NOTE : we CAN'T use weight copied with terms
                // because those were normalized for it's document
//                weight = (1.0 + MathUtils.log(10, termFrequency[doc.getInternalId()][term.getId()]))
//                        * MathUtils.log(10, (double)ndoc / (double)termDocumentFreq[term.getId()]);

                //USE TF*IDF
                weight = calculateWeight(termFrequency[doc.getIdentifier()][i], documentFreq[i]);

//                        (1 + MathUtils.log(10, termFrequency[doc.getInternalId()][term.getInternalId()])) * MathUtils.log(10, (double)ndoc / (double)termDocumentFreq[doc.getInternalId()]);
            }
            upper.setWeight(i, weight);

//            lengthFactor += term.getWeight() * term.getWeight();
        }

        upper.normalize();

        //length factor = sqrt(SUM of weight^2)
//        lengthFactor = Math.sqrt(lengthFactor);
        //upper set "vector" length normalization
//        for (Iterator i = upper.iterator(); i.hasNext(); ) {
//            WeightedTerm term = (WeightedTerm) i.next();
//            term.setWeight(term.getWeight()/lengthFactor);
//        }
        return upper;
    }

    public int[] getDocumentFrequency() {
        return documentFreq;
    }


    public Term[] getTermByIndices(int[] indices) {
        return (Term[]) ArrayUtils.projection(termIndex, indices).toArray(new Term[indices.length]);
    }

    public Snippet[] getSnippetByIndices(int[] indices) {
        return (Snippet[]) ArrayUtils.projection(snippetIndex, indices).toArray(new Snippet[indices.length]);
    }

    private double calculateWeight(double tf, double df) {
        return tf * MathUtils.log10(ndoc / df);
    }

    public int noOfTerms() {
        return nterm;
    }

    public int noOfDocuments() {
        return ndoc;
    }

    public double[][] getTermWeight() {
        return termWeight;
    }

    public Term[] getTermArray() {
        return termIndex;
    }

    public Snippet[] getSnippets() {
        return snippetIndex;
    }


    public String getSnippetTermWeightAsString(int snippetId) {
        double[] weight = termWeight[snippetId];
        List nonzeroes = new ArrayList();

        for (int i = 0; i < weight.length; i++) {
            if (weight[i] > 0) {
//                b.append(sep + "("+termIndex[i].getStemmedTerm()+","+weight[i]+")");
//                sep = ", ";
                nonzeroes.add(new TW(termIndex[i].getStemmedTerm(), weight[i]));
            }
        }

        Collections.sort(nonzeroes, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((TW) o1).weight > ((TW) o2).weight ? -1 : 1;
            }

            public boolean equals(Object obj) {
                return false;
            }
        });
        StringBuffer b = new StringBuffer();
        String sep = "";
        for (Iterator it = nonzeroes.iterator(); it.hasNext();) {
            TW tw = (TW) it.next();
            b.append(sep + "(" + tw.term + "," + tw.weight + ")");
        }
        return "{" + b.toString() + "}";
    }

    static class TW {
        String term;
        double weight;

        TW(String t, double w) {
            term = t;
            weight = w;
        }
    }


}
