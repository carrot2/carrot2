package org.carrot2.webapp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Random;

import org.apache.log4j.Logger;
import org.carrot2.util.RollingWindowAverage;

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

    private final int AVG_INTERVAL = 1000 * 10;
    private final RollingWindowAverage successes = new RollingWindowAverage(AVG_INTERVAL, 100);
    private final RollingWindowAverage failures = new RollingWindowAverage(AVG_INTERVAL, 100);

    private String serviceURI;
    private String [] algorithms;
    private String [] inputs;
    private String [] queries;

    public StressApp(String serviceURI, 
        String [] algorithms,
        String [] inputs,
        String [] queries)
    {
        this.serviceURI = serviceURI;
        this.algorithms = algorithms;
        this.inputs = inputs;
        this.queries = queries;
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
                        synchronized (successes)
                        {
                            synchronized (failures)
                            {
                                successHits = successes.getUpdatesInWindow();
                                failureHits = failures.getUpdatesInWindow();
                            }
                        }
                        statsLogger.info(
                            "successes;" 
                            + successHits
                            + ";successes-per-sec;" 
                            + ";" + mf.format(new Object [] { new Double(((double) successHits / (AVG_INTERVAL / 1000))) })
                            + ";failures;"
                            + failureHits
                            + ";failures-per-sec;"
                            + ";" + mf.format(new Object [] { new Double(((double) failureHits / (AVG_INTERVAL / 1000))) })
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
            long initialPeriod = 2000;
            long minimalPeriod = 100;
            long saturationTime = 30 * 1000;

            final long start = System.currentTimeMillis();
            while (true)
            {
                final long now = System.currentTimeMillis();
                
                long sleepTime = (long) (initialPeriod - 
                    (initialPeriod - minimalPeriod) * (Math.min(1.0d, (now - start) / (double) saturationTime)));
                logger.warn("Sleep time: " + sleepTime);
                Thread.sleep(sleepTime);

                final String algorithm = random(algorithms);
                final String input = random(inputs);
                final String query = random(queries);
                doQuery(input, algorithm, query);
            }
        } 
        catch (InterruptedException e)
        {
            // fall through and return.
        }
        reporter.interrupt();
    }

    private void doQuery(String input, String algorithm, String query)
    {
        try
        {
            final String requestDetails = 
                    QueryProcessorServlet.PARAM_Q + "=" + URLEncoder.encode(query, "UTF-8")
            + "&" + QueryProcessorServlet.PARAM_INPUT + "=" + URLEncoder.encode(input, "UTF-8")
            + "&" + QueryProcessorServlet.PARAM_ALG + "=" + URLEncoder.encode(algorithm, "UTF-8");

            // Construct the page, clusters and document URLs.
            final String pageURI = serviceURI + "?" + requestDetails;
            final String clustersURI = serviceURI + "?type=c&" + requestDetails;
            final String documentURI = serviceURI + "?type=d&" + requestDetails;
            
            // Randomize the order of document/clusters requests.
            final boolean clustersFirst;
            synchronized (rnd)
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
            public void failure(String reason)
            {
                synchronized (failures)
                {
                    failures.add(System.currentTimeMillis(), 1);
                }
            }

            public void success(byte [] content)
            {
                synchronized (successes)
                {
                    successes.add(System.currentTimeMillis(), 1);
                }
            }
        };
        logger.debug("Fetching: " + uri);
        new Thread(task).start();
    }

    private synchronized String random(String [] array)
    {
        return array[rnd.nextInt(array.length)];
    }

    public static void main(String [] args)
    {
        new StressApp(
            "http://localhost:8080/search",
            new String [] {"Lingo", "STC (+English)"},
            new String [] {"Web", "Yahoo!", "MSN"},
            new String [] {"data mining", "Dawid Weiss", "apple", "computer"}
        ).start();
    }
}
