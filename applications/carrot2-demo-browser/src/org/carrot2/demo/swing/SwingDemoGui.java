
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
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

import javax.swing.*;

import org.apache.log4j.Logger;
import org.carrot2.demo.DemoContext;
import org.carrot2.demo.ProcessSettings;
import org.carrot2.demo.swing.util.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

/**
 * Swing-based GUI for the Carrot2 demo.
 * 
 * @author Dawid Weiss
 */
public final class SwingDemoGui {

    /** Search banner color. */
    private static final Color BANNER_COLOR = new Color(0xe0, 0xe0, 0xe0);
    private SwingDemoGuiConfig config;

    /** Main demo object (application context). */
    private final DemoContext demoContext;
    
    /** Main demo frame */
    private final JFrame frame;
    
    /** Main demo frame title */
    private final String mainFrameTitle;

    /** Tabbed pane with search results. */
    private JTabbedPane tabbedPane;
    
    /* Search UI components */
    private JTextField  queryField;
    private JComboBox   processComboBox;
    private JComboBox   sizeComboBox;
    private JButton     processSettingsButton;

    /** A combo model for displaying process names */
    private MapComboModel processComboModel;

    /**
     * A listener for new queries.
     */
    private class NewQueryListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // make sure all required data is available.
            final String processId = (String) processComboModel.getSelectedKey();
            final String query = queryField.getText();
            final int requestedResults = Integer.parseInt((String) sizeComboBox.getSelectedItem());
            if (processId == null) {
                JOptionPane.showMessageDialog(queryField, "Select a process first.");
            } else {
                performQuery(processId, query, requestedResults);
            }
        }
    }    
    
    /**
     * A task that switches user interface on or off.
     */
    private final class UISwitchTask implements Runnable {
        private final boolean onOff;
        
        public UISwitchTask(boolean onOff) {
            this.onOff = onOff;
        }

        public void run() {
            synchronized (this) {
                queryField.setEnabled(onOff);
                processComboBox.setEnabled(onOff);
                sizeComboBox.setEnabled(onOff);
                processSettingsButton.setEnabled(onOff);
            }
        }
    }

    /**
     * Creates a new object attached to a demo context. Call {@link #display()} to display
     * the demo frame.
     */
    public SwingDemoGui(DemoContext carrotDemo, String frameTitle) {
        this.demoContext = carrotDemo;
        this.mainFrameTitle = frameTitle;
        this.frame = new JFrame();
        this.config = new SwingDemoGuiConfig("/config/browser-config.xml");
    }

    /**
     * Displays the demo frame.
     */
    public void display(final JWindow splash) {
        try {
            configureUI();
        } catch (Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
        }

        frame.setTitle(mainFrameTitle);
        frame.setIconImage(new ImageIcon(this.getClass().getResource("/res/browser-icon.png")).getImage());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(buildMainPanel());
        frame.pack();

        // Prevent from resizing below preferred size.
        frame.addComponentListener(new KeepMimumumFrameSizeListener(frame.getPreferredSize()));

        disableUI();
        queryField.setText("Please wait...");

        SwingUtils.centerFrameOnScreen(frame);

        Runnable task = new Runnable()
        {
            public void run()
            {
                // replace the combo box's model.
                try {
                    demoContext.initialize();
                } catch (Exception e) {
                    SwingUtils.showExceptionDialog(frame, "Program startup failed.", e);
                    frame.dispose();
                    return;
                }
        
                processComboModel = new MapComboModel(demoContext.getProcessIdToProcessNameMap());
                processComboBox.setModel(processComboModel);
                if (demoContext.getDefaultProcessId() != null) {
                    processComboBox.setSelectedItem(
                            demoContext.getProcessIdToProcessNameMap().get(
                                    demoContext.getDefaultProcessId()));
                }
                queryField.setText("");
                queryField.requestFocus();

                frame.setVisible(true);
                frame.toFront();

                enableUI();
                if (splash.isVisible()) splash.dispose();
            }
        };
        SwingUtilities.invokeLater(task);
    }

    /**
     * Configures the UI; tries to set the system look on Mac, 
     * <code>WindowsLookAndFeel</code> on general Windows, and
     * <code>Plastic3DLookAndFeel</code> on Windows XP and all other OS.<p>
     * 
     * The JGoodies Swing Suite's <code>ApplicationStarter</code>,
     * <code>ExtUIManager</code>, and <code>LookChoiceStrategies</code>
     * classes provide a much more fine grained algorithm to choose and
     * restore a look and theme.
     * 
     * @author Karsten Lentzsch
     */
    private void configureUI() {
        UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
        Options.setDefaultIconSize(new Dimension(18, 18));

        String lafName =
            LookUtils.IS_OS_WINDOWS_XP
                ? Options.getCrossPlatformLookAndFeelClassName()
                : Options.getSystemLookAndFeelClassName();

        try {
            UIManager.setLookAndFeel(lafName);
        } catch (Exception e) {
            System.err.println("Can't set look & feel:" + e);
        }
    }

    /**
     * Performs a query on the given process identifier and displays a new tab with the
     * result.  
     */
    private void performQuery(String processId, String query, int requestedResults) {
        // ready to process the query, disable the user interface for the time it takes
        // to spawn the query processing thread.
        disableUI();
        try {
            final ProcessSettings settings = demoContext.getSettingsObject(processId);
            if (false == settings.isConfigured()) {
                // Return immediately -- the process is not configured properly.
                JOptionPane.showMessageDialog(queryField, "Process not properly configured (see settings).");
                return;
            }

            // Create a visual component for storing the result (or displaying progress).
            String tabName = query;
            if ("".equals(query)) {
                tabName = "<empty>";
            } else if (tabName.length() > 20) {
                tabName = tabName.substring(0, 20) + "...";
            }
            final ResultsTab resultsTab = new ResultsTab(frame, query, demoContext, settings.createClone(), processId, requestedResults);
            tabbedPane.addTab(tabName, resultsTab);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
            tabbedPane.setToolTipTextAt(tabbedPane.getTabCount()-1, query);

            resultsTab.performQuery();
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(queryField, "Exception executing query: \n"
                    + t.toString());
        } finally {
            enableUI();
            queryField.requestFocus();
        }
    }

    /**
     * Disables interactive user interface elements.
     */
    public void disableUI() {
        SwingTask.runNow(new UISwitchTask(false));
    }

    /**
     * Enables interactive user interface elements.
     */
    public void enableUI() {
        SwingTask.runNow(new UISwitchTask(true));
    }
    
    /**
     * Builds the main split between top panel and the tabbed pane with search results.
     */
    private JComponent buildMainPanel() {
        final JPanel mainPanel = new JPanel();
        final GridBagLayout layout = new GridBagLayout();
        mainPanel.setLayout(layout);
        GridBagConstraints cc;

        final JComponent topPanel = buildTopPanel();
        cc = new GridBagConstraints(
                0, 0,
                1, 1,
                0, 0,
                GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,
                new Insets(0,0,2,0), 
                0, 0);
        layout.setConstraints(topPanel, cc);
        mainPanel.add(topPanel, cc);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        tabbedPane.setTabPlacement(SwingConstants.TOP);
        tabbedPane.setPreferredSize(new Dimension(300,300));
        tabbedPane.putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE);
        tabbedPane.putClientProperty(Options.NO_CONTENT_BORDER_KEY, Boolean.FALSE);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());

        final AbstractAction closeTabAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (tabbedPane.getTabCount() > 0) {
                    final int selected = tabbedPane.getSelectedIndex();
                    if (selected >= 0) {
                        ResultsTab tab = (ResultsTab) tabbedPane.getComponent(selected);
                        tab.cleanup();
                    }
                }
            }
        };
        final Object delTabKey = "deltab";
        tabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK), delTabKey);
        tabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_DOWN_MASK), delTabKey);
        tabbedPane.getActionMap().put(delTabKey, closeTabAction);

        // this trick is needed so that we can enable/disable the tabbed pane
        final JPanel tabbedPaneContainer = new JPanel(new BorderLayout());
        tabbedPaneContainer.add(tabbedPane, BorderLayout.CENTER);
        
        cc = new GridBagConstraints(
                0, 1,
                1, 1,
                1, 1,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(0,0,0,0), 
                0, 0);
        layout.setConstraints(tabbedPaneContainer, cc);
        mainPanel.add(tabbedPaneContainer);
        mainPanel.setPreferredSize(new Dimension(config.mainWindowWidth, config.mainWindowHeight));

        return mainPanel;
    }

    /**
     * Builds the top panel with a split between search form and 
     * Carrot2 logo. 
     */
    private JComponent buildTopPanel() {
        final JPanel topPanel = new JPanel();
        topPanel.setBackground(BANNER_COLOR);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
        final FormLayout layout = new FormLayout(
                // columns
                "4px, fill:max(pref;550px):grow, 8px, center:pref, 4px",
                // rows
                "4px, pref, 4px");
        topPanel.setLayout( layout );

        final CellConstraints cc = new CellConstraints();

        final JComponent queryForm = buildQueryForm();
        topPanel.add(queryForm, cc.xywh(2,2,1,1));

        final ImageIcon carrotLogo = new ImageIcon(
                this.getClass().getClassLoader().getResource("res/browser-logo.png"));
        final JLabel label = new JLabel(carrotLogo);
        topPanel.add(label, cc.xywh(4,2,1,1));

        return topPanel;
    }

    /**
     * Builds query form in the top panel.
     */
    private JComponent buildQueryForm() {
        final FormLayout layout = new FormLayout(
                // columns
                "right:pref,4px,fill:min:grow,2px,right:min",
                // rows
                "pref:grow, 2px, pref");
        final JPanel topPanel = new JPanel(layout);
        topPanel.setOpaque(false);

        final CellConstraints cc = new CellConstraints();

        JLabel queryLabel = new JLabel("Query:", SwingConstants.RIGHT);
        queryLabel.setPreferredSize(new Dimension(42, queryLabel.getPreferredSize().height));
        topPanel.add(queryLabel, cc.xywh(1,1,1,1));
        
        queryField = new JTextField();
        queryField.setToolTipText("The query to be sent to the input component (syntax depends on the input).");
        queryField.addActionListener(new NewQueryListener());
        topPanel.add(queryField, cc.xy(3,1, CellConstraints.FILL, CellConstraints.FILL));

        final JButton searchButton = new JButton("Search");
        topPanel.add(searchButton, cc.xy(5,1, CellConstraints.FILL, CellConstraints.FILL));
        searchButton.addActionListener(new NewQueryListener());

        // build details panel.
        final FormLayout layout2 = new FormLayout(
                "right:pref,4px,fill:max(200;pref),2px,min,16px,right:default,max(pref;50px)",
                "pref");
        JPanel detailsPanel = new JPanel(layout2);
        detailsPanel.setOpaque(false);
        topPanel.add(detailsPanel, cc.xywh(1,3,2,1, CellConstraints.LEFT, CellConstraints.DEFAULT));

        this.processSettingsButton = new JButton("Settings");
        processSettingsButton.setToolTipText("Displays process settings window. Disabled if no settings.");
        this.processSettingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String processId = (String) processComboModel.getSelectedKey();
                final ProcessSettings settings = demoContext.getSettingsObject(processId);

                final JScrollPane scrollPane = new JScrollPane(settings.getSettingsComponent(frame));
                final Dimension dim = scrollPane.getPreferredSize();
                scrollPane.setPreferredSize(new Dimension(dim.width, Math.min(640, (int) dim.getHeight())));
                scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());
                JOptionPane.showMessageDialog(frame,
                        new Object[] {scrollPane}, "Default settings", 
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        detailsPanel.add(processSettingsButton, cc.xy(5,1, CellConstraints.DEFAULT, CellConstraints.FILL));

        JLabel processLabel = new JLabel("Process:", SwingConstants.RIGHT);
        processLabel.setPreferredSize(queryLabel.getPreferredSize());
        detailsPanel.add(processLabel, cc.xy(1,1));
        this.processComboBox = new JComboBox();
        this.processComboBox.setToolTipText("A chain of components used to process the query.");
        this.processComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String processId = (String) processComboModel.getSelectedKey();
                processSettingsButton.setVisible(demoContext.getSettingsObject(processId).hasSettings());
            }
        });
        // Hack: We need to hard-code the width to avoid JDIC overlap problem...
        processComboBox.setPreferredSize(new Dimension(250, processComboBox.getPreferredSize().height));
        detailsPanel.add(processComboBox, cc.xy(3,1, CellConstraints.DEFAULT, CellConstraints.FILL));

        detailsPanel.add(new JLabel("Results:"), cc.xy(7,1));
        this.sizeComboBox = new JComboBox(config.requestedResultCounts);
        this.sizeComboBox.setToolTipText("Number of results to acquire from the input source.");
        this.sizeComboBox.setSelectedItem(config.selectedResultCount);
        detailsPanel.add(sizeComboBox, cc.xy(8,1, CellConstraints.DEFAULT, CellConstraints.FILL));

        return topPanel;
    }
}