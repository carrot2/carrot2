package com.dawidweiss.carrot2.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.dawidweiss.carrot.core.local.LocalController;
import com.dawidweiss.carrot.core.local.LocalControllerBase;
import com.dawidweiss.carrot.core.local.MissingProcessException;
import com.dawidweiss.carrot.core.local.ProcessingResult;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.plaf.Options;

/**
 * A demonstration "browser" that uses a few local components
 * and imitates a browser facility.
 * 
 * @author Dawid Weiss
 */
public class Browser {
    
    /* UI elements */
    private JTabbedPane tabbedPane;
    private JTextField  queryField;
    private JComboBox   processComboBox;
    private JComboBox   sizeComboBox;
    private JFrame 		frame;

    /**
     * Default query sizes.
     */
    private String [] defaultSizes = new String [] {
            "50", "100", "150", "200", "400"
    };
    
    /** Local Carrot2 controller */
    private LocalController controller;
    
    
    /**
     * A query processing thread. 
     */
    private class ProcessingThread extends Thread {
        
        private ResultsTab tab;
        private String query;
        private String processId;

        public ProcessingThread(ResultsTab tab, String query, String processId) {
            this.tab = tab;
            this.query = query;
            this.processId = processId;

            // just in case it hangs it will not block JVM from exiting.
            setDaemon(true);            
        }

        public void run() {
            try {
                tab.showProgress("Executing process...");
                HashMap requestParams = new HashMap();
                ProcessingResult result = controller.query(processId, query, requestParams);
                
                // get at the clustered result?
                Object queryResult = result.getQueryResult();
                tab.showProgress("Rendering results...");
                if (queryResult instanceof ClustersConsumerOutputComponent.Result) {
	    			ClustersConsumerOutputComponent.Result output =
	    				(ClustersConsumerOutputComponent.Result) result.getQueryResult();

	    			// Ok, we have the clusters. We also have the documents.
	    			// Show them.
	    			tab.showResults(output);
                } else {
                    // don't know what to do with the result... Just inform about it.
                    tab.showInfo("<html><body><h1>Query processed</h1><p>The result object is not interpretable, however.</p></body></html>");
                }
            } catch (MissingProcessException e) {
                tab.showError(e);
            } catch (Exception e) {
                tab.showError(e);
            } finally {
                enableUI();
            }
        }
    }
    
    /**
     * A listener for new queries.
     */
    private class NewQueryListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // make sure all required data is available.
            int results = Integer.parseInt((String) sizeComboBox.getSelectedItem());
            String processId = (String) processComboBox.getSelectedItem();
            String query = queryField.getText();
            if (processId == null) {
                JOptionPane.showMessageDialog(queryField, "Select a process first.");
            } else {
                // ready to process the query
                disableUI();
                // display a tab with results.
                ResultsTab tab = addTab(query, processId);

                Thread processingThread = new ProcessingThread(tab, query, processId);
                processingThread.start();
            }
        }
    };
    
    
    private ResultsTab addTab(String query, String processId) {
        String tabName = query;
        if ("".equals(query)) {
            tabName = "<empty query>";
        }
        if (tabName.length() > 20) {
            tabName = tabName.substring(0,20) + "...";
        }
        ResultsTab tb = new ResultsTab(query, processId);
        tabbedPane.addTab(tabName, tb);
        tabbedPane.setToolTipTextAt(tabbedPane.getTabCount()-1, query);
        return tb;
    }

    /**
     * Builds the main split between top panel and tabbed pane
     * with results.
     */
    private JComponent buildMainPanel(JFrame frame) {
        this.frame = frame;
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setPreferredSize(new Dimension(300,300));
        tabbedPane.putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE);
        tabbedPane.putClientProperty(Options.NO_CONTENT_BORDER_KEY, Boolean.TRUE);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());

        JComponent topPanel = buildTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        return mainPanel;
    }

    /**
     * Builds the top panel. 
     */
    private JComponent buildTopPanel() {
        JPanel topPanel = new JPanel();
        FormLayout layout = new FormLayout(
                // columns
                "4px, fill:max(pref;550px):grow, 8px, center:pref, 4px",
                // rows
                "4px, pref, 4px");
        topPanel.setLayout( layout );
        
        CellConstraints cc = new CellConstraints();

        JComponent queryForm = buildQueryForm();
        topPanel.add(queryForm, cc.xywh(2,2,1,1));

        ImageIcon carrotLogo = new ImageIcon(this.getClass().getResource("/res/Carrot2-symbol-final.png"));
        JLabel label = new JLabel(carrotLogo);
        topPanel.add(label, cc.xywh(4,2,1,1));

        return topPanel;
    }

    /**
     * Builds query form in the top panel.
     */
    private JComponent buildQueryForm() {
        JPanel topPanel = new JPanel();
        FormLayout layout = new FormLayout(
                // columns
                "right:pref,4px,fill:min:grow",
                // rows
                "pref, 2px, pref");
        topPanel.setLayout( layout );
        
        CellConstraints cc = new CellConstraints();

        JLabel queryLabel = new JLabel("Query:");
        topPanel.add(queryLabel, cc.xywh(1,1,1,1));
        
        queryField = new JTextField();
        queryField.setToolTipText("The query to be sent to the input component (syntax depends on the input).");
        queryField.addActionListener(new NewQueryListener());
        topPanel.add(queryField, cc.xywh(3,1,1,1));
        
        // build details panel.
        JPanel detailsPanel = new JPanel();
        layout = new FormLayout(
                "right:pref,4px,fill:max(200;pref),16px,right:default,max(pref;60px)",
                "pref");
        detailsPanel.setLayout(layout);
        topPanel.add(detailsPanel, cc.xywh(3,3,1,1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
        
        detailsPanel.add(new JLabel("Process:"), cc.xy(1,1));
        this.processComboBox = new JComboBox();
        this.processComboBox.setToolTipText("A chain of components used to process the query.");
        detailsPanel.add(processComboBox, cc.xy(3,1));
        detailsPanel.add(new JLabel("Results:"), cc.xy(5,1));
        this.sizeComboBox = new JComboBox(defaultSizes);
        this.sizeComboBox.setToolTipText("Number of results to acquire from the input source.");
        this.sizeComboBox.setSelectedItem("100");
        detailsPanel.add(sizeComboBox, cc.xy(6,1));

        return topPanel;
    }
    
    
    /**
     * Creates local controller and component factories.
     */
    private void createLocalController() {
        try {
            disableUI();
            
            // create local controller.
            this.controller = new LocalControllerBase();

            ProcessesAndComponents.addComponentFactories(controller);
            ProcessesAndComponents.addProcesses(controller);
            
            final Vector processes = new Vector(controller.getProcessIds());
            // replace the combo box's model.
            Runnable task = new Runnable() {
                public void run() {
                    processComboBox.setModel(
                            new DefaultComboBoxModel(processes));
                }
            };
            runAwtTask(task);
        } finally {
            enableUI();
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
            synchronized (Browser.this) {
                queryField.setEnabled(onOff);
                processComboBox.setEnabled(onOff);
                sizeComboBox.setEnabled(onOff);
            }
        }
    };
    
    
    /**
     * Disables interactive user interface elements.
     */
    public void disableUI() {
        runAwtTask(new UISwitchTask(false));
    }

    /**
     * Enable UI.
     */
    public void enableUI() {
        runAwtTask(new UISwitchTask(true));
    }
    
    /**
     * Invokes a task from an AWT thread.
     */
    public static void runAwtTask(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) {
            // safe to invoke it directly.
            task.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(task);
            } catch (InterruptedException e) {
                // just post to the queue then.
                SwingUtilities.invokeLater(task);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            }
        }        
    }

    /**
     * Entry point.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
        } catch (Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
        }
        Browser browser = new Browser();
        
        JFrame frame = new JFrame();
        frame.setTitle("Carrot2 Browser");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JComponent panel = browser.buildMainPanel(frame);
        frame.getContentPane().add(panel);
        frame.setJMenuBar(browser.buildMenuBar());
        
        // Create processes and add them to the user interface.
        browser.createLocalController();
        
        frame.pack();
        frame.setVisible(true);        
    }

    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();
        mb.setBorderPainted(false);

        JMenu mainMenu = new JMenu("Application");

        JMenuItem aboutItem = new JMenuItem("About...");
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent av) {
                JOptionPane.showMessageDialog(frame, new AboutPanel());
            }});
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent av) {
                frame.dispose();
            }});
        
        mainMenu.add(aboutItem);
        mainMenu.add(exitItem);
        
        mb.add(mainMenu);        
        return mb;
    }
}
