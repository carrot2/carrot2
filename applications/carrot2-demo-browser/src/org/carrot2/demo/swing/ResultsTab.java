
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.apache.log4j.Logger;
import org.apache.lucene.store.Directory;
import org.carrot2.core.LocalInputComponent;
import org.carrot2.core.ProcessingResult;
import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.impl.ArrayOutputComponent;
import org.carrot2.core.impl.RawDocumentEnumerator;
import org.carrot2.core.impl.ArrayOutputComponent.Result;
import org.carrot2.demo.*;
import org.carrot2.demo.cache.RawDocumentProducerCacheWrapper;
import org.carrot2.demo.index.RawDocumentsLuceneIndexSearcher;
import org.carrot2.demo.swing.util.SwingTask;
import org.carrot2.demo.swing.util.ToolbarButton;
import org.carrot2.demo.visualization.ClusterMapVisualization;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

/**
 * A component that displays results of a clustering process.
 *  
 * @author Dawid Weiss
 */
public class ResultsTab extends JPanel {
    private final static String MAIN_CARD = "main";
    private final static String PROGRESS_CARD = "progress";
    
    private final static Logger logger = Logger.getLogger(ResultsTab.class);

    /**
     * If <code>true</code>, a warning about swing browser has been displayed
     * and should not be displayed again.
     */
    private static boolean warningShown = false;

    private String query;
    private String processId;
    
    private Frame owner;
    
    private HtmlDisplay browserView;
    private RawClustersTree clustersTree;
    private Result result;
    private JPanel cards;
    private JLabel progressInfo;
    private DemoContext demoContext;
    private ProcessSettings processSettings;
    private WorkerThread workerThread;
    private SimpleInternalFrame internalFrame;
    private String defaultTitle;
    
    private JTextField clusterSearchField;
    private Directory indexDirectory;
    private List lastResultClusters;
    
    private BenchmarkDialog benchmarkDialog;
    private QuerySaveDialog querySaveDialog;

    private int requestedResults;
    
    private JLabel matchedDocumentsLabel;
    
    private class SelfRemoveAction implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            cleanup();
        }
    }
    
    private class WorkerThread extends Thread {
        private boolean done = false;
        private Map requestParams;

        public void run() {
            while (!done) {
                final Map rqParamsCopy;
                synchronized (this) {
                    rqParamsCopy = requestParams;
                    requestParams = null;
                }
                if (rqParamsCopy != null) {
                    try {
                        changeTitle("Processing...", true);
                        rqParamsCopy.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, Integer.toString(requestedResults));
                        ProcessingResult result = demoContext.getController().query(processId, query, rqParamsCopy);

                        Object queryResult = result.getQueryResult();
                        if (queryResult instanceof ArrayOutputComponent.Result) {
                            ArrayOutputComponent.Result output =
                                (ArrayOutputComponent.Result) result.getQueryResult();

                            // Ok, we have the clusters. We also have the documents.
                            // Show them.
                            showResults(output);
                        } else {
                            // don't know what to do with the result... Just inform about it.
                            showInfo("<html><body><h1>Query processed</h1><p>The result object is not interpretable, however.</p></body></html>");
                        }
                    } catch (Throwable e) {
                        showInfo("<html><body><h1>Exception executing query.</h1></body></html>");
                        SwingUtils.showExceptionDialog(ResultsTab.this, "Exception executing query.", e);
                    } finally {
                        changeTitle(defaultTitle, false);                        
                    }
                }

                synchronized (this) {
                    if (requestParams == null) {
                        // Nothing to do. Go to sleep.
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            // If interrupted, return.
                            return;
                        }
                    }
                }
            }
            Logger.getLogger(ResultsTab.class).debug("Tab thread ended.");
        }

        public void dispose() {
            synchronized (this) {
                done = true;
                this.notify();
            }
        }

        public void runQuery(Map requestParams) {
            synchronized (this) {
                // Copy request params to local.
                this.requestParams = requestParams;
                this.notify();
            }
        }
    }

    public ResultsTab(Frame owner, String query, DemoContext demoContext,
        ProcessSettings settings, String processId, int requestedResults)
    {
        this.owner = owner;
        this.query = query;
        this.processId = processId;
        this.demoContext = demoContext;
        this.processSettings = settings;
        this.requestedResults = requestedResults;
        this.benchmarkDialog = new BenchmarkDialog(owner, demoContext, query,
            processId, settings, requestedResults);
        this.querySaveDialog = new QuerySaveDialog(owner, demoContext, query,
            processId, settings, requestedResults);

        this.workerThread = new WorkerThread();
        workerThread.start();

        this.defaultTitle = "["
            + demoContext.getProcessIdToProcessNameMap().get(processId) + "] "
            + ("".equals(query) ? "<empty>" : query);

        buildSplit();
    }

    private void buildSplit() {
        this.setLayout(new BorderLayout());

        this.internalFrame = new SimpleInternalFrame(defaultTitle);
        JToolBar toolbar = new JToolBar();
        ToolbarButton closeButton = new ToolbarButton(
                new ImageIcon(this.getClass().getResource("remove.gif")),
                new ImageIcon(this.getClass().getResource("remove_co.gif")));
        closeButton.addActionListener(new SelfRemoveAction());
        closeButton.setToolTipText("Close this tab");
        ToolbarButton homeButton = new ToolbarButton(
                new ImageIcon(this.getClass().getResource("home_nav.gif")),
                new ImageIcon(this.getClass().getResource("home_nav_dis.gif")));
        homeButton.setToolTipText("Display all documents (from all clusters)");
        homeButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        showAllDocuments();
                    }
                });
        ToolbarButton benchmarkButton = new ToolbarButton(
            new ImageIcon(this.getClass().getResource("benchmark.gif")),
            new ImageIcon(this.getClass().getResource("benchmark_dis.gif")));
        benchmarkButton.setToolTipText("Benchmark this algorithm with current settings");
        benchmarkButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    benchmarkDialog.show();
                }
            });
        ToolbarButton saveButton = new ToolbarButton(
            new ImageIcon(this.getClass().getResource("save.gif")),
            new ImageIcon(this.getClass().getResource("save_dis.gif")));
        saveButton.setToolTipText("Save the documents of this query");
        saveButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    querySaveDialog.show();
                }
            });
        ToolbarButton mapButton = new ToolbarButton(
            new ImageIcon(this.getClass().getResource("map.gif")),
            new ImageIcon(this.getClass().getResource("map_dis.gif")));
        mapButton.setToolTipText("Explore cluster map");
        mapButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent arg0)
            {
                ClusterMapVisualization.showMapFrame(result, owner
                    .getLocation(), owner.getWidth(), owner.getHeight());
            }
            });
        toolbar.add(mapButton);
        toolbar.add(benchmarkButton);
        toolbar.add(saveButton);
        toolbar.add(homeButton);
        toolbar.add(closeButton);
        internalFrame.setToolBar(toolbar);

        final JScrollPane clustersTreeScroller = new JScrollPane();
        this.clustersTree = new RawClustersTree(demoContext.getClusterInfoRenderer(processId));
        clustersTreeScroller.getViewport().add(clustersTree);
        clustersTreeScroller.setBorder(BorderFactory.createEmptyBorder());
        clustersTree.addTreeSelectionListener(
                new RawClustersTreeSelectionListener(this));

        final JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(clustersTreeScroller, BorderLayout.CENTER);
        
        final JPanel clusterSearchPanel = new JPanel();
        clusterSearchPanel.setLayout(new BorderLayout());
        clusterSearchField = new JTextField();
        clusterSearchPanel.add(clusterSearchField, BorderLayout.CENTER);
        JButton clusterSearchButton = new JButton("Search");
        clusterSearchPanel.add(clusterSearchButton, BorderLayout.LINE_END);
        matchedDocumentsLabel = new JLabel(" ");
        clusterSearchPanel.add(matchedDocumentsLabel, BorderLayout.PAGE_END);
        leftPanel.add(clusterSearchPanel, BorderLayout.PAGE_END);
        
        clusterSearchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addClusterHighlightingInfo();
                ResultsTab.this.clustersTree.setModel(new RawClustersTreeModel(
                    lastResultClusters));
            } 
        });
        clusterSearchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addClusterHighlightingInfo();
                ResultsTab.this.clustersTree.setModel(new RawClustersTreeModel(
                    lastResultClusters));
            } 
        });

        this.browserView = HtmlDisplay.newHtmlDisplay();

        // create 'progress' card.
        JPanel progressPane = new JPanel();
        FormLayout fm = new FormLayout(
                "pref:grow,center:min(pref;200px),pref:grow",
                "40px,default,4px,8px");
        progressPane.setLayout(fm);
        progressInfo = new JLabel("");
        CellConstraints cc = new CellConstraints();
        progressPane.add(progressInfo, cc.xy(2,2));
        JProgressBar pindic = new JProgressBar();
        pindic.setIndeterminate(true);
        progressPane.add(pindic, cc.xy(2,4));

        final JPanel subPanel = new JPanel(new BorderLayout()) {
            public Dimension getMinimumSize() {
                return new Dimension(0,0);
            }

            public Dimension getPreferredSize() {
                return super.getMinimumSize();
            }
        };
        subPanel.add(browserView, BorderLayout.CENTER);
        
        if (this.processSettings.hasSettings()) {
            final JComponent settingsComponent = processSettings.getSettingsComponent(owner);

            final JButton updateButton = new JButton("Refresh");
            updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    performQuery();
                }
            });

            final JCheckBox liveUpdate = new JCheckBox("Live update");
            liveUpdate.setSelected(processSettings.isLiveUpdate());
            liveUpdate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    processSettings.setLiveUpdate(liveUpdate.isSelected());
                }
            });

            this.processSettings.addListener(new ProcessSettingsListener() {
                public void settingsChanged(ProcessSettings settings) {
                    performQuery();
                }
            });

            final JPanel liveUpdatePanel = new JPanel();
            liveUpdatePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            liveUpdatePanel.add(liveUpdate);
            liveUpdatePanel.add(updateButton);

            final CellConstraints cc2 = new CellConstraints();
            final FormLayout settingsContainerLayout = new FormLayout(
                    "fill:pref",
                    "pref, top:default:grow, 8px, pref, fill:pref");
            final PanelBuilder panelBuilder = new PanelBuilder(settingsContainerLayout);
            panelBuilder.setBorder(BorderFactory.createEmptyBorder(2,4,2,4));

            final JPanel settingsWrapper = new JPanel(new BorderLayout());
            settingsWrapper.add(settingsComponent, BorderLayout.CENTER);
            settingsWrapper.setBorder(BorderFactory.createEmptyBorder(4,4,0,4));
            
            final JScrollPane scroller = new JScrollPane(settingsWrapper,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scroller.setBorder(BorderFactory.createEmptyBorder());
            panelBuilder.addSeparator("Process settings", cc2.xy(1, 1));
            panelBuilder.add(scroller, cc2.xy(1, 2));
            panelBuilder.addSeparator("Update settings", cc2.xy(1, 4));
            panelBuilder.add(liveUpdatePanel, cc2.xy(1, 5, CellConstraints.RIGHT, CellConstraints.BOTTOM));

            subPanel.add(panelBuilder.getPanel(), BorderLayout.EAST);
        }

        // Hack: We need to hard-code the min-width to avoid JDIC overlap problem...
        leftPanel.setMinimumSize(new Dimension(300, leftPanel.getMinimumSize().height));
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel, subPanel);
        splitPane.setDividerLocation(300);
        
        this.cards = new JPanel(new CardLayout());
        cards.add(splitPane, MAIN_CARD);
        cards.add(progressPane, PROGRESS_CARD);
        internalFrame.add(cards);
        ((CardLayout)cards.getLayout()).show(cards, PROGRESS_CARD);

        this.add(internalFrame, BorderLayout.CENTER);        
    }
    
    /**
     * Updates the HTML view component with the provided HTML. MUST BE INVOKED
     * FROM AN AWT THREAD.
     */
    public void updateDocumentsView(String htmlContent) {
        if (!SwingUtilities.isEventDispatchThread())
            throw new IllegalStateException("Invoke updateDocumentsView() from AWT thread only.");

        this.browserView.setContent(htmlContent);
    }

    /**
     * Leaves only HTML display and sets it to the provided HTML content.
     */
    public void showInfo(final String htmlContent) {
        Runnable task = new Runnable() {
            public void run() {
                updateDocumentsView(htmlContent);
                ((CardLayout)cards.getLayout()).show(cards, MAIN_CARD);
            }
        };
        SwingTask.runNow(task);
    }

    /**
     * Displays the result of clustering -- clusters and 
     * documents.
     */
    private void showResults(final ArrayOutputComponent.Result output) {
        if (indexDirectory == null)
        {
            indexDirectory = (Directory) output.context
                .get(RawDocumentProducerCacheWrapper.PARAM_INDEX_CONTENT);
        }
        this.lastResultClusters = output.clusters;
        addClusterHighlightingInfo();
        
        Runnable task = new Runnable() {
            public void run() {
                result = output;
                // ok, display the clusters tree first. it is a new clusters
                // model on top of the results
                ResultsTab.this.clustersTree.setModel(new RawClustersTreeModel(output.clusters));
                showAllDocuments();
                ((CardLayout)cards.getLayout()).show(cards, MAIN_CARD);
                
                if (browserView instanceof HtmlDisplayWithSwing) {
                    if (warningShown  == false) {
                        warningShown = true;
                        JOptionPane.showMessageDialog(ResultsTab.this, 
                                "This application is for tuning/ demonstration only.\n\n"
                                + "The browser's component navigational capabilities and rendering quality\n"
                                + "are very limited.", "Information", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        };
        SwingTask.runNow(task);
    }

    private void addClusterHighlightingInfo()
    {
        String clusterSearchQuery = clusterSearchField.getText();
        if (indexDirectory == null || clusterSearchQuery == null)
        {
            return;
        }

        if (clusterSearchQuery.trim().length() == 0)
        {
            // Just clear the results
            clearClusterSearchInfo();
            displayMatchedDocumentsCount(-1);
            return;
        }
        
        try
        {
            String [] documentIds = RawDocumentsLuceneIndexSearcher.search(
                indexDirectory, clusterSearchQuery);
            
            displayMatchedDocumentsCount(documentIds.length);
            updateClusterSearchInfo(new HashSet(Arrays.asList(documentIds)));
        }
        catch (IOException e)
        {
            logger.warn("Error while searching Lucene index", e);
            return;
        }
    }

    private void displayMatchedDocumentsCount(final int count)
    {
        Runnable task = new Runnable() {
            public void run() {
                if (count >= 0)
                {
                    matchedDocumentsLabel.setText("Matched documents: " + count);
                }
                else
                {
                    matchedDocumentsLabel.setText(" ");
                }
            }
        };
        SwingTask.runNow(task);
    }
    
    private void updateClusterSearchInfo(Set clusterSearchResult)
    {
        if (lastResultClusters == null)
        {
            return;
        }
        
        clearClusterSearchInfo();
        
        for (Iterator iter = lastResultClusters.iterator(); iter.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) iter.next();
            updateClusterSearchInfo(clusterSearchResult, rawCluster);
        }
    }

    private void clearClusterSearchInfo()
    {
        for (Iterator iter = lastResultClusters.iterator(); iter.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) iter.next();
            clearClusterSearchInfo(rawCluster);
        }
    }

    private int updateClusterSearchInfo(Set clusterSearchResult, RawCluster rawCluster)
    {
        // Depth-first tree search here
        List subclusters = rawCluster.getSubclusters();
        if (subclusters == null || subclusters.size() == 0)
        {
            List rawDocuments = rawCluster.getDocuments();
            int searchMatches = 0;
            for (Iterator iter = rawDocuments.iterator(); iter.hasNext();)
            {
                RawDocument rawDocument = (RawDocument) iter.next();
                if (clusterSearchResult.contains(rawDocument.getId().toString()))
                {
                    searchMatches++;
                }
            }

            if (searchMatches > 0)
            {
                rawCluster.setProperty(RawClustersCellRenderer.PROPERTY_SEARCH_MATCHES, new Integer(searchMatches));
            }
            
            return searchMatches;
        }
        else
        {
            int searchMatches = 0;
            for (Iterator iter = subclusters.iterator(); iter.hasNext();)
            {
                RawCluster subcluster = (RawCluster) iter.next();
                searchMatches += updateClusterSearchInfo(
                    clusterSearchResult, subcluster);
            }
            
            if (searchMatches > 0)
            {
                rawCluster.setProperty(RawClustersCellRenderer.PROPERTY_SEARCH_MATCHES, new Integer(searchMatches));
            }
            
            return searchMatches;
        }
    }

    private void clearClusterSearchInfo(RawCluster rawCluster)
    {
        rawCluster.setProperty(RawClustersCellRenderer.PROPERTY_SEARCH_MATCHES, null);
        List subclusters = rawCluster.getSubclusters();
        if (subclusters != null)
        {
            for (Iterator iter = subclusters.iterator(); iter.hasNext();)
            {
                RawCluster subcluster = (RawCluster) iter.next();
                clearClusterSearchInfo(subcluster);
            }
        }
    }
    
    /**
     * Displays all documents in the result, regardless of the cluster they are
     * in.
     */
    private void showAllDocuments() {
        if (result == null) return;

        final StringBuffer html = new StringBuffer(50000);

        this.browserView.appendHtmlHeader(html);

        if (result.documents != null && result.documents.size() > 0) {
            appendHtmlForDocuments(html, result.documents, false);
        } else {
            // Build document list from clusters.
            appendHtmlForDocuments(html, collectDocuments(result.clusters), true);
        }
        this.browserView.appendHtmlTrailer(html);

        Runnable task = new Runnable() {
            public void run() {
                ResultsTab.this.clustersTree.clearSelection();
                updateDocumentsView(html.toString());
                ((CardLayout)cards.getLayout()).show(cards, MAIN_CARD);
            }
        };
        SwingTask.runNow(task);
    }

    /**
     * A comparator for {@link RawDocument}s which compares their addition sequence number.
     */
    private final static Comparator DOCUMENT_SEQ_COMPARATOR = new Comparator() {
        public int compare(final Object o1, final Object o2) {
            final RawDocument rc1 = (RawDocument) o1;
            final RawDocument rc2 = (RawDocument) o2;

            final int rci1 = ((Integer) rc1.getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER)).intValue();
            final int rci2 = ((Integer) rc2.getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER)).intValue();

            return rci1 - rci2;
        }
    };

    public StringBuffer appendHtmlForDocuments(StringBuffer buffer, Collection documents, boolean resortDocuments) {
        if (resortDocuments) {
            // Re-sort documents according to their addition sequence.
            final ArrayList docsCopy = new ArrayList(documents);
            Collections.sort(docsCopy, DOCUMENT_SEQ_COMPARATOR);
            documents = docsCopy;
        }

        for (Iterator i = documents.iterator(); i.hasNext();) {
            final RawDocument doc = (RawDocument) i.next();

            final Object seqNum = doc.getProperty(RawDocumentEnumerator.DOCUMENT_SEQ_NUMBER);
            
            browserView.appendHtmlFor(buffer, seqNum, doc);
        }

        return buffer;
    }

    /**
     * This method converts a set of raw documents to a string.
     */
    public String getHtmlFor(final RawCluster rc) {
        final StringBuffer buffer = new StringBuffer();

        browserView.appendHtmlHeader(buffer);

        final String clusterLabel = RawClustersCellRenderer.getLabel(rc, Integer.MAX_VALUE);

        if (clusterLabel != null) {
            buffer.append("<div style=\"color: white; font-weight: bold; background-color: #5588BE;" +
                    " border-bottom: 1px solid #303030; padding-bottom: 4px; padding-top: 4px;\">");
            buffer.append(clusterLabel);
            buffer.append("</div>");
            buffer.append("<br/>");
        }

        final ArrayList clusters = new ArrayList();
        clusters.add(rc);
        final HashSet documents = collectDocuments(clusters);

        appendHtmlForDocuments(buffer, documents, rc.getSubclusters().size() > 0);

        browserView.appendHtmlTrailer(buffer);

        return buffer.toString();
    }

    private HashSet collectDocuments(final Collection clusterCollection) {
        final HashSet documents = new HashSet();
        final ArrayList clusters = new ArrayList(clusterCollection);
        while (!clusters.isEmpty()) {
            final RawCluster subCluster = (RawCluster) clusters.remove(clusters.size()-1);

            final List clusterDocuments = subCluster.getDocuments();
            if (clusterDocuments != null) {
                documents.addAll(clusterDocuments);
            }

            final List subclusters = subCluster.getSubclusters();
            if (subclusters != null) {
                clusters.addAll(subclusters);
            }
        }

        return documents;
    }
    
    public void changeTitle(final String title, final boolean alert) {
        Runnable task = new Runnable() {
            public void run() {
                internalFrame.setTitle(title);
                if (alert) {
                    internalFrame.setHeaderBackground(Color.RED);
                } else {
                    internalFrame.setHeaderBackground(
                            internalFrame.getDefaultHeaderBackground());
                }
            }
        };
        SwingTask.runNow(task);
    }

    public void showProgress(final String string) {
        Runnable task = new Runnable() {
            public void run() {
                ((CardLayout)cards.getLayout()).show(cards, PROGRESS_CARD);
                progressInfo.setText(string);
            }
        };
        SwingTask.runNow(task);
    }

    public void cleanup() {
        synchronized (this) {
            // find the tabbedpane we belong to.
            Component last = this;
            while (last != null)
            {
                if (last.getParent() instanceof JTabbedPane)
                {
                    ((JTabbedPane) last.getParent()).remove(last);
                    break;
                }
                last = last.getParent();
            }
            
            // Cleanup resources
            if (workerThread != null) {
                workerThread.dispose();
                workerThread = null;
            }
            if (browserView != null) {
                // TODO: Dispose doesn't seem to work properly.
                // this.browserView.dispose();
                this.browserView = null;
            }
            
            if (processSettings != null) {
                processSettings.dispose();
                processSettings = null;
            }
        }
    }
    
    protected void finalize() throws Throwable {
        cleanup();
    }

    public void performQuery() {
        HashMap requestParams = processSettings.getRequestParams();
        workerThread.runQuery(requestParams);
    }
}
