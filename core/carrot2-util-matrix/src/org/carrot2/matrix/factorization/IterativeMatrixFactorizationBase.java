/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.matrix.factorization;

import org.carrot2.matrix.NNIDoubleFactory2D;
import org.carrot2.matrix.factorization.seeding.RandomSeedingStrategy;
import org.carrot2.matrix.factorization.seeding.ISeedingStrategy;

import org.apache.mahout.math.Arrays;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.apache.mahout.math.matrix.doublealgo.Sorting;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.apache.mahout.math.matrix.linalg.Algebra;
import org.apache.mahout.math.jet.math.Functions;

/**
 * Base functionality for {@link IIterativeMatrixFactorization}s.
 */
abstract class IterativeMatrixFactorizationBase extends MatrixFactorizationBase implements
    IIterativeMatrixFactorization
{
    /** The desired number of base vectors */
    protected int k;
    protected static int DEFAULT_K = 15;

    /** The maximum number of iterations the algorithm is allowed to run */
    protected int maxIterations;
    protected static final int DEFAULT_MAX_ITERATIONS = 15;

    /**
     * If the percentage decrease in approximation error becomes smaller than
     * <code>stopThreshold</code>, the algorithm will stop. Note: calculation of
     * approximation error is quite costly. Setting the threshold to -1 turns off
     * approximation error calculation and hence makes the algorithm do the maximum number
     * of iterations.
     */
    protected double stopThreshold;
    protected static double DEFAULT_STOP_THRESHOLD = -1.0;

    /** Seeding strategy */
    protected ISeedingStrategy seedingStrategy;
    protected static final ISeedingStrategy DEFAULT_SEEDING_STRATEGY = new RandomSeedingStrategy(
        0);

    /** Order base vectors according to their 'activity'? */
    protected boolean ordered;
    protected static final boolean DEFAULT_ORDERED = false;

    /** Current approximation error */
    protected double approximationError;

    /** Approximation errors during subsequent iterations */
    protected double [] approximationErrors;

    /** Iteration counter */
    protected int iterationsCompleted;

    /** Sorting aggregates */
    protected double [] aggregates;

    /**
     * @param A
     */
    public IterativeMatrixFactorizationBase(DoubleMatrix2D A)
    {
        super(A);

        this.k = DEFAULT_K;
        this.maxIterations = DEFAULT_MAX_ITERATIONS;
        this.stopThreshold = DEFAULT_STOP_THRESHOLD;
        this.seedingStrategy = DEFAULT_SEEDING_STRATEGY;
        this.ordered = DEFAULT_ORDERED;
        this.approximationErrors = null;
        this.approximationError = -1;
        this.iterationsCompleted = 0;
    }

    /**
     * Sets the number of base vectors <i>k </i>.
     * 
     * @param k the number of base vectors
     */
    public void setK(int k)
    {
        this.k = k;
    }

    /**
     * Returns the number of base vectors <i>k </i>.
     */
    public int getK()
    {
        return k;
    }

    /**
     * @return true if the decrease in the approximation error is smaller than the
     *         <code>stopThreshold</code>
     */
    protected boolean updateApproximationError()
    {
        if (approximationErrors == null)
        {
            approximationErrors = new double [maxIterations + 1];
        }

        // Approximation error
        double newApproximationError = Algebra.DEFAULT.normF(U.zMult(V, null, 1, 0,
            false, true).assign(A, Functions.minus));
        approximationErrors[iterationsCompleted] = newApproximationError;

        if ((approximationError - newApproximationError) / approximationError < stopThreshold)
        {
            approximationError = newApproximationError;
            return true;
        }
        else
        {
            approximationError = newApproximationError;
            return false;
        }
    }

    /**
     * Orders U and V matrices according to the 'activity' of base vectors.
     */
    protected void order()
    {
        DoubleMatrix2D VT = V.viewDice();
        aggregates = new double [VT.rows()];

        for (int i = 0; i < aggregates.length; i++)
        {
            // we take -aggregate to do descending sorting
            aggregates[i] = -VT.viewRow(i).aggregate(Functions.plus, Functions.square);
        }

        // Need to make a copy of aggregates because they get sorted as well
        double [] aggregatesCopy = aggregates.clone();

        try
        {
            V = NNIDoubleFactory2D.asNNIMatrix(Sorting.quickSort.sort(VT, aggregates)
                .viewDice());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            String debugInfo = create(aggregates, VT);
            System.out.println("Debug info 1: " + debugInfo);
            throw new RuntimeException(
                "Aggregates1: " + Arrays.toString(aggregates)
                + "\nMatrix1: " + VT.toString(), e);
        }
        try
        {
            U = NNIDoubleFactory2D.asNNIMatrix(Sorting.quickSort.sort(U.viewDice(),
                aggregatesCopy).viewDice());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            String debugInfo = create(aggregatesCopy, U.viewDice());
            System.out.println("Debug info 2: " + debugInfo);
            throw new RuntimeException("Aggregates2: " + Arrays.toString(aggregatesCopy)
                + "\nMatrix2: " + U.viewDice().toString(), e);
        }

        // Revert back to positive values of aggregates
        for (int i = 0; i < aggregates.length; i++)
        {
            aggregates[i] = -aggregates[i];
        }
    }

    private String create(double [] aggregates, DoubleMatrix2D m)
    {
        StringBuilder b = new StringBuilder();

        b.append("Aggregates: ");
        for (int i = 0; i < aggregates.length; i++)
        {
            b.append(Double.doubleToRawLongBits(aggregates[i]));
            b.append(", ");
        }
        b.append("\n");

        b.append("Matrix: " + m.columns() + " " + m.rows());
        for (int c = 0; c < m.columns(); c++)
        {
            for (int r = 0; r < m.rows(); r++)
            {
                b.append(Double.doubleToRawLongBits(m.get(r, c)));
                b.append(", ");
            }
            b.append("\n");
        }
        b.append("\n");

        return b.toString();
    }

    /**
     * Returns current {@link ISeedingStrategy}.
     */
    public ISeedingStrategy getSeedingStrategy()
    {
        return seedingStrategy;
    }

    /**
     * Sets new {@link ISeedingStrategy}.
     */
    public void setSeedingStrategy(ISeedingStrategy seedingStrategy)
    {
        this.seedingStrategy = seedingStrategy;
    }

    /**
     * Returns the maximum number of iterations the algorithm is allowed to run.
     */
    public int getMaxIterations()
    {
        return maxIterations;
    }

    /**
     * Sets the maximum number of iterations the algorithm is allowed to run.
     */
    public void setMaxIterations(int maxIterations)
    {
        this.maxIterations = maxIterations;
    }

    /**
     * Returns the algorithms <code>stopThreshold</code>. If the percentage decrease in
     * approximation error becomes smaller than <code>stopThreshold</code>, the algorithm
     * will stop.
     */
    public double getStopThreshold()
    {
        return stopThreshold;
    }

    /**
     * Sets the algorithms <code>stopThreshold</code>. If the percentage decrease in
     * approximation error becomes smaller than <code>stopThreshold</code>, the algorithm
     * will stop.
     * <p>
     * Note: calculation of approximation error is quite costly. Setting the threshold to
     * -1 turns off calculation of the approximation error and hence makes the algorithm
     * do the maximum allowed number of iterations.
     */
    public void setStopThreshold(double stopThreshold)
    {
        this.stopThreshold = stopThreshold;
    }

    /**
     * Returns final approximation error or -1 if the approximation error calculation has
     * been turned off (see {@link #setMaxIterations(int)}.
     * 
     * @return final approximation error or -1
     */
    public double getApproximationError()
    {
        return approximationError;
    }

    /**
     * Returns an array of approximation errors during after subsequent iterations of the
     * algorithm. Element 0 of the array contains the approximation error before the first
     * iteration. The array is <code>null</code> if the approximation error calculation
     * has been turned off (see {@link #setMaxIterations(int)}.
     */
    public double [] getApproximationErrors()
    {
        return approximationErrors;
    }

    public int getIterationsCompleted()
    {
        return iterationsCompleted;
    }

    /**
     * Returns <code>true</code> when the factorization is set to generate an ordered
     * basis.
     */
    public boolean isOrdered()
    {
        return ordered;
    }

    /**
     * Set to <code>true</code> to generate an ordered basis.
     */
    public void setOrdered(boolean ordered)
    {
        this.ordered = ordered;
    }

    /**
     * Returns column aggregates for a sorted factorization, and <code>null</code> for an
     * unsorted factorization.
     */
    public double [] getAggregates()
    {
        return aggregates;
    }
    
    /*
     * An attempt to isolate the AIOOB exception... no luck.
     */
    
    public static void main(String [] args)
    {
        while (true)
        {
            main0();
        }
    }

    public static void main0()
    {
        DoubleMatrix2D VT = new DenseDoubleMatrix2D(new double [][] {
            { 2.2257406540351653E-5, 1.2071023693753946E-26, 1.061219269332203E-37, 1.344212241637969E-75, 6.400659472326756E-90, 5.134887453779655E-4, 6.849872541277218E-4, 1.9097246493326433E-4, 4.069744180185159E-5, 2.69843066616651E-27, 2.6841342764195455E-35, 6.753195755117056E-12, 1.9626578605219688E-4, 1.2205509225633882E-5, 1.4358535295395569E-40, 6.5973254725755934E-80, 5.052336324868411E-4, 0.05693891924412174, 7.109352336761214E-40, 0.0038439057030338394, 2.14423165996733E-11, 8.794350545070527E-44, 2.3874357853545E-17, 0.01501916398663604, 3.450233530130748E-12, 0.06273782001264791, 7.910889435363546E-17, 0.0016217677447577005, 4.2659679213991984E-15, 0.001153267800074161, 5.49737183825318E-10, 3.4179687089718736E-109
            }, { 0.005959844642078099, 0.06953487692177461, 8.536435401609165E-102, 5.860416097455547E-78, 1.079280534110645E-84, 7.365283313862704E-6, 3.5964994159811066E-69, 3.893079796476791E-18, 0.0027866692325839012, 6.098305062634013E-32, 2.5985971892420417E-82, 1.4786881321768903E-10, 7.172325205560412E-11, 0.022937747736110094, 7.2753341878153435E-59, 1.0645226373221255E-83, 5.137434714394483E-4, 9.106590503943942E-10, 1.6566401816114593E-88, 8.043619213377271E-10, 8.255945010848652E-69, 2.9686046038630627E-78, 1.405270177483319E-5, 2.326086609416611E-26, 0.007815507703633548, 1.679393463331813E-30, 3.5648045208059085E-4, 0.011400381630801936, 3.787063518742861E-17, 5.411837003092966E-9, 3.237341280793787E-17, 4.504627954292496E-85, 
            }, { 3.614140552928874E-5, 5.601521163934916E-11, 3.788201160726079E-37, 4.071022174068035E-34, 4.502950822135628E-22, 8.98026513764296E-5, 0.0027656236840892133, 2.2135584565401392E-4, 0.032658138764950044, 2.2490938125833012E-11, 1.5175575439764726E-5, 0.0013591868717070024, 0.002400591124086327, 1.6194687298482172E-14, 8.703224638077778E-57, 5.051736143648927E-11, 3.663421294158789E-4, 2.3791226524993445E-5, 8.275966430849803E-15, 0.055475123095468565, 2.91610226591364E-11, 2.059662514797619E-25, 3.630872354000051E-27, 0.0013389215892610213, 0.06303286277018869, 1.4229934752662217E-100, 4.853141773959929E-26, 3.3706522820287428E-12, 5.049262717214708E-44, 6.170925616858194E-12, 2.696625700373731E-6, 1.855950336202053E-79, 
            }, { 2.100807472957377E-6, 9.18744404138076E-93, 4.259944832300584E-107, 5.652102929026567E-11, 0.002589733010027256, 0.001155402032648711, 4.3551864717782074E-8, 2.1509775665787075E-72, 0.003906530188498946, 4.257901751381503E-12, 4.411372760858196E-11, 5.352780621283852E-25, 7.086810379128628E-4, 5.175388266806939E-22, 4.218930764936624E-70, 1.3849633768890615E-8, 4.487063449648156E-29, 2.732869079109374E-34, 2.2970869152722675E-47, 1.7034473006294815E-16, 1.956632526389355E-25, 3.8291175417387984E-21, 7.047079926151611E-4, 0.005111484641467019, 3.9141056919246366E-11, 6.837867169351158E-80, 0.05444800504966511, 1.706932781763143E-23, 0.05638639427834855, 9.674267653233272E-24, 0.0602618412734346, 1.0076343390935648E-87
            }, { 2.7042526864904587E-10, 3.515887023765519E-4, 2.548805174441561E-61, 1.3071333897080997E-39, 5.950603639338244E-49, 8.000271453670229E-4, 0.0010650886423424905, 4.008591828582504E-32, 2.371471577930408E-5, 3.36070060982281E-12, 5.611268680299301E-6, 2.962748584517342E-16, 0.07209170353143854, 8.77453823223997E-6, 3.0903476392745875E-12, 4.9679775480353904E-42, 0.0514364083392455, 0.004277259273205262, 3.804649586313773E-17, 3.121779404537852E-5, 1.1956469868516773E-4, 1.029260856310047E-6, 2.9267025441773476E-22, 0.0014746153850059068, 8.682914788659068E-12, 6.514836131586907E-54, 2.9029839794651365E-13, 2.347962760245442E-5, 9.260172257920612E-68, 2.931863989154256E-12, 1.6424459086345025E-6, 2.8338046425955006E-75
            }, { 1.740171658775908E-4, 3.5570399971220736E-32, 1.5620595320755597E-84, 1.9552076447469733E-70, 1.930668227926425E-54, 3.1225851079970245E-6, 0.06307801374473898, 1.9615668119742132E-5, 1.4032803168336748E-12, 2.1580733467079658E-29, 0.007571277833748611, 0.01102837862665047, 1.7427930046521375E-4, 9.230761330577153E-4, 9.852300541102897E-51, 2.075742637737369E-5, 2.9033755502777735E-6, 2.668613128731423E-4, 5.857636356311105E-50, 1.548019032243396E-6, 1.992823313982637E-36, 1.3903216528988144E-22, 0.06342172526073854, 3.310359762673103E-8, 5.9774893568307384E-33, 1.738401709340802E-53, 0.0050501276444952226, 3.2394116136664536E-64, 3.7176194779431214E-19, 1.2534627259774093E-4, 2.748771032104295E-6, 1.2570629285445074E-95
            }, { 8.312548901341222E-18, 4.6730859577669566E-82, 1.1498635333518858E-67, 1.4258697514817082E-75, 7.968096794449937E-88, 0.027092476961592858, 0.003526993949793312, 2.994152419119884E-8, 5.199117805890779E-13, 1.4432061174249414E-9, 9.814233729554432E-4, 0.0636062344812213, 1.683430341659827E-13, 1.6147656031767676E-8, 0.0682099767057131, 5.59189640590394E-46, 2.530352119846206E-7, 2.3668562724624948E-12, 6.492964762574266E-26, 3.796487302732511E-4, 0.017675586868549923, 4.2330165193666616E-13, 2.309838037025625E-15, 0.009191725138020006, 2.5682164777828076E-26, 1.030074122005067E-78, 6.655014055564714E-13, 2.5433955546393922E-26, 1.264883924591732E-77, 6.895320709319583E-13, 1.7600755371629567E-62, 3.614163797008662E-43, 
            }, { 5.874878476096886E-19, 2.657429950213895E-16, 0.001544175753620477, 2.4451612136839797E-59, 0.002483800415787519, 4.5424362870512887E-4, 0.0043416810168499225, 0.0012124306176232598, 1.58518072688912E-84, 1.7567861964436684E-4, 0.058520823016211185, 2.8171171539053573E-5, 8.950956005629849E-12, 1.584223553256096E-49, 7.35989176482381E-50, 3.2206238202930613E-37, 0.020417692560348283, 1.216939614784801E-5, 1.5105379213904767E-75, 2.376963799579138E-8, 0.05457669997318883, 0.028969553972196197, 8.490183995651163E-50, 4.866924051093269E-19, 2.005789032060379E-30, 2.5274239785221772E-76, 4.781658654004639E-86, 3.1569379196506877E-51, 1.8053103242572307E-19, 2.0380584165473104E-56, 1.075446093374758E-85, 2.7537069221709067E-25
            }, { 1.1770826126853279E-5, 3.897467866531032E-65, 6.122740458142484E-59, 1.8718805883887875E-66, 5.3132646404448134E-68, 0.042776906397633735, 4.373335631575082E-30, 1.5479129076502802E-10, 0.024953770915080675, 9.257271942186477E-5, 0.00208657560013046, 1.1233214728231568E-5, 2.850970640461832E-11, 0.022730322027456288, 4.619473607589083E-5, 2.1407958198576694E-69, 3.4146733696144885E-6, 8.638816967647555E-8, 2.0272873002798627E-5, 0.010666039342563485, 0.008239358449958706, 0.021519484017203893, 5.443406291191974E-5, 2.865724487311789E-75, 7.5235794226216E-46, 2.3031500236471138E-107, 0.001170510178607286, 1.8102094763015889E-68, 6.49098275385887E-95, 0.06327446064809271, 2.0004081424559094E-35, 8.170056456131695E-14, 
            }, { 1.3504620981614808E-14, 0.019864894772400356, 9.177195353588176E-5, 1.5364411852875106E-84, 1.1225074791022236E-61, 9.246255865221045E-5, 3.3550138045936624E-31, 7.478760674626391E-4, 4.842796506645399E-6, 1.59699661208473E-4, 2.1823760804167257E-54, 1.3167890951684763E-7, 3.4442826055542936E-47, 0.046124673067596045, 5.199583031332242E-59, 7.246436174622957E-78, 3.5603620135919744E-6, 3.2704448675294974E-46, 1.0116330760935336E-68, 0.0028894926634140438, 9.999565130354134E-57, 6.971765405049961E-66, 2.972840444559718E-4, 0.015271651323724798, 5.388185561880736E-50, 2.0667503761379876E-11, 9.038071971027654E-30, 0.06313387795138793, 2.4991603384611167E-6, 0.0026064085124942646, 4.7722698050624E-5, 1.308372917220391E-79, 
            }, { 0.07230014655460179, 4.161141218054704E-8, 6.956198632601522E-95, 3.57922270544001E-7, 8.993912642892095E-4, 0.0023811130136018517, 1.6588314593789842E-5, 3.4858184945607677E-6, 5.332253393044488E-9, 2.7272092082388077E-5, 3.2160092905872384E-5, 0.003906779750514593, 2.928012632759952E-4, 1.0003369877180102E-5, 3.3856343609054464E-8, 2.2856018038203344E-4, 2.177039820304129E-4, 0.011265729444352881, 8.798202329873739E-81, 2.1847761299886466E-6, 7.954040638464922E-14, 6.303051801091158E-54, 1.3996838136098374E-5, 0.005434376185210008, 1.0402801478487945E-5, 3.202745939207775E-68, 0.007524000175276152, 3.7493931640742444E-11, 2.0092392808631555E-7, 0.015549000798213563, 6.615478666697145E-13, 6.0709804469144914E-96
            }, { 1.0074392586787441E-23, 2.0491308434197844E-61, 1.1974203480655074E-91, 1.011552615292075E-5, 0.04140937627797117, 0.00614974288171616, 2.115988843720454E-39, 3.681972341241975E-37, 9.025362358393712E-14, 0.06994677257470995, 0.014076308997998681, 2.5681671609862657E-31, 8.559064358299598E-10, 2.3612590283921855E-18, 2.7146238262766985E-29, 7.34847053299496E-10, 2.419747228991843E-20, 1.14773876128078E-19, 0.004034837537108514, 1.4319484137704215E-18, 2.36666864784102E-17, 0.001355285678984476, 1.1417045556326589E-18, 3.765683718171715E-60, 3.7293232568784845E-57, 3.589590220981099E-29, 2.5699174310666867E-18, 0.0012515345140521445, 2.1755022369822218E-6, 5.400297523120787E-15, 1.8146444539345352E-84, 5.418126444350841E-76, 
            }, { 2.1001310866190696E-14, 2.6529006397661023E-98, 1.2207143308712552E-88, 0.002709317123506444, 1.515524282668847E-45, 0.030245121266115928, 2.8378766602365302E-5, 2.0774074369791314E-18, 9.073380137858456E-18, 0.012500387760228111, 2.2820936131490558E-8, 3.76680149896607E-4, 2.287420707200674E-5, 8.932122686709499E-20, 6.868615276812199E-5, 3.186288492066631E-28, 6.197082867804471E-5, 2.957089668619476E-4, 0.0647712592952734, 4.5543099380374374E-7, 6.743053720168538E-8, 9.075472984253717E-23, 4.806470691205649E-15, 0.0013686693787390383, 1.045679787279773E-51, 3.957678388140618E-95, 6.75937030355245E-4, 8.887809448238464E-7, 2.2491837596113418E-5, 1.4548169077465647E-17, 3.797179680051802E-7, 1.236505174296063E-79, 
            }, { 2.981095583667497E-45, 2.4164257625955657E-41, 8.85136370476721E-5, 0.055974043478162705, 0.03443713159592424, 2.676661852371196E-43, 0.0015792457525516193, 9.767314145641433E-5, 1.2423419273211019E-84, 1.5021863541304446E-17, 2.1660904224233643E-11, 8.986430420897846E-13, 4.142245159078242E-10, 1.6914293873648134E-35, 7.699022282429376E-43, 0.06292907537850972, 2.826532641225932E-5, 1.0268101059958745E-14, 6.606435964799219E-9, 1.9013655150515235E-5, 1.1407442191708147E-37, 1.5527089273135837E-81, 2.407468311234625E-14, 7.804285475400988E-17, 1.6813176673354087E-58, 3.247587054021619E-69, 2.138275681371734E-76, 1.067908768316968E-68, 5.543143200877325E-6, 6.269389843767537E-42, 4.283318315234264E-23, 3.5208897364034337E-93
            }, { 3.2676851623816943E-22, 8.289109309814909E-15, 2.3331979759393564E-103, 1.4423263715861096E-74, 1.7293792024078637E-16, 2.877085721454861E-14, 5.95254822654156E-4, 1.2329002930110148E-84, 5.907726895779726E-25, 3.5165253492746556E-10, 1.5318853892110355E-5, 0.0016128836634459493, 5.0937537593272E-5, 2.2012103807326617E-17, 5.698611767868523E-13, 2.0067337078157953E-12, 7.84062456909995E-5, 0.0038428577057000443, 1.039988204298554E-82, 3.759288450427851E-5, 0.014990419662612702, 0.0024269211329531666, 1.988951717809833E-36, 0.011159687013263921, 6.067299590478363E-81, 1.8457660797712647E-14, 2.749730839540698E-13, 6.459287369122999E-4, 8.722640950563726E-4, 4.002352162271783E-27, 9.454562142329979E-5, 0.07299944298627106, 
            }, { 4.548272217471257E-6, 5.599204492271857E-16, 0.06173373148306021, 3.6900637586513686E-89, 2.7702395033332445E-84, 8.46907444648419E-6, 7.621896583285616E-8, 0.05952655757999205, 0.0042261117237311525, 3.702558765646957E-40, 7.911983683234714E-25, 2.1096464662381846E-13, 8.161399951029899E-4, 3.7896885117988194E-8, 1.0856114409642503E-83, 1.2970592915879978E-74, 0.029381729564482952, 8.320333835830944E-6, 9.854118768449589E-86, 4.3274013636202007E-4, 6.895713099176509E-17, 7.758493262034959E-7, 1.0301105361168689E-19, 6.858821892656703E-6, 0.0019231467489314164, 5.284580254436686E-61, 1.924209981853294E-4, 1.654117164382769E-52, 1.1803127314342225E-44, 1.6325728271211979E-4, 1.2493380547530173E-5, 2.832106585255714E-87, }            
        });

        double [] aggregates =
        {
            -0.00984281748853148, -0.009825928998124076, -0.00828159068687211,
            -0.00824022659874175, -0.008206604720647236, -0.008134430090410927,
            -0.007865301826379716, -0.0076885322005745245, -0.007623357174840662,
            -0.005276253356779391, -0.005704071674425006, -0.006862932408786219,
            -0.006757044414092535, -0.005595966777687804, -0.005702980593272206,
            -0.007423451070418778
        };

        Sorting.quickSort.sort(VT, aggregates);
    }
}
