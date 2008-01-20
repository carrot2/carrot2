
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.demo.swing;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.carrot2.core.LocalInputComponent;
import org.carrot2.core.MissingProcessException;
import org.carrot2.demo.DemoContext;
import org.carrot2.demo.ProcessSettings;
import org.carrot2.demo.swing.util.SwingTask;

/**
 * @author Stanislaw Osinski
 */
public class BenchmarkDialog
{
    /** */
    private JDialog dialog;

    /** */
    private DemoContext demoContext;

    /** */
    private String query;

    /** */
    private String processId;

    /** */
    private ProcessSettings processSettings;

    /** */
    private int requestedResults;

    /** */
    private Frame owner;
    private JProgressBar progressBar;
    private JButton stopButton;
    private JLabel avgLabel;
    private JLabel stdDevLabel;
    private JLabel minLabel;
    private JLabel maxLabel;
    private JSpinner warmupSpinner;
    private JSpinner benchmarkSpinner;
    private JSpinner threadsSpinner;

    /** */
    private BenchmarkThread [] benchmarkThreads;
    private int benchmarkThreadsRunning;

    private SpinnerNumberModel threadsSpinnerModel;

    private SpinnerNumberModel benchmarkSpinnerModel;

    private SpinnerNumberModel warmupSpinnerModel;

    private BenchmarkStats benchmarkStats;

    private WarmupThread warmupThread;

    /**
     * 
     */
    public BenchmarkDialog(Frame owner, DemoContext demoContext, String query,
        String processId, ProcessSettings processSettings, int requestedResults)
    {
        this.demoContext = demoContext;
        this.query = query;
        this.processId = processId;
        this.processSettings = processSettings;
        this.owner = owner;
        this.requestedResults = requestedResults;
    }

    /**
     * 
     */
    public void show()
    {
        if (dialog == null)
        {
            dialog = new JDialog(owner, demoContext.getProcessIdToProcessNameMap().get(
                processId)
                + " benchmark", true);
            dialog.setModal(true);
            dialog.addWindowListener(new WindowAdapter()
            {
                public void windowClosed(WindowEvent e)
                {
                }

                public void windowClosing(WindowEvent e)
                {
                    if (areBenchmarksRunning())
                    {
                        cancelBenchmarks();
                        benchmarkCancelled();
                    }
                    dialog.setVisible(false);
                }
            });
            dialog.getContentPane().add(buildUI());
            dialog.pack();
            dialog.setLocation((owner.getLocation().x + (owner.getWidth() - dialog
                .getWidth()) / 2), (owner.getLocation().y + (owner.getHeight() - dialog
                .getHeight()) / 2));

            SwingUtils.addEscapeKeyCloseAction(dialog);
        }

        dialog.setVisible(true);
    }

    private void runBenchmark()
    {
        warmupSpinner.setEnabled(false);
        benchmarkSpinner.setEnabled(false);
        threadsSpinner.setEnabled(false);

        progressBar.setValue(0);
        progressBar.setMaximum(warmupSpinnerModel.getNumber().intValue()
            + benchmarkSpinnerModel.getNumber().intValue());
        progressBar.setString("Warming up...");

        benchmarkStats = new BenchmarkStats();

        warmupThread = new WarmupThread(warmupSpinnerModel.getNumber().intValue(),
            benchmarkStats);
        warmupThread.start();
    }

    private void warmupThreadFinished()
    {
        // Run benchmark threads
        benchmarkThreads = new BenchmarkThread [threadsSpinnerModel.getNumber()
            .intValue()];
        final int benchmarkRounds = benchmarkSpinnerModel.getNumber().intValue();
        for (int i = 0; i < benchmarkThreads.length; i++)
        {
            benchmarkThreads[i] = new BenchmarkThread(benchmarkRounds
                / benchmarkThreads.length
                + (i < (benchmarkRounds % benchmarkThreads.length) ? 1 : 0), benchmarkStats);
        }
        for (int i = 0; i < benchmarkThreads.length; i++)
        {
            benchmarkThreads[i].start();
            benchmarkThreadsRunning++;
        }
        SwingTask.runNow(new Runnable()
        {
            public void run()
            {
                progressBar.setString("Benchmarking...");
            }
        });
    }

    private boolean areBenchmarksRunning()
    {
        return (warmupThread != null && warmupThread.isRunning())
            || benchmarkThreadsRunning != 0;
    }

    private void cancelBenchmarks()
    {
        warmupThread.cancel();
        if (benchmarkThreads != null)
        {
            for (int i = 0; i < benchmarkThreads.length; i++)
            {
                benchmarkThreads[i].cancel();
            }
        }
    }

    private void updateBenchmarkResults(BenchmarkStats stats)
    {
        final BenchmarkStats statsClone = (stats != null ? stats.getClone() : null);

        SwingTask.runNow(new Runnable()
        {
            public void run()
            {
                NumberFormat format = NumberFormat.getNumberInstance();
                format.setMinimumFractionDigits(2);
                format.setMaximumFractionDigits(2);

                progressBar.setValue(statsClone.warmup + statsClone.count);
                avgLabel.setText(format.format(statsClone.avg) + " ms");
                stdDevLabel.setText(format.format(statsClone.stdDev) + " ms");
                minLabel.setText(format.format(statsClone.min) + " ms");
                maxLabel.setText(format.format(statsClone.max) + " ms");
            }
        });
    }

    private void updateWarmupResults(BenchmarkStats stats)
    {
        final BenchmarkStats statsClone = (stats != null ? stats.getClone() : null);

        SwingTask.runNow(new Runnable()
        {
            public void run()
            {
                progressBar.setValue(statsClone.warmup + statsClone.count);
            }
        });
    }

    private synchronized void benchmarkThreadFinished()
    {
        benchmarkThreadsRunning--;
        if (benchmarkThreadsRunning == 0)
        {
            benchmarkFinished();
        }
    }

    private void benchmarkFinished()
    {
        SwingTask.runNow(new Runnable()
        {
            public void run()
            {
                warmupSpinner.setEnabled(true);
                benchmarkSpinner.setEnabled(true);
                threadsSpinner.setEnabled(true);
                stopButton.setText("Start");
                progressBar.setString("Finished");
            }
        });
    }

    private void benchmarkCancelled()
    {
        SwingTask.runNow(new Runnable()
        {
            public void run()
            {
                warmupSpinner.setEnabled(true);
                benchmarkSpinner.setEnabled(true);
                threadsSpinner.setEnabled(true);
                stopButton.setText("Start");
                SwingTask.runNow(new Runnable()
                {
                    public void run()
                    {
                        progressBar.setString("Cancelled");
                    }
                });
            }
        });
    }

    private JPanel buildUI()
    {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        JPanel buttonPanel = buildButtonPanel();
        JPanel contentPanel = buildContentPanel();

        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel buildContentPanel()
    {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 3));

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(3, 2, 0, 3));

        settingsPanel.add(new JLabel("JVM warm-up cycles"));
        warmupSpinnerModel = new SpinnerNumberModel(25, 0, 1000000, 1);
        warmupSpinner = new JSpinner(warmupSpinnerModel);
        settingsPanel.add(warmupSpinner);

        settingsPanel.add(new JLabel("Benchmark cycles"));
        benchmarkSpinnerModel = new SpinnerNumberModel(75, 1, 1000000, 1);
        benchmarkSpinner = new JSpinner(benchmarkSpinnerModel);
        settingsPanel.add(benchmarkSpinner);

        settingsPanel.add(new JLabel("Benchmark threads"));
        threadsSpinnerModel = new SpinnerNumberModel(1, 1, 32, 1);
        threadsSpinner = new JSpinner(threadsSpinnerModel);
        settingsPanel.add(threadsSpinner);

        final TitledBorder titledBorder = BorderFactory
            .createTitledBorder("Benchmark settings");
        titledBorder.setTitleFont(warmupSpinner.getFont());
        settingsPanel.setBorder(BorderFactory.createCompoundBorder(titledBorder,
            BorderFactory.createEmptyBorder(0, 5, 5, 5)));

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout());
        final TitledBorder titledBorder2 = BorderFactory
            .createTitledBorder("Benchmark progress & results");
        titledBorder2.setTitleFont(warmupSpinner.getFont());
        resultsPanel.setBorder(BorderFactory.createCompoundBorder(titledBorder2,
            BorderFactory.createEmptyBorder(0, 5, 5, 5)));

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BorderLayout(5, 0));
        stopButton = new JButton("Start");
        stopButton.setPreferredSize(new Dimension(60, 22));
        stopButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (areBenchmarksRunning())
                {
                    cancelBenchmarks();
                    benchmarkCancelled();
                }
                else
                {
                    stopButton.setText("Stop");
                    avgLabel.setText("n/a");
                    stdDevLabel.setText("n/a");
                    minLabel.setText("n/a");
                    maxLabel.setText("n/a");
                    runBenchmark();
                }
            }
        });

        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(280, stopButton.getHeight()));
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(stopButton, BorderLayout.AFTER_LINE_ENDS);

        JPanel statsPanel = new JPanel();
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 2, 5, 0));
        statsPanel.setLayout(new GridLayout(2, 4, 0, 3));
        avgLabel = makeBoldLabel("n/a");
        stdDevLabel = makeBoldLabel("n/a");
        minLabel = makeBoldLabel("n/a");
        maxLabel = makeBoldLabel("n/a");
        statsPanel.add(new JLabel("Average time"));
        statsPanel.add(avgLabel);
        statsPanel.add(new JLabel("Time std dev"));
        statsPanel.add(stdDevLabel);
        statsPanel.add(new JLabel("Min time"));
        statsPanel.add(minLabel);
        statsPanel.add(new JLabel("Max time"));
        statsPanel.add(maxLabel);

        resultsPanel.add(progressPanel, BorderLayout.PAGE_START);
        resultsPanel.add(statsPanel, BorderLayout.PAGE_END);

        contentPanel.add(settingsPanel, BorderLayout.PAGE_START);
        contentPanel.add(resultsPanel, BorderLayout.PAGE_END);
        return contentPanel;
    }

    private JLabel makeBoldLabel(String text)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont()
            .getSize()));

        return label;
    }

    private JPanel buildButtonPanel()
    {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 5));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (areBenchmarksRunning())
                {
                    cancelBenchmarks();
                    benchmarkThreadFinished();
                }
                dialog.setVisible(false);
            }
        });

        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private class BenchmarkThread extends Thread
    {
        private int benchmarkCycles;

        private boolean running = false;

        private BenchmarkStats stats;

        public BenchmarkThread(int benchmarkCycles, BenchmarkStats stats)
        {
            this.benchmarkCycles = benchmarkCycles;
            this.stats = stats;
            this.running = false;
        }

        public void run()
        {
            running = true;
            try
            {
                // Warmup
                final Map requestParams = processSettings.getRequestParams();
                requestParams.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, Integer
                    .toString(requestedResults));
                long start;
                long stop;

                for (int i = 0; i < benchmarkCycles && running; i++)
                {
                    start = System.currentTimeMillis();
                    demoContext.getController().query(processId, query, requestParams);
                    stop = System.currentTimeMillis();

                    stats.updateStats(stop - start);
                    updateBenchmarkResults(stats);
                }

                if (running)
                {
                    benchmarkThreadFinished();
                }
                running = false;
            }
            catch (MissingProcessException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void cancel()
        {
            running = false;
        }

        public boolean isRunning()
        {
            return running;
        }
    }

    private class WarmupThread extends Thread
    {
        private int warmupCycles;
        private boolean running = false;
        private BenchmarkStats stats;

        public WarmupThread(int warmupCycles, BenchmarkStats stats)
        {
            this.warmupCycles = warmupCycles;
            this.stats = stats;
            this.running = false;
        }

        public void run()
        {
            running = true;
            try
            {
                // Warmup
                final Map requestParams = processSettings.getRequestParams();
                requestParams.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, Integer
                    .toString(requestedResults));
                for (int i = 0; i < warmupCycles && running; i++)
                {
                    demoContext.getController().query(processId, query, requestParams);
                    stats.updateWarmupCount();
                    updateWarmupResults(stats);
                }

                if (running)
                {
                    warmupThreadFinished();
                }
                running = false;
            }
            catch (MissingProcessException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void cancel()
        {
            running = false;
        }

        public boolean isRunning()
        {
            return running;
        }
    }

    /**
     * A thread-safe container for benchmark stats.
     * 
     * @author Stanislaw Osinski
     */
    private class BenchmarkStats
    {
        double avg = 0;
        double stdDev = 0;
        double min = 10e9;
        double max = 0;
        double sum = 0;
        double sumSquares = 0;
        int count = 0;
        int warmup = 0;

        public synchronized void updateWarmupCount()
        {
            warmup++;
        }

        public synchronized void updateStats(long time)
        {
            count++;
            sum += time;
            avg = sum / count;
            min = Math.min(min, time);
            max = Math.max(max, time);
            sumSquares += time * time;

            // We assume the measurements are independent events
            stdDev = Math.sqrt(sumSquares / count - avg * avg);
        }

        public synchronized BenchmarkStats getClone()
        {
            BenchmarkStats clone = new BenchmarkStats();

            clone.avg = this.avg;
            clone.stdDev = this.stdDev;
            clone.min = this.min;
            clone.max = this.max;
            clone.sum = this.sum;
            clone.sumSquares = this.sumSquares;
            clone.count = this.count;
            clone.warmup = this.warmup;

            return clone;
        }
    }
}
