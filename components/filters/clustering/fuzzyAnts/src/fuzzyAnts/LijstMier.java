

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package fuzzyAnts;


import java.io.*;
import java.util.*;


/**
 * Implementation of a fuzzy ant
 *
 * @author Steven Schockaert
 */
public class LijstMier
    extends Mier
    implements Constants
{
    protected Hoop obj;
    protected int hoopNr;
    protected LijstBordModel bm;
    protected Map objectPak = new HashMap();
    protected Map hoopPak = new HashMap();
    protected Map drop = new HashMap();

    public LijstMier(LijstBordModel bm, Map parameters)
    {
        super(parameters);
        this.bm = bm;
    }


    public LijstMier(LijstMier m)
    {
        super(m);
        bm = m.getBM();
    }

    /*
     * Creates a new (empty) heap
     */
    protected Hoop nieuweHoop()
    {
        return new SimHoop(bm);
    }


    /*
     * Calculates the stimulus for dropping the current heap by
     */
    public int berekenDropStimulus(int nieuweAfstand, int oudeAfstand)
    {
        if (nieuweAfstand == 100)
        {
            return 0;
        }
        else if (drop.containsKey(new Paar(nieuweAfstand, oudeAfstand)))
        {
            return ((Integer) drop.get(new Paar(nieuweAfstand, oudeAfstand))).intValue();
        }
        else
        {
            FuzzyNumber res = new FuzzyNumber(100);

            infer(HEELGROOT, oudeAfstand, HEELGROOT, nieuweAfstand, REDELIJKGROOTNUT, res);
            infer(HEELGROOT, oudeAfstand, GROOT, nieuweAfstand, GROOTNUT, res);
            infer(HEELGROOT, oudeAfstand, MATIG, nieuweAfstand, HEELGROOTNUT, res);
            infer(HEELGROOT, oudeAfstand, KLEIN, nieuweAfstand, HEELHEELGROOTNUT, res);
            infer(HEELGROOT, oudeAfstand, HEELKLEIN, nieuweAfstand, HEELHEELGROOTNUT, res);

            infer(GROOT, oudeAfstand, HEELGROOT, nieuweAfstand, KLEINNUT, res);
            infer(GROOT, oudeAfstand, GROOT, nieuweAfstand, REDELIJKGROOTNUT, res);
            infer(GROOT, oudeAfstand, MATIG, nieuweAfstand, GROOTNUT, res);
            infer(GROOT, oudeAfstand, KLEIN, nieuweAfstand, HEELGROOTNUT, res);
            infer(GROOT, oudeAfstand, HEELKLEIN, nieuweAfstand, HEELHEELGROOTNUT, res);

            infer(MATIG, oudeAfstand, HEELGROOT, nieuweAfstand, HEELKLEINNUT, res);
            infer(MATIG, oudeAfstand, GROOT, nieuweAfstand, KLEINNUT, res);
            infer(MATIG, oudeAfstand, MATIG, nieuweAfstand, REDELIJKGROOTNUT, res);
            infer(MATIG, oudeAfstand, KLEIN, nieuweAfstand, GROOTNUT, res);
            infer(MATIG, oudeAfstand, HEELKLEIN, nieuweAfstand, HEELGROOTNUT, res);

            infer(KLEIN, oudeAfstand, HEELGROOT, nieuweAfstand, HEELHEELKLEINNUT, res);
            infer(KLEIN, oudeAfstand, GROOT, nieuweAfstand, HEELKLEINNUT, res);
            infer(KLEIN, oudeAfstand, MATIG, nieuweAfstand, KLEINNUT, res);
            infer(KLEIN, oudeAfstand, KLEIN, nieuweAfstand, REDELIJKGROOTNUT, res);
            infer(KLEIN, oudeAfstand, HEELKLEIN, nieuweAfstand, GROOTNUT, res);

            infer(HEELKLEIN, oudeAfstand, HEELGROOT, nieuweAfstand, HEELHEELKLEINNUT, res);
            infer(HEELKLEIN, oudeAfstand, GROOT, nieuweAfstand, HEELHEELKLEINNUT, res);
            infer(HEELKLEIN, oudeAfstand, MATIG, nieuweAfstand, HEELKLEINNUT, res);
            infer(HEELKLEIN, oudeAfstand, KLEIN, nieuweAfstand, KLEINNUT, res);
            infer(HEELKLEIN, oudeAfstand, HEELKLEIN, nieuweAfstand, REDELIJKGROOTNUT, res);

            int resultaat = res.defuzzification();
            drop.put(new Paar(nieuweAfstand, oudeAfstand), new Integer(resultaat));

            return resultaat;
        }
    }


    /*
     * calculates the stimulus for picking up a single object
     */
    public int berekenObjectPakStimulus(int maxAfst, int gemAfst)
    {
        if (objectPak.containsKey(new Paar(maxAfst, gemAfst)))
        {
            return ((Integer) objectPak.get(new Paar(maxAfst, gemAfst))).intValue();
        }
        else
        {
            FuzzyNumber res = new FuzzyNumber(100);

            infer(HEELGROOT, maxAfst, HEELGROOT, gemAfst, HEELHEELGROOTNUT, res);
            infer(HEELGROOT, maxAfst, GROOT, gemAfst, HEELHEELGROOTNUT, res);
            infer(HEELGROOT, maxAfst, MATIG, gemAfst, HEELHEELGROOTNUT, res);
            infer(HEELGROOT, maxAfst, KLEIN, gemAfst, HEELHEELGROOTNUT, res);
            infer(HEELGROOT, maxAfst, HEELKLEIN, gemAfst, HEELHEELGROOTNUT, res);

            infer(GROOT, maxAfst, GROOT, gemAfst, HEELGROOTNUT, res);
            infer(GROOT, maxAfst, MATIG, gemAfst, HEELHEELGROOTNUT, res);
            infer(GROOT, maxAfst, KLEIN, gemAfst, HEELHEELGROOTNUT, res);
            infer(GROOT, maxAfst, HEELKLEIN, gemAfst, HEELHEELGROOTNUT, res);

            infer(MATIG, maxAfst, MATIG, gemAfst, GROOTNUT, res);
            infer(MATIG, maxAfst, KLEIN, gemAfst, HEELGROOTNUT, res);
            infer(MATIG, maxAfst, HEELKLEIN, gemAfst, HEELHEELGROOTNUT, res);

            infer(KLEIN, maxAfst, KLEIN, gemAfst, REDELIJKGROOTNUT, res);
            infer(KLEIN, maxAfst, HEELKLEIN, gemAfst, GROOTNUT, res);

            infer(HEELKLEIN, maxAfst, HEELKLEIN, gemAfst, MATIGNUT, res);

            int resultaat = res.defuzzification();
            objectPak.put(new Paar(maxAfst, gemAfst), new Integer(resultaat));

            return resultaat;
        }
    }


    /*
     * calculates the stimulus for picking up an entire heap
     */
    public int berekenHoopPakStimulus(int maxAfst, int gemAfst)
    {
        if (hoopPak.containsKey(new Paar(maxAfst, gemAfst)))
        {
            return ((Integer) hoopPak.get(new Paar(maxAfst, gemAfst))).intValue();
        }
        else
        {
            FuzzyNumber res = new FuzzyNumber(100);

            infer(HEELGROOT, maxAfst, HEELGROOT, gemAfst, MATIGNUT, res);
            infer(HEELGROOT, maxAfst, GROOT, gemAfst, HEELKLEINNUT, res);
            infer(HEELGROOT, maxAfst, MATIG, gemAfst, HEELHEELKLEINNUT, res);
            infer(HEELGROOT, maxAfst, KLEIN, gemAfst, HEELHEELKLEINNUT, res);
            infer(HEELGROOT, maxAfst, HEELKLEIN, gemAfst, HEELHEELKLEINNUT, res);

            infer(GROOT, maxAfst, GROOT, gemAfst, REDELIJKGROOTNUT, res);
            infer(GROOT, maxAfst, MATIG, gemAfst, KLEINNUT, res);
            infer(GROOT, maxAfst, KLEIN, gemAfst, HEELKLEINNUT, res);
            infer(GROOT, maxAfst, HEELKLEIN, gemAfst, HEELHEELKLEINNUT, res);

            infer(MATIG, maxAfst, MATIG, gemAfst, GROOTNUT, res);
            infer(MATIG, maxAfst, KLEIN, gemAfst, REDELIJKKLEINNUT, res);
            infer(MATIG, maxAfst, HEELKLEIN, gemAfst, KLEINNUT, res);

            infer(KLEIN, maxAfst, KLEIN, gemAfst, HEELGROOTNUT, res);
            infer(KLEIN, maxAfst, HEELKLEIN, gemAfst, MATIGNUT, res);

            infer(HEELKLEIN, maxAfst, HEELKLEIN, gemAfst, HEELHEELGROOTNUT, res);

            int resultaat = res.defuzzification();
            hoopPak.put(new Paar(maxAfst, gemAfst), new Integer(resultaat));

            return resultaat;
        }
    }


    /*
     * picks up the heap with index "i" with probability "pr"
     */
    public void pakHoop(double pr, int i)
    {
        double waarde = ((double) Math.pow(pr, m1)) / (Math.pow(pr, m1) + Math.pow(0.50, m1));
        double drempel = rand.nextDouble();

        if (waarde > drempel)
        {
            obj = bm.takeHoop(i);
        }
    }


    /*
     * picks up the object with index "i" with probability "pr"
     */
    public void pakObject(double pr, int i)
    {
        double waarde = ((double) Math.pow(pr, n1)) / (Math.pow(pr, n1) + Math.pow(0.50, n1));
        double drempel = rand.nextDouble();

        if (waarde > drempel)
        {
            obj = nieuweHoop();
            obj.add(bm.takeDocument(i));
        }
    }


    /*
     * Calculates the average similarity between objects of "a" and the centre of "b"
     */
    private int bepaalGemiddeldeAfstand(Hoop a, Hoop b)
    {
        List docs = a.geefDocumenten();
        double som = 0;

        for (Iterator it = docs.iterator(); it.hasNext();)
        {
            Document d = (Document) it.next();
            som += b.geefAfstandTotCentrum(d);
        }

        return (int) ((100 * som) / a.geefAantal());
    }


    /*
     * Performs 1 iteration
     */
    public void move()
    {
        if (bm.aantalLijstHopen() > 0)
        {
            hoopNr = rand.nextInt(bm.aantalLijstHopen());

            if (!hasObject())
            {
                Hoop h = bm.geefHoop(hoopNr);

                if (!h.isLeeg())
                {
                    double r = rand.nextDouble();

                    if ((h.geefAantal() == 1) && (r < PLOAD))
                    {
                        obj = nieuweHoop();
                        obj.add(bm.takeDocument(hoopNr));
                    }
                    else if ((h.geefAantal() == 2) && (r < h.geefMaximumAfstand()))
                    {
                        obj = nieuweHoop();
                        obj.add(bm.takeDocument(hoopNr));
                    }
                    else if ((h.geefAantal() == 2) && (r < PLOAD))
                    {
                        obj = bm.takeHoop(hoopNr);
                    }
                    else if (h.geefAantal() > 2)
                    {
                        int maxAfst = (int) (100 * h.geefMaximumAfstand());
                        int gemAfst = (int) (100 * h.geefGemiddeldeAfstand());
                        double pobj = berekenObjectPakStimulus(maxAfst, gemAfst) / 100.0;
                        double phoop = berekenHoopPakStimulus(maxAfst, gemAfst) / 100.0;

                        if (r < (pobj / (pobj + phoop)))
                        {
                            pakObject(pobj, hoopNr);
                        }
                        else
                        {
                            pakHoop(phoop, hoopNr);
                        }
                    }
                }
            }

            else
            {
                Hoop h = bm.geefHoop(hoopNr);
                double r = rand.nextDouble();

                if (h.geefAantal() == 0)
                {
                    System.err.println("Lege hoop in lijst ...");
                }
                else if (r < PDROP)
                {
                    bm.nieuweHoop(obj);
                    obj = nieuweHoop();
                }
                else if (
                    (h.geefAantal() == 1) && (obj.geefAantal() == 1)
                        && (r < (1 - h.geefAfstandTotCentrum(obj.geefCentrum())))
                )
                {
                    bm.drop(obj, hoopNr);
                    obj = nieuweHoop();
                }
                else if (h.geefAantal() > 1)
                {
                    int nieuweAfstand = bepaalGemiddeldeAfstand(obj, h);
                    int oudeAfstand = (int) (100 * (bm.geefHoop(hoopNr).geefGemiddeldeAfstand()));
                    double pdrop = berekenDropStimulus(nieuweAfstand, oudeAfstand) / 100.0;
                    int e = (obj.geefAantal() == 1) ? n2
                                                    : m2;
                    double b = 0.50;
                    double waarde = ((double) Math.pow(pdrop, e)) / (Math.pow(pdrop, e)
                        + Math.pow(b, e));

                    if (r < waarde)
                    {
                        bm.drop(obj, hoopNr);
                        obj = nieuweHoop();
                    }
                }
            }
        }
        else
        {
            bm.nieuweHoop(obj);
            obj = nieuweHoop();
        }
    }


    public LijstBordModel getBM()
    {
        return bm;
    }


    public Hoop getObject()
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
