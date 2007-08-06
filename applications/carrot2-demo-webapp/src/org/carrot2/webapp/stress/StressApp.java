package org.carrot2.webapp.stress;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Random;

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
                        synchronized (this)
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
                
                synchronized (this) {
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
            synchronized (this)
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
                    synchronized (StressApp.this) {
                        StressApp.this.fetchers--;
                    }
                }
            }

            public void failure(String reason)
            {
                synchronized (this)
                {
                    failures.add(System.currentTimeMillis(), 1);
                }
            }

            public void success(byte [] content)
            {
                synchronized (this)
                {
                    successes.add(System.currentTimeMillis(), 1);
                }
            }
        };

        logger.debug("Fetching: " + uri);
        synchronized (StressApp.this) {
            StressApp.this.fetchers++;
            new Thread(task).start();
        }
    }

    private synchronized String random(String [] array)
    {
        return array[rnd.nextInt(array.length)];
    }

    private synchronized int random(int [] array)
    {
        return array[rnd.nextInt(array.length)];
    }

    public static void main(String [] args)
    {
        new StressApp(
            // "http://localhost:8080/carrot2-demo-webapp/search",
            // "http://localhost:8080/search",
            "http://ophelia.cs.put.poznan.pl:8081/carrot2-demo-webapp/search",
            new String [] {"Lingo", /* "STC (+English)" */},
            new String [] {"Web", "Yahoo!", "MSN", "News"},
            new String [] {
                "data mining", "Dawid Weiss", "apple", "computer",
                "amiga", "iraq", "war", "guacamole", "global warming",
                "the times", "al arabia", "star wars" },
            new int [] { 100 /* 50, 75, 100, 150, 200*/ },
            2000, 0, 120 * 1000
        ).start();
    }
}
