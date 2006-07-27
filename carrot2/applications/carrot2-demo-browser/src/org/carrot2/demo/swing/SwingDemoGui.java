
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

package org.carrot2.demo.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.carrot2.demo.*;
import org.carrot2.demo.swing.util.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.plaf.Options;

/**
 * Swing-based GUI for the Carrot2 demo.
 * 
 * @author Dawid Weiss
 */
public class SwingDemoGui implements DemoGuiDelegate {

    /** Search banner color. */
    private static final Color BANNER_COLOR = new Color(0xe0, 0xe0, 0xe0);
    private SwingDemoGuiConfig config;

    /** Main demo object (application context). */
    private final DemoContext demoContext;
    
    /** Main demo frame */
    private final JFrame frame;

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
    public SwingDemoGui(DemoContext carrotDemo) {
        this.demoContext = carrotDemo;
        this.frame = new JFrame();
        this.config = new SwingDemoGuiConfig("/config/browser-config.xml");
    }

    /**
     * Displays the demo frame.
     */
    public void display() {
        try {
            Options.setUseNarrowButtons(true);
            UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
        } catch (Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
        }

        frame.setTitle("Carrot2 Demo");
        frame.setIconImage(new ImageIcon(this.getClass().getResource("carrot2-icon.png")).getImage());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(buildMainPanel());
        frame.pack();

        // Prevent from resizing below preferred size.
        frame.addComponentListener(new KeepMimumumFrameSizeListener(frame.getPreferredSize()));

        disableUI();
        queryField.setText("Please wait...");
        
        SwingUtils.centerFrameOnScreen(frame);
        frame.setVisible(true);

        // replace the combo box's model.
        final Runnable task = new Runnable() {
            public void run() {
                // Now initialize the application.
                demoContext.initialize();

                processComboModel = new MapComboModel(demoContext.getProcessIdToProcessNameMap());
                processComboBox.setModel(processComboModel);
                if (demoContext.getDefaultProcessId() != null) {
                    processComboBox.setSelectedItem(
                            demoContext.getProcessIdToProcessNameMap().get(
                                    demoContext.getDefaultProcessId()));
                }
                queryField.setText("");
                queryField.requestFocus();

                enableUI();
            }
        };
        try {
            SwingTask.runNow(task);
        } catch (Throwable t) {
            SwingUtils.showExceptionDialog(frame, "Initialization exception.", t);
        }
    }

    /**
     * Performs a query on the given process identifier and displays a new tab with the
     * result.  
     */
    private void performQuery(String processId, String query, int requestedResults) {
        try {
            // ready to process the query, disable the user interface for the time it takes
            // to spawn the query processing thread.
            disableUI();

            final ProcessSettings settings = demoContext.getSettingsObject(processId);
            if (false == settings.isConfigured()) {
                // Return immediately -- the process is not configured properly.
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
            try {
                resultsTab.performQuery();
            } finally {
                enableUI();
                queryField.requestFocus();
            }
        } catch (Throwable t) {
            JOptionPane.showMessageDialog(queryField, "Exception executing query: \n"
                    + t.toString());
            enableUI();
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
        GridBagConstraints cc = new GridBagConstraints();

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

        cc = new GridBagConstraints(
                0, 1,
                1, 1,
                1, 1,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(0,0,0,0), 
                0, 0);
        layout.setConstraints(tabbedPane, cc);
        mainPanel.add(tabbedPane);
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
                this.getClass().getClassLoader().getResource("res/Carrot2-symbol-final.png"));
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

        JLabel queryLabel = new JLabel("Query:");
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
        topPanel.add(detailsPanel, cc.xywh(3,3,1,1, CellConstraints.RIGHT, CellConstraints.DEFAULT));

        this.processSettingsButton = new JButton("Settings");
        processSettingsButton.setToolTipText("Displays process settings window. Disabled if no settings.");
        this.processSettingsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String processId = (String) processComboModel.getSelectedKey();
                final ProcessSettings settings = demoContext.getSettingsObject(processId);

                JOptionPane.showMessageDialog(frame, 
                        new Object[] {settings.getSettingsComponent(frame)}, "Default settings", 
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        detailsPanel.add(processSettingsButton, cc.xy(5,1, CellConstraints.DEFAULT, CellConstraints.FILL));

        detailsPanel.add(new JLabel("Process:"), cc.xy(1,1));
        this.processComboBox = new JComboBox();
        this.processComboBox.setToolTipText("A chain of components used to process the query.");
        this.processComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String processId = (String) processComboModel.getSelectedKey();
                processSettingsButton.setVisible(demoContext.getSettingsObject(processId).hasSettings());
            }
        });
        detailsPanel.add(processComboBox, cc.xy(3,1, CellConstraints.DEFAULT, CellConstraints.FILL));

        detailsPanel.add(new JLabel("Results:"), cc.xy(7,1));
        this.sizeComboBox = new JComboBox(config.requestedResultCounts);
        this.sizeComboBox.setToolTipText("Number of results to acquire from the input source.");
        this.sizeComboBox.setSelectedItem(config.selectedResultCount);
        detailsPanel.add(sizeComboBox, cc.xy(8,1, CellConstraints.DEFAULT, CellConstraints.FILL));

        return topPanel;
    }
}