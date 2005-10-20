package com.dawidweiss.carrot2.browser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.dawidweiss.carrot.core.local.DuplicatedKeyException;
import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalComponentFactory;
import com.dawidweiss.carrot.core.local.LocalComponentFactoryBase;
import com.dawidweiss.carrot.core.local.LocalController;
import com.dawidweiss.carrot.core.local.LocalControllerBase;
import com.dawidweiss.carrot.core.local.MissingProcessException;
import com.dawidweiss.carrot.core.local.ProcessingResult;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent;
import com.dawidweiss.carrot.local.controller.ControllerHelper;
import com.dawidweiss.carrot.local.controller.loaders.ComponentInitializationException;
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
    
    private static final Color BANNER_COLOR = new Color(0xe0, 0xe0, 0xf0);

    /* UI elements */
    private JTabbedPane tabbedPane;
    private JTextField  queryField;
    private JComboBox   processComboBox;
    private JComboBox   sizeComboBox;

    /**
     * Default query sizes.
     */
    private String [] defaultSizes = new String [] {
            "50", "100", "150", "200", "400"
    };
    
    /** Local Carrot2 controller */
    private LocalController controller;

    /** Combo box model with currently selected process ids. */
    private ProcessComboModel processComboModel;

    
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

    private final class ProcessComboModel extends DefaultComboBoxModel {
        private final List processIds;
        private final List processNames;
        int selected = 0;

        public ProcessComboModel(List processIds, List processNames) {
            this.processIds = processIds;
            this.processNames = processNames;
        }

        public void setSelectedItem(Object anItem) {
            selected = processNames.indexOf(anItem);
        }

        public Object getSelectedItem() {
            return processNames.get(selected);
        }

        public int getSize() {
            return processIds.size();
        }

        public Object getElementAt(int index) {
            return processNames.get(index);
        }
        
        public String getSelectedProcessId() {
            return (String) processIds.get(selected);
        }
    }

    /**
     * A listener for new queries.
     */
    private class NewQueryListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // make sure all required data is available.
            String processId = processComboModel.getSelectedProcessId();
            String query = queryField.getText();
            if (processId == null) {
                JOptionPane.showMessageDialog(queryField, "Select a process first.");
            } else {
                try {
                    // ready to process the query
                    disableUI();
                    // display a tab with results.
                    ResultsTab tab = addTab(query, processId);
                    Thread processingThread = new ProcessingThread(tab, query, processId);
                    processingThread.start();
                } catch (Throwable t) {
                    JOptionPane.showMessageDialog(queryField, "Exception executing query: \n"
                            + t.toString());
                    enableUI();
                }
            }
        }
    }
    
    
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

        return mainPanel;
    }

    /**
     * Builds the top panel. 
     */
    private JComponent buildTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(BANNER_COLOR);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
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
        topPanel.setOpaque(false);
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
        detailsPanel.setOpaque(false);
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
            ControllerHelper cl = new ControllerHelper();

            final File componentsDir = new File("components");
            if (componentsDir.isDirectory() == false) {
                throw new RuntimeException("Components directory not found: "
                        + componentsDir.getAbsolutePath());
            }
            final File processesDir = new File("processes");
            if (processesDir.isDirectory() == false) {
                throw new RuntimeException("Components directory not found: "
                        + componentsDir.getAbsolutePath());
            }

            //
            // Add predefined components
            //
            LocalComponentFactory clusterConsumerOutputFactory = new LocalComponentFactoryBase() {
                public LocalComponent getInstance() {
                    return new ClustersConsumerOutputComponent();
                }
            };
            controller.addLocalComponentFactory("output-demo-tab", 
                clusterConsumerOutputFactory);

            //
            // Add scripted/ custom components and processes
            //
            try {
                cl.addComponentFactoriesFromDirectory(controller, componentsDir);
                cl.addProcessesFromDirectory(controller, processesDir);
            } catch (DuplicatedKeyException e) {
                throw new RuntimeException("Identifiers of components and processes must be unique.", e);
            } catch (ComponentInitializationException e) {
                throw new RuntimeException("Cannot initialize component.", e);
            } catch (Exception e) {
                throw new RuntimeException("Unhandled exception when initializing components and processes.", e);
            }

            final List processIds = controller.getProcessIds();
            final List processNames = new ArrayList(processIds.size());
            for (Iterator i = processIds.iterator(); i.hasNext();) {
                try {
                    processNames.add(controller.getProcessName((String) i.next()));
                } catch (MissingProcessException e) {
                    throw new Error("Process identifier not associated with any name?", e);
                }
            }
            this.processComboModel = new ProcessComboModel(processIds, processNames);

            // replace the combo box's model.
            final Runnable task = new Runnable() {
                public void run() {
                    processComboBox.setModel(
                            processComboModel);
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
    }
    
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
        JComponent panel = browser.buildMainPanel();
        frame.getContentPane().add(panel);
        
        // Create processes and add them to the user interface.
        browser.createLocalController();
        
        frame.pack();
        frame.setVisible(true);        
    }
}
