
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.apache.log4j.Logger;
import org.apache.lucene.store.*;
import org.jdesktop.jdic.browser.WebBrowser;

import carrot2.demo.DemoContext;
import carrot2.demo.ProcessSettings;
import carrot2.demo.ProcessSettingsListener;
import carrot2.demo.cache.*;
import carrot2.demo.index.*;
import carrot2.demo.swing.util.SwingTask;
import carrot2.demo.swing.util.ToolbarButton;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.RawCluster;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent;
import com.dawidweiss.carrot.core.local.impl.RawDocumentEnumerator;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent.Result;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

/**
 * A component that displays results of a clustering process.
 *  
 * @author Dawid Weiss
 */
public class ResultsTab extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final static String MAIN_CARD = "main";
    private final static String PROGRESS_CARD = "progress";

    private String query;
    private String processId;
    
    private WebBrowser browserView;
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

    private int requestedResults;
    
    /** */
    private final static Logger logger = Logger
        .getLogger(ResultsTab.class);

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
                        if (queryResult instanceof ClustersConsumerOutputComponent.Result) {
                            ClustersConsumerOutputComponent.Result output =
                                (ClustersConsumerOutputComponent.Result) result.getQueryResult();

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
        this.query = query;
        this.processId = processId;
        this.demoContext = demoContext;
        this.processSettings = settings;
        this.requestedResults = requestedResults;
        this.benchmarkDialog = new BenchmarkDialog(owner, demoContext, query,
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
        toolbar.add(benchmarkButton);
        toolbar.add(homeButton);
        toolbar.add(closeButton);
        internalFrame.setToolBar(toolbar);

        final JScrollPane clustersTreeScroller = new JScrollPane();
        this.clustersTree = new RawClustersTree();
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

        this.browserView = new WebBrowser();
        this.browserView.setFocusable(false);

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
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public Dimension getMinimumSize() {
                return new Dimension(0,0);
            }

            public Dimension getPreferredSize() {
                return super.getMinimumSize();
            }
        };
        subPanel.add(browserView, BorderLayout.CENTER);
        
        if (this.processSettings.hasSettings()) {
            final JPanel settingsContainer = new JPanel(new BorderLayout(0, 8));
            settingsContainer.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
            final JComponent settingsComponent = processSettings.getSettingsComponent();
            settingsContainer.add(settingsComponent, BorderLayout.CENTER);
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
            JPanel buttons = new JPanel();
            buttons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("controlShadow")));
            buttons.setLayout(new FlowLayout(FlowLayout.LEFT));
            buttons.add(liveUpdate);
            buttons.add(updateButton);
            settingsContainer.add(buttons, BorderLayout.SOUTH);

            final JPanel spacer = new JPanel(new BorderLayout());
            spacer.add(settingsContainer, BorderLayout.SOUTH);
            spacer.add(new JPanel(), BorderLayout.CENTER);
            subPanel.add(spacer, BorderLayout.EAST);
        }

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
     * Shows error information.
     */
    public void showError(final Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        final String stackTrace = sw.toString();

        showInfo("<html><body>"
                + "<h1>An exception occurred.</h1>"
                + "<h2>" + e.toString() + "</h2>"
                + "<br><br><pre>"
                + stackTrace
                + "</pre>"
                + "</body></html>");
    }

    /**
     * Updates the HTML view component with the provided HTML.
     * MUST BE INVOKED FROM AN AWT THREAD.
     */
    public void updateDocumentsView(String htmlContent) {
        if (!SwingUtilities.isEventDispatchThread())
            throw new IllegalStateException("Invoke updateDocumentsView() from AWT thread only.");

        try {
            // TODO: This can be refactored to use browser listener to detect
            // if the file has been loaded.
            // BUGFIX: We dump the HTML to an external file because setContent
            // method does not convert (or detect) characters properly.
            final File tempFile = File.createTempFile("c2tmphtml", "html");
            tempFile.deleteOnExit();
            final FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(htmlContent.getBytes("UTF-8"));
            fos.close();
            this.browserView.setURL(tempFile.toURL());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Cannot create temporary file for HTML.");
        }
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
    private void showResults(final ClustersConsumerOutputComponent.Result output) {
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
    
    /**
     * @param clusterSearchResult
     */
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

    /**
     * 
     */
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
    
    /**
     * @param rawCluster
     */
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
     * 
     */
    private void showAllDocuments() {
        if (result == null) return;
        
        final StringBuffer html = new StringBuffer(50000);

        appendHtmlHeader(html);
        
        if (result.documents != null && result.documents.size() > 0) {
            appendHtmlForDocuments(html, result.documents, false);
        } else {
            // Build document list from clusters.
            appendHtmlForDocuments(html, collectDocuments(result.clusters), true);
        }
        appendHtmlTrailer(html);

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
    private JLabel matchedDocumentsLabel;

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
            if (seqNum != null) {
                buffer.append("<span style=\"font-size: 10px\">(");
                buffer.append(seqNum);
                buffer.append(") </span>");
            }
            
            buffer.append("<a href=\"" + doc.getUrl() + "\"><b>");

            if ((doc.getTitle() == null) || (doc.getTitle().length() == 0)) {
                buffer.append("(no title)");
            } else {
                buffer.append(doc.getTitle());
            }
            buffer.append("</b></a>");

            if (doc.getProperty(RawDocument.PROPERTY_LANGUAGE) != null) {
                buffer.append(" [" +
                    doc.getProperty(RawDocument.PROPERTY_LANGUAGE) + "]");
            }

            buffer.append("<br>");

            String r = (String) doc.getProperty(RawDocument.PROPERTY_SNIPPET);

            if (r != null) {
                buffer.append(r);
                buffer.append("<br>");
            }

            buffer.append("<font color=green>");
            buffer.append(doc.getUrl());
            buffer.append("</font><br><br>");
        }

        return buffer;
    }

    private void appendHtmlHeader(StringBuffer buffer) {
        buffer.append("<html>");
        buffer.append("<meta><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></meta>\n");
        buffer.append("<body style=\"font-size: 10pt; font-family: Arial, Helvetica, sans-serif;\">");
    }

    private void appendHtmlTrailer(StringBuffer buffer) {
        buffer.append("</body></html>");
    }

    /**
     * This method converts a set of raw documents to a string.
     */
    public String getHtmlFor(final RawCluster rc) {
        final StringBuffer buffer = new StringBuffer();

        appendHtmlHeader(buffer);

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
        appendHtmlTrailer(buffer);

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
