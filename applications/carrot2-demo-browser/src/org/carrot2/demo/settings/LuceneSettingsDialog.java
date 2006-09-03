package org.carrot2.demo.settings;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.*;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.carrot2.demo.swing.SwingUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Visual component for {@link LuceneSettings}.
 * 
 * @author Dawid Weiss
 */
public class LuceneSettingsDialog extends JPanel {

    private final LuceneSettings settings;

    private JTextField indexLocationLabel;
    
    public LuceneSettingsDialog(LuceneSettings settings) {
        this.setLayout(new BorderLayout());
        this.settings = settings;
        buildGui();
    }

    private void buildGui() {
        final DefaultFormBuilder builder = 
            new DefaultFormBuilder(new FormLayout("fill:200px:grow, 4dlu, pref"));

        builder.appendSeparator("Lucene index location");

        this.indexLocationLabel = new JTextField();
        this.indexLocationLabel.setEditable(false);
        if (settings.getIndexDir() != null) {
            this.indexLocationLabel.setText(
                    settings.getIndexDir().getAbsolutePath());
        }

        final JButton indexLocationEditButton = new JButton();
        indexLocationEditButton.setText("Edit");
        indexLocationEditButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    final JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new File(indexLocationLabel.getText()));
                    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setMultiSelectionEnabled(false);
                    if (chooser.showOpenDialog(indexLocationEditButton) == JFileChooser.APPROVE_OPTION) {
                        final File luceneIndexDir = chooser.getSelectedFile();
                        // verify if it's lucene's index.
                        final Collection fields;
                        try {
                            IndexReader reader = IndexReader.open(luceneIndexDir);
                            fields = reader.getFieldNames(FieldOption.ALL);
                            reader.close();
                        } catch (IOException e) {
                            SwingUtils.showExceptionDialog(indexLocationEditButton, 
                                    "Could not open Lucene index.", e);
                            return;
                        }

                        // show details dialog.
                        final DefaultFormBuilder builder = 
                            new DefaultFormBuilder(new FormLayout("pref:grow"));

                        builder.appendSeparator("Search fields");
                        final JList list = new JList(fields.toArray());
                        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                        builder.append(list);
                        builder.nextLine();

                        builder.appendSeparator("Results fields");

                        builder.append(new JLabel("URL:"));
                        final JComboBox url = new JComboBox(fields.toArray());
                        builder.append(url);
                        builder.nextLine();

                        builder.append(new JLabel("Title:"));
                        final JComboBox title = new JComboBox(fields.toArray());
                        builder.append(title);
                        builder.nextLine();

                        builder.append(new JLabel("Snippet:"));
                        final JComboBox snippet = new JComboBox(fields.toArray());
                        builder.append(snippet);
                        builder.nextLine();

                        builder.appendSeparator("Analyzer");
                        final JComboBox analyzers = new JComboBox(new Object [] {
                                StandardAnalyzer.class.getName(), 
                                SimpleAnalyzer.class.getName(),
                        });
                        builder.append(analyzers);
                        builder.nextLine();

                        final int result = JOptionPane.showConfirmDialog(indexLocationEditButton, builder.getPanel(), "Select fields",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                        if (result == JOptionPane.OK_OPTION) {
                            indexLocationLabel.setText(luceneIndexDir.getAbsolutePath());
                            final String [] searchFields = (String []) Arrays.asList(
                                    list.getSelectedValues()).toArray(
                                            new String [list.getSelectedIndices().length]);
                            
                            final Analyzer analyzer;
                            try {
                                analyzer = (Analyzer) Thread.currentThread().getContextClassLoader().loadClass(
                                    (String) analyzers.getSelectedItem()).newInstance();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                            settings.setConfig(
                                    luceneIndexDir,
                                    searchFields,
                                    (String) url.getSelectedItem(),
                                    (String) title.getSelectedItem(),
                                    (String) snippet.getSelectedItem(),
                                    analyzer);
                        }
                    }
                }
            }
        );

        builder.append(indexLocationLabel);
        builder.append(indexLocationEditButton);
        builder.nextLine();

        this.add(builder.getPanel(), BorderLayout.CENTER);
    }
}
