
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

package org.carrot2.filter.fuzzyAnts;


import java.util.*;


/**
 * Implementation of a fuzzy ant
 *
 * @author Steven Schockaert
 */
public class ListAnt
    extends Ant
    implements Constants
{
    protected Heap obj;
    protected int heapNr;
    protected ListBordModel bm;
    protected Map objectPickup = new HashMap();
    protected Map heapPickup = new HashMap();
    protected Map drop = new HashMap();

    public ListAnt(ListBordModel bm, FuzzyAntsParameters parameters)
    {
        super(parameters);
        this.bm = bm;
    }


    /*
     * Creates a new (empty) heap
     */
    protected Heap newHeap()
    {
        return new SimHeap(bm);
    }


    /*
     * Calculates the stimulus for dropping the current heap by
     */
    public int inferDropStimulus(int newDissimilarity, int oldDissimilarity)
    {
        if (newDissimilarity == 100)
        {
            return 0;
        }
        else if (drop.containsKey(new Paar(newDissimilarity, oldDissimilarity)))
        {
            return ((Integer) drop.get(new Paar(newDissimilarity, oldDissimilarity))).intValue();
        }
        else
        {
            FuzzyNumber res = new FuzzyNumber(100);

            infer(VERYHIGH, oldDissimilarity, VERYHIGH, newDissimilarity, RATHERHIGHSTIMULUS, res);
            infer(VERYHIGH, oldDissimilarity, HIGH, newDissimilarity, HIGHSTIMULUS, res);
            infer(VERYHIGH, oldDissimilarity, MEDIUM, newDissimilarity, VERYHIGHSTIMULUS, res);
            infer(VERYHIGH, oldDissimilarity, LOW, newDissimilarity, VERYVERYHIGHSTIMULUS, res);
            infer(VERYHIGH, oldDissimilarity, VERYLOW, newDissimilarity, VERYVERYHIGHSTIMULUS, res);

            infer(HIGH, oldDissimilarity, VERYHIGH, newDissimilarity, LOWSTIMULUS, res);
            infer(HIGH, oldDissimilarity, HIGH, newDissimilarity, RATHERHIGHSTIMULUS, res);
            infer(HIGH, oldDissimilarity, MEDIUM, newDissimilarity, HIGHSTIMULUS, res);
            infer(HIGH, oldDissimilarity, LOW, newDissimilarity, VERYHIGHSTIMULUS, res);
            infer(HIGH, oldDissimilarity, VERYLOW, newDissimilarity, VERYVERYHIGHSTIMULUS, res);

            infer(MEDIUM, oldDissimilarity, VERYHIGH, newDissimilarity, VERYLOWSTIMULUS, res);
            infer(MEDIUM, oldDissimilarity, HIGH, newDissimilarity, LOWSTIMULUS, res);
            infer(MEDIUM, oldDissimilarity, MEDIUM, newDissimilarity, RATHERHIGHSTIMULUS, res);
            infer(MEDIUM, oldDissimilarity, LOW, newDissimilarity, HIGHSTIMULUS, res);
            infer(MEDIUM, oldDissimilarity, VERYLOW, newDissimilarity, VERYHIGHSTIMULUS, res);

            infer(LOW, oldDissimilarity, VERYHIGH, newDissimilarity, VERYVERYLOWSTIMULUS, res);
            infer(LOW, oldDissimilarity, HIGH, newDissimilarity, VERYLOWSTIMULUS, res);
            infer(LOW, oldDissimilarity, MEDIUM, newDissimilarity, LOWSTIMULUS, res);
            infer(LOW, oldDissimilarity, LOW, newDissimilarity, RATHERHIGHSTIMULUS, res);
            infer(LOW, oldDissimilarity, VERYLOW, newDissimilarity, HIGHSTIMULUS, res);

            infer(VERYLOW, oldDissimilarity, VERYHIGH, newDissimilarity, VERYVERYLOWSTIMULUS, res);
            infer(VERYLOW, oldDissimilarity, HIGH, newDissimilarity, VERYVERYLOWSTIMULUS, res);
            infer(VERYLOW, oldDissimilarity, MEDIUM, newDissimilarity, VERYLOWSTIMULUS, res);
            infer(VERYLOW, oldDissimilarity, LOW, newDissimilarity, LOWSTIMULUS, res);
            infer(VERYLOW, oldDissimilarity, VERYLOW, newDissimilarity, RATHERHIGHSTIMULUS, res);

            int result = res.defuzzification();
            drop.put(new Paar(newDissimilarity, oldDissimilarity), new Integer(result));

            return result;
        }
    }


    /*
     * calculates the stimulus for picking up a single object
     */
    public int inferObjectPickupStimulus(int maxDissim, int avgDissim)
    {
        if (objectPickup.containsKey(new Paar(maxDissim, avgDissim)))
        {
            return ((Integer) objectPickup.get(new Paar(maxDissim, avgDissim))).intValue();
        }
        else
        {
            FuzzyNumber res = new FuzzyNumber(100);

            infer(VERYHIGH, maxDissim, VERYHIGH, avgDissim, VERYVERYHIGHSTIMULUS, res);
            infer(VERYHIGH, maxDissim, HIGH, avgDissim, VERYVERYHIGHSTIMULUS, res);
            infer(VERYHIGH, maxDissim, MEDIUM, avgDissim, VERYVERYHIGHSTIMULUS, res);
            infer(VERYHIGH, maxDissim, LOW, avgDissim, VERYVERYHIGHSTIMULUS, res);
            infer(VERYHIGH, maxDissim, VERYLOW, avgDissim, VERYVERYHIGHSTIMULUS, res);

            infer(HIGH, maxDissim, HIGH, avgDissim, VERYHIGHSTIMULUS, res);
            infer(HIGH, maxDissim, MEDIUM, avgDissim, VERYVERYHIGHSTIMULUS, res);
            infer(HIGH, maxDissim, LOW, avgDissim, VERYVERYHIGHSTIMULUS, res);
            infer(HIGH, maxDissim, VERYLOW, avgDissim, VERYVERYHIGHSTIMULUS, res);

            infer(MEDIUM, maxDissim, MEDIUM, avgDissim, HIGHSTIMULUS, res);
            infer(MEDIUM, maxDissim, LOW, avgDissim, VERYHIGHSTIMULUS, res);
            infer(MEDIUM, maxDissim, VERYLOW, avgDissim, VERYVERYHIGHSTIMULUS, res);

            infer(LOW, maxDissim, LOW, avgDissim, RATHERHIGHSTIMULUS, res);
            infer(LOW, maxDissim, VERYLOW, avgDissim, HIGHSTIMULUS, res);

            infer(VERYLOW, maxDissim, VERYLOW, avgDissim, MEDIUMSTIMULUS, res);

            int result = res.defuzzification();
            objectPickup.put(new Paar(maxDissim, avgDissim), new Integer(result));

            return result;
        }
    }


    /*
     * calculates the stimulus for picking up an entire heap
     */
    public int inferHeapPickupStimulus(int maxDissim, int avgDissim)
    {
        if (heapPickup.containsKey(new Paar(maxDissim, avgDissim)))
        {
            return ((Integer) heapPickup.get(new Paar(maxDissim, avgDissim))).intValue();
        }
        else
        {
            FuzzyNumber res = new FuzzyNumber(100);

            infer(VERYHIGH, maxDissim, VERYHIGH, avgDissim, MEDIUMSTIMULUS, res);
            infer(VERYHIGH, maxDissim, HIGH, avgDissim, VERYLOWSTIMULUS, res);
            infer(VERYHIGH, maxDissim, MEDIUM, avgDissim, VERYVERYLOWSTIMULUS, res);
            infer(VERYHIGH, maxDissim, LOW, avgDissim, VERYVERYLOWSTIMULUS, res);
            infer(VERYHIGH, maxDissim, VERYLOW, avgDissim, VERYVERYLOWSTIMULUS, res);

            infer(HIGH, maxDissim, HIGH, avgDissim, RATHERHIGHSTIMULUS, res);
            infer(HIGH, maxDissim, MEDIUM, avgDissim, LOWSTIMULUS, res);
            infer(HIGH, maxDissim, LOW, avgDissim, VERYLOWSTIMULUS, res);
            infer(HIGH, maxDissim, VERYLOW, avgDissim, VERYVERYLOWSTIMULUS, res);

            infer(MEDIUM, maxDissim, MEDIUM, avgDissim, HIGHSTIMULUS, res);
            infer(MEDIUM, maxDissim, LOW, avgDissim, RATHERLOWSTIMULUS, res);
            infer(MEDIUM, maxDissim, VERYLOW, avgDissim, LOWSTIMULUS, res);

            infer(LOW, maxDissim, LOW, avgDissim, VERYHIGHSTIMULUS, res);
            infer(LOW, maxDissim, VERYLOW, avgDissim, MEDIUMSTIMULUS, res);

            infer(VERYLOW, maxDissim, VERYLOW, avgDissim, VERYVERYHIGHSTIMULUS, res);

            int result = res.defuzzification();
            heapPickup.put(new Paar(maxDissim, avgDissim), new Integer(result));

            return result;
        }
    }


    /*
     * picks up the heap with index "i" with probability "pr"
     */
    public void pickupHeap(double pr, int i)
    {
        double value = Math.pow(pr, m1) / (Math.pow(pr, m1) + Math.pow(0.50, m1));
        double threshold = rand.nextDouble();

        if (value > threshold)
        {
            obj = bm.takeHeap(i);
        }
    }


    /*
     * picks up the object with index "i" with probability "pr"
     */
    public void pickupObject(double pr, int i)
    {
        double value = Math.pow(pr, n1) / (Math.pow(pr, n1) + Math.pow(0.50, n1));
        double threshold = rand.nextDouble();

        if (value > threshold)
        {
            obj = newHeap();
            obj.add(bm.takeDocument(i));
        }
    }


    /*
     * Calculates the average similarity between objects of "a" and the centre of "b"
     */
    private int getAverageDissimilarity(Heap a, Heap b)
    {
        List docs = a.getDocuments();
        double som = 0;

        for (Iterator it = docs.iterator(); it.hasNext();)
        {
            Document d = (Document) it.next();
            som += b.getDissimilarityWithCentrum(d);
        }

        return (int) ((100 * som) / a.getNumber());
    }


    /*
     * Performs 1 iteration
     */
    public void move()
    {
        if (bm.numberOfListHeaps() > 0)
        {
            heapNr = rand.nextInt(bm.numberOfListHeaps());

            if (!hasObject())
            {
                Heap h = bm.getHeap(heapNr);

                if (!h.isLeeg())
                {
                    double r = rand.nextDouble();

                    if ((h.getNumber() == 1) && (r < PLOAD))
                    {
                        obj = newHeap();
                        obj.add(bm.takeDocument(heapNr));
                    }
                    else if ((h.getNumber() == 2) && (r < h.getMaximumDissimilarity()))
                    {
                        obj = newHeap();
                        obj.add(bm.takeDocument(heapNr));
                    }
                    else if ((h.getNumber() == 2) && (r < PLOAD))
                    {
                        obj = bm.takeHeap(heapNr);
                    }
                    else if (h.getNumber() > 2)
                    {
                        int maxDissim = (int) (100 * h.getMaximumDissimilarity());
                        int avgDissim = (int) (100 * h.getAverageDissimilarity());
                        double pobj = inferObjectPickupStimulus(maxDissim, avgDissim) / 100.0;
                        double pheap = inferHeapPickupStimulus(maxDissim, avgDissim) / 100.0;

                        if (r < (pobj / (pobj + pheap)))
                        {
                            pickupObject(pobj, heapNr);
                        }
                        else
                        {
                            pickupHeap(pheap, heapNr);
                        }
                    }
                }
            }

            else
            {
                Heap h = bm.getHeap(heapNr);
                double r = rand.nextDouble();

                if (h.getNumber() == 0)
                {
                    System.err.println("Empty heap in list ...");
                }
                else if (r < PDROP)
                {
                    bm.newHeap(obj);
                    obj = newHeap();
                }
                else if (
                    (h.getNumber() == 1) && (obj.getNumber() == 1)
                        && (r < (1 - h.getDissimilarityWithCentrum(obj.getCentrum())))
                )
                {
                    bm.drop(obj, heapNr);
                    obj = newHeap();
                }
                else if (h.getNumber() > 1)
                {
                    int newDissimilarity = getAverageDissimilarity(obj, h);
                    int oldDissimilarity = (int) (100 * (bm.getHeap(heapNr).getAverageDissimilarity()));
                    double pdrop = inferDropStimulus(newDissimilarity, oldDissimilarity) / 100.0;
                    int e = (obj.getNumber() == 1) ? n2
                                                    : m2;
                    double b = 0.50;
                    double value = Math.pow(pdrop, e) / (Math.pow(pdrop, e)
                        + Math.pow(b, e));

                    if (r < value)
                    {
                        bm.drop(obj, heapNr);
                        obj = newHeap();
                    }
                }
            }
        }
        else
        {
            bm.newHeap(obj);
            obj = newHeap();
        }
    }


    public ListBordModel getBM()
    {
        return bm;
    }


    public Heap getObject()
    {
        return obj;
    }


    public boolean hasObject()
    {
        return ((obj != null) && !obj.isLeeg());
    }

    public class Paar
    {
        public int a;
        public int b;

        public Paar(int a, int b)
        {
            this.a = a;
            this.b = b;
        }

        public boolean equals(Object obj)
        {
            if (!(obj instanceof Paar))
            {
                return false;
            }
            else
            {
                Paar p = (Paar) obj;

                return ((a == p.a) && (b == p.b));
            }
        }


        public int hashCode()
        {
            return (a * 100) + b;
        }
    }
}
