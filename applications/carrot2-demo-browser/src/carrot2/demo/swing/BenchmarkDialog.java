
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

package carrot2.demo.swing;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import carrot2.demo.DemoContext;
import carrot2.demo.ProcessSettings;
import carrot2.demo.swing.util.SwingTask;

import com.dawidweiss.carrot.core.local.LocalInputComponent;
import com.dawidweiss.carrot.core.local.MissingProcessException;

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

    /** */
    private BenchmarkThread benchmarkThread;

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
            dialog = new JDialog(owner, demoContext
                .getProcessIdToProcessNameMap().get(processId)
                + " benchmark", true);
            dialog.setModal(true);
            dialog.addWindowListener(new WindowAdapter()
            {
                public void windowClosed(WindowEvent e)
                {
                }

                public void windowClosing(WindowEvent e)
                {
                    if (benchmarkThread != null && benchmarkThread.isRunning())
                    {
                        benchmarkThread.cancel();
                        benchmarkFinished();
                    }
                    dialog.setVisible(false);
                }
            });
            dialog.getContentPane().add(buildUI());
            dialog.pack();
            dialog.setLocation((int) (owner.getLocation().getX() + (owner
                .getWidth() - dialog.getWidth()) / 2),
                (int) (owner.getLocation().getY() + (owner.getHeight() - dialog
                    .getHeight()) / 2));
            
            SwingUtils.addEscapeKeyCloseAction(dialog);
        }

        dialog.setVisible(true);
    }

    /**
     * 
     */
    private void runBenchmark(int warmup, int benchmark)
    {
        progressBar.setValue(0);
        progressBar.setMaximum(warmup + benchmark);
        warmupSpinner.setEnabled(false);
        benchmarkSpinner.setEnabled(false);

        benchmarkThread = new BenchmarkThread(warmup, benchmark);
        benchmarkThread.start();

    }

    private void updateResults(final int value, final String message,
        final String avg, final String stdDev, final String min,
        final String max)
    {
        SwingTask.runNow(new Runnable()
        {
            public void run()
            {
                progressBar.setValue(value);
                progressBar.setString(message);
                avgLabel.setText(avg);
                stdDevLabel.setText(stdDev);
                minLabel.setText(min);
                maxLabel.setText(max);
            }
        });
    }

    private void benchmarkFinished()
    {
        SwingTask.runNow(new Runnable()
        {
            public void run()
            {
                warmupSpinner.setEnabled(true);
                benchmarkSpinner.setEnabled(true);
                stopButton.setText("Start");
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
        settingsPanel.setLayout(new GridLayout(2, 2, 0, 3));
        settingsPanel.add(new JLabel("JVM warm-up cycles"));
        final SpinnerNumberModel warmupSpinnerModel = new SpinnerNumberModel(
            25, 0, 1000000, 1);
        warmupSpinner = new JSpinner(warmupSpinnerModel);
        settingsPanel.add(warmupSpinner);
        settingsPanel.add(new JLabel("Benchmark cycles"));
        final SpinnerNumberModel benchmarkSpinnerModel = new SpinnerNumberModel(
            75, 1, 1000000, 1);
        benchmarkSpinner = new JSpinner(benchmarkSpinnerModel);
        settingsPanel.add(benchmarkSpinner);

        final TitledBorder titledBorder = BorderFactory
            .createTitledBorder("Benchmark settings");
        titledBorder.setTitleFont(warmupSpinner.getFont());
        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
            titledBorder, BorderFactory.createEmptyBorder(0, 5, 5, 5)));

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout());
        final TitledBorder titledBorder2 = BorderFactory
            .createTitledBorder("Benchmark progress & results");
        titledBorder2.setTitleFont(warmupSpinner.getFont());
        resultsPanel.setBorder(BorderFactory.createCompoundBorder(
            titledBorder2, BorderFactory.createEmptyBorder(0, 5, 5, 5)));

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BorderLayout(5, 0));
        stopButton = new JButton("Start");
        stopButton.setPreferredSize(new Dimension(60, 22));
        stopButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (benchmarkThread != null && benchmarkThread.isRunning())
                {
                    benchmarkThread.cancel();
                    benchmarkFinished();
                }
                else
                {
                    stopButton.setText("Stop");
                    runBenchmark(warmupSpinnerModel.getNumber().intValue(),
                        benchmarkSpinnerModel.getNumber().intValue());
                }
            }
        });

        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar
            .setPreferredSize(new Dimension(280, stopButton.getHeight()));
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
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, label
            .getFont().getSize()));

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
                if (benchmarkThread != null && benchmarkThread.isRunning())
                {
                    benchmarkThread.cancel();
                    benchmarkFinished();
                }
                dialog.setVisible(false);
            }
        });

        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    /**
     * @author Stanislaw Osinski
     */
    private class BenchmarkThread extends Thread
    {
        /** */
        private int warmupCycles;

        private int benchmarkCycles;

        private boolean running = false;

        double avg = 0;
        double stdDev = 0;
        double min = 10e9;
        double max = 0;
        double sum = 0;
        double sumSquares = 0;
        int count = 0;

        public BenchmarkThread(int warmupCycles, int benchmarkCycles)
        {
            this.warmupCycles = warmupCycles;
            this.benchmarkCycles = benchmarkCycles;
            this.running = false;
        }

        public void run()
        {
            NumberFormat format = NumberFormat.getNumberInstance();
            format.setMinimumFractionDigits(2);
            format.setMaximumFractionDigits(2);

            running = true;
            try
            {
                // Warmup
                final HashMap requestParams = processSettings
                    .getRequestParams();
                requestParams.put(LocalInputComponent.PARAM_REQUESTED_RESULTS,
                    Integer.toString(requestedResults));
                for (int i = 0; i < warmupCycles && running; i++)
                {
                    demoContext.getController().query(processId, query,
                        requestParams);
                    updateResults(i, "Warming up...", "n/a", "n/a", "n/a",
                        "n/a");
                }

                if (!running)
                {
                    benchmarkFinished();
                    running = false;
                    return;
                }

                long start;
                long stop;

                for (int i = 0; i < benchmarkCycles && running; i++)
                {
                    start = System.currentTimeMillis();
                    demoContext.getController().query(processId, query,
                        requestParams);
                    stop = System.currentTimeMillis();

                    updateStats(stop - start);
                    updateResults(warmupCycles + i, "Benchmarking...", format
                        .format(avg)
                        + " ms", format.format(stdDev) + " ms", format
                        .format(min)
                        + " ms", format.format(max) + " ms");
                }

                updateResults(warmupCycles + benchmarkCycles, "Finished",
                    format.format(avg) + " ms", format.format(stdDev) + " ms",
                    format.format(min) + " ms", format.format(max) + " ms");
                benchmarkFinished();
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

        private void updateStats(long time)
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

        public void cancel()
        {
            running = false;
        }

        public boolean isRunning()
        {
            return running;
        }
    }
}
