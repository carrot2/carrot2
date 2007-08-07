package org.carrot2.webapp.stress;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.carrot2.util.RollingWindowAverage;
import org.carrot2.webapp.QueryProcessorServlet;

/**
 * A simple stress utility to put some load on the webapp.
 * 
 * @author Dawid Weiss
 */
public class StressApp
{
    private final static Logger logger = Logger.getLogger(StressApp.class);
    private final static Logger statsLogger = Logger.getLogger("stats");

    private final Random rnd = new Random();

    private final static int MAX_RUNNING_FETCHERS = 200;

    private final int AVG_INTERVAL = 1000 * 10;
    private final RollingWindowAverage successes = new RollingWindowAverage(AVG_INTERVAL, 100);
    private final RollingWindowAverage failures = new RollingWindowAverage(AVG_INTERVAL, 100);

    private String serviceURI;
    private String [] algorithms;
    private String [] inputs;
    private String [] queries;
    private int [] requestSizes; 

    private long initialPeriod;
    private long minimalPeriod;
    private long saturationTime;

    volatile long sleepTime;
    
    private long fetchers;

    public StressApp(
        String serviceURI, 
        String [] algorithms,
        String [] inputs,
        String [] queries,
        int [] requestSizes,
        long maxPeriod, long minPeriod, long saturationTime)
    {
        this.serviceURI = serviceURI;
        this.algorithms = algorithms;
        this.inputs = inputs;
        this.queries = queries;
        this.requestSizes = requestSizes;

        this.initialPeriod = maxPeriod;
        this.minimalPeriod = minPeriod;
        this.saturationTime = saturationTime;
    }

    public void start()
    {
        final Thread reporter = new Thread() {
            public void run()
            {
                final MessageFormat mf = new MessageFormat("{0,number,0.00}",
                    Locale.ENGLISH);
                while (!isInterrupted())
                {
                    try
                    {
                        Thread.sleep(1000);

                        final long successHits;
                        final long failureHits;
                        final long fetchers;
                        synchronized (StressApp.class)
                        {
                            successHits = successes.getUpdatesInWindow();
                            failureHits = failures.getUpdatesInWindow();
                            fetchers = StressApp.this.fetchers;
                        }

                        statsLogger.info(
                              "running-fetchers;"
                            + fetchers
                            + ";request-period;"
                            + mf.format(new Object [] { new Double(sleepTime / 1000.0d) })
                            + ";successes-in-window;" 
                            + successHits
                            + ";successes-per-sec;" 
                            + mf.format(new Object [] { new Double(((double) successHits / (AVG_INTERVAL / 1000))) })
                            + ";failures-in-window;"
                            + failureHits
                            + ";failures-per-sec;"
                            + mf.format(new Object [] { new Double(((double) failureHits / (AVG_INTERVAL / 1000))) })
                            );
                    }
                    catch (InterruptedException e)
                    {
                        return;
                    }
                }
            }
        };
        reporter.setPriority(Thread.MAX_PRIORITY);
        reporter.start();

        try
        {
            final long start = System.currentTimeMillis();
            while (true)
            {
                final long now = System.currentTimeMillis();
                sleepTime = (long) (initialPeriod - 
                    (initialPeriod - minimalPeriod) * (Math.min(1.0d, (now - start) / (double) saturationTime)));
                Thread.sleep(sleepTime);
                
                synchronized (StressApp.class) {
                    if (fetchers > MAX_RUNNING_FETCHERS) {
                        continue;
                    }
                }

                final String algorithm = random(algorithms);
                final String input = random(inputs);
                final String query = random(queries);
                final int size = random(requestSizes);
                doQuery(input, algorithm, size, query);
            }
        } 
        catch (InterruptedException e)
        {
            // fall through and return.
        }
        reporter.interrupt();
    }

    /**
     * Emulates a single "query" (page, document and clusters fetch). 
     */
    private void doQuery(String input, String algorithm, int requestSize, String query)
    {
        try
        {
            final String requestDetails = 
                    QueryProcessorServlet.PARAM_Q + "=" + URLEncoder.encode(query, "UTF-8")
            + "&" + QueryProcessorServlet.PARAM_INPUT + "=" + URLEncoder.encode(input, "UTF-8")
            + "&" + QueryProcessorServlet.PARAM_ALG + "=" + URLEncoder.encode(algorithm, "UTF-8")
            + "&" + QueryProcessorServlet.PARAM_SIZE + "=" + requestSize;

            // Construct the page, clusters and document URLs.
            final String pageURI = serviceURI + "?" + requestDetails;
            final String clustersURI = serviceURI + "?" + QueryProcessorServlet.PARAM_TYPE 
                + "=" + QueryProcessorServlet.TYPE_DOCUMENTS + "&" + requestDetails;
            final String documentURI = serviceURI + "?" + QueryProcessorServlet.PARAM_TYPE 
                + "=" + QueryProcessorServlet.TYPE_CLUSTERS  + "&" + requestDetails;

            // Randomize the order of document/clusters requests.
            final boolean clustersFirst;
            synchronized (StressApp.class)
            {
                clustersFirst = rnd.nextBoolean();
            }

            final String [] requests = new String [] {
                pageURI, 
                    clustersFirst ? clustersURI : documentURI, 
                    clustersFirst ? documentURI : clustersURI,
            };

            for (int i = 0; i < requests.length; i++)
            {
                fetch(requests[i]);
            }
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Start a HTTP connection in a new thread and fetch the result stream.
     */
    private void fetch(final String uri)
    {
        final Runnable task = new StreamFetcher(uri) {
            public void run()
            {
                try {
                    super.run();
                } finally {
                    synchronized (StressApp.class) {
                        StressApp.this.fetchers--;
                    }
                }
            }

            public void failure(String reason)
            {
                synchronized (StressApp.class)
                {
                    failures.add(System.currentTimeMillis(), 1);
                }
            }

            public void success(byte [] content)
            {
                synchronized (StressApp.class)
                {
                    successes.add(System.currentTimeMillis(), 1);
                }
            }
        };

        logger.debug("Fetching: " + uri);
        synchronized (StressApp.class) {
            StressApp.this.fetchers++;
            new Thread(task).start();
        }
    }

    private String random(String [] array)
    {
        synchronized (StressApp.class) {
            return array[rnd.nextInt(array.length)];
        }
    }

    private int random(int [] array)
    {
        synchronized (StressApp.class) {
            return array[rnd.nextInt(array.length)];
        }
    }

    public static void main(String [] args)
    {
        /** Command line options. */
        final Options options = createOptions();
        if (args.length == 0)
        {
            printUsage(options);
            return;
        }
        
        final StressApp app = parseOptions(options, args);
        if (app != null)
        {
            app.start();
        }
    }

    /**
     * 
     */
    private static StressApp parseOptions(Options options, String [] args)
    {
        final Parser parser = new GnuParser();
        final CommandLine line;
        try
        {
            line = parser.parse(options, args);
            try
            {
                return validateOptions(line);
            }
            catch (Throwable e)
            {
                logger.fatal("Unhandled program error occurred.", e);
            }
        }
        catch (MissingArgumentException e)
        {
            logger.log(Level.FATAL, "Provide the required argument for option "
                + e.getMessage());
            printUsage(options);
        }
        catch (MissingOptionException e)
        {
            logger.log(Level.FATAL, "Provide the required option " + e.getMessage());
            printUsage(options);
        }
        catch (UnrecognizedOptionException e)
        {
            logger.log(Level.FATAL, e.getMessage() + "\n");
            printUsage(options);
        }
        catch (ParseException exp)
        {
            logger.log(Level.FATAL, "Could not parse command line: " + exp.getMessage());
            printUsage(options);
        }

        return null;
    }

    private final static String SERVICE_OPTION = "u";
    private final static String ALGORITHMS_OPTION = "a";
    private final static String INPUTS_OPTION = "i";
    private final static String QUERIES_OPTION = "q";
    private final static String REQUEST_SIZE_OPTION = "s";
    private final static String TIMES_OPTION = "t";

    /**
     * 
     */
    private static StressApp validateOptions(CommandLine line)
    {
        final String serviceURL = line.getOptionValue(SERVICE_OPTION);
        final String [] algorithms = line.getOptionValues(ALGORITHMS_OPTION);
        final String [] inputs = line.getOptionValues(INPUTS_OPTION);
        final String [] queries = line.getOptionValues(QUERIES_OPTION);
        final int [] requestSizes = toIntArray(
            line.hasOption(REQUEST_SIZE_OPTION) 
                ? line.getOptionValues(REQUEST_SIZE_OPTION)
                : new String [] {"100"});
        
        final String [] times = line.getOptionValues(TIMES_OPTION);
        final int startPeriod = Integer.parseInt(times[0]);
        final int endPeriod = Integer.parseInt(times[1]);
        final int saturation = Integer.parseInt(times[2]);
        
        if (startPeriod < endPeriod) {
            logger.error("Start period must be >= than the end period.");
            return null;
        }

        return new StressApp(serviceURL, algorithms, inputs, queries,
            requestSizes, startPeriod, endPeriod, saturation);
    }

    /**
     * 
     */
    private static int [] toIntArray(String [] array)
    {
        final int [] requestSizes = new int [ array.length ];
        for (int i = 0; i < array.length; i++)
        {
            requestSizes[i] = Integer.parseInt(array[i]);
        }
        return requestSizes;
    }

    /**
     * 
     */
    private static Options createOptions()
    {
        final Options options = new Options();

        options.addOption(OptionBuilder
            .isRequired()
            .hasArg()
            .withDescription("Webapp query service URL (~/search)")
            .withArgName("URL")
            .create(SERVICE_OPTION));
        
        options.addOption(OptionBuilder
            .isRequired()
            .withDescription("Space separated list of algorithm names.")
            .withArgName("ID1 ID2...")
            .withLongOpt("algorithms")
            .hasArgs()
            .create(ALGORITHMS_OPTION));

        options.addOption(OptionBuilder
            .isRequired()
            .withDescription("Space separated list of input names.")
            .withArgName("ID1 ID2...")
            .withLongOpt("inputs")
            .hasArgs()
            .create(INPUTS_OPTION));

        options.addOption(OptionBuilder
            .isRequired()
            .withDescription("Space separated list of queries.")
            .withArgName("Q1 Q2 Q3...")
            .withLongOpt("queries")
            .hasArgs()
            .create(QUERIES_OPTION));

        options.addOption(OptionBuilder
            .withDescription("Space separated list of request sizes (default: 100).")
            .withArgName("SIZE1 SIZE2...")
            .withLongOpt("request-sizes")
            .hasArgs()
            .create(REQUEST_SIZE_OPTION));

        options.addOption(OptionBuilder
            .isRequired()
            .withDescription("Initial and final period between spawning new request " +
                    "threads and saturation time between the two (in milliseconds).")
            .withArgName("START END SATURATION")
            .withLongOpt("times")
            .hasArgs(3)
            .create(TIMES_OPTION));

        return options;
    }

    /**
     * 
     */
    private static void printUsage(Options options)
    {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(StressApp.class.getName(), options, true);
    }    
}
