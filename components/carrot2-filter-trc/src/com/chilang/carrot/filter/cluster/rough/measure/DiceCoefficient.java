/**
 * 
 * @author chilang
 * Created 2003-07-24, 07:47:02.
 */
package com.chilang.carrot.filter.cluster.rough.measure;

import com.chilang.carrot.filter.cluster.rough.clustering.Clusterable;

public class DiceCoefficient implements Similarity {


    /**
     * Calculate Dice coefficient as
     *                      2 * SUM[k=1..N](w_kj1 * w_kj2)
     *   S_D(d_j1, d_j2) = --------------------------------------------
     *                     (SUM[k=1..N](w_kj1^2) + SUM[k=1..N](w_kj2^2)
     * @param obj1
     * @param obj2
     * @return
     */
    public double measure(Clusterable obj1, Clusterable obj2) {
        /*Clusterable[] doc = { obj1, obj2};
//        System.out.println("o1="+obj1+",o2="+obj2);
        double[] sum = new double [2];
        Map termWeightMap = CollectionFactory.getHashMap();
        //store map of term -> weight of first document
        sum[0] = 0;
        for (Iterator iter = doc[0].getAttributes().iterator(); iter.hasNext(); ) {
            WeightedTerm term = (WeightedTerm) iter.next();
            double weight = term.getWeight();
            termWeightMap.put(term, new Double(weight));
            sum[0] += weight * weight;
        }
        double weightProduct = 0;
        sum[1] = 0;

        //calculate sum of product of term weight from both document
        for (Iterator iter = doc[1].getAttributes().iterator(); iter.hasNext(); ) {
            WeightedTerm term = (WeightedTerm) iter.next();
            double weight = term.getWeight();

            //if a term exist in both document, calculate product of weight
            //else : product = 0 * something = 0 => no need to add
            if (termWeightMap.containsKey(term)) {
                weightProduct += weight * ((Double)termWeightMap.get(term)).doubleValue();
            }
            sum[1] += weight * weight;
        }

        return 2 * weightProduct / (sum[0] + sum[1]);*/
        return 0;
    }

    public double measure(double[] vector1, double[] vector2) {
        return 0;  //To change body of implemented methods use Options | File Templates.
    }
}
