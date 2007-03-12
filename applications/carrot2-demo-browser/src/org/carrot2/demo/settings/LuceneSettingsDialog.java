
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

package org.carrot2.demo.settings;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.carrot2.demo.swing.SwingUtils;
import org.carrot2.input.lucene.StandardAnalyzerWithPorterStemmer;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Visual component for {@link LuceneSettings}.
 * 
 * @author Dawid Weiss
 */
public class LuceneSettingsDialog extends JPanel {

    private final transient LuceneSettings settings;

    private transient JTextField indexLocationLabel;
    
    public LuceneSettingsDialog(LuceneSettings settings) {
        this.settings = settings;
        buildGui();
    }

    private void buildGui() {
        this.setLayout(new BorderLayout());
        
        final DefaultFormBuilder builder = 
            new DefaultFormBuilder(new FormLayout("fill:200px:grow, 4dlu, pref"));

        builder.appendSeparator("Lucene index location");

        this.indexLocationLabel = new JTextField();
        this.indexLocationLabel.setEditable(false);
        if (settings.luceneIndexDir != null) {
            this.indexLocationLabel.setText(
                    settings.luceneIndexDir.getAbsolutePath());
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
                        final Object [] fieldsList = fields.toArray();
                        final JList list = new JList(fieldsList);
                        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                        if (settings.searchFields != null) {
                            for (int i = 0; i < settings.searchFields.length; i++) {
                                final String key = settings.searchFields[i];
                                for (int j = 0; j < fieldsList.length; j++) {
                                    if (fieldsList[j].equals(key)) {
                                        list.addSelectionInterval(j, j);
                                    }
                                }
                            }
                        }
                        builder.append(list);
                        builder.nextLine();

                        builder.appendSeparator("Results fields");

                        builder.append(new JLabel("URL:"));
                        final JComboBox url = new JComboBox(fields.toArray());
                        if (settings.urlField != null) {
                            url.setSelectedItem(settings.urlField);
                        }
                        builder.append(url);
                        builder.nextLine();

                        builder.append(new JLabel("Title:"));
                        final JComboBox title = new JComboBox(fields.toArray());
                        if (settings.titleField != null) {
                            title.setSelectedItem(settings.titleField);
                        }
                        builder.append(title);
                        builder.nextLine();

                        builder.append(new JLabel("Snippet:"));
                        final JComboBox snippet = new JComboBox(fields.toArray());
                        if (settings.summaryField != null) {
                            snippet.setSelectedItem(settings.summaryField);
                        }
                        builder.append(snippet);
                        builder.nextLine();

                        builder.appendSeparator("Analyzer");
                        final JComboBox analyzers = new JComboBox(new Object [] {
                                StandardAnalyzerWithPorterStemmer.class.getName(),
                                StandardAnalyzer.class.getName(), 
                                SimpleAnalyzer.class.getName(),
                        });
                        if (settings.analyzer != null) {
                            analyzers.setSelectedItem(settings.analyzer.getClass().getName());
                        }
                        builder.append(analyzers);
                        builder.nextLine();

                        final int result = JOptionPane.showConfirmDialog(indexLocationEditButton, builder.getPanel(), "Select fields",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                        if (result == JOptionPane.OK_OPTION) {
                            indexLocationLabel.setText(luceneIndexDir.getAbsolutePath());
                            final String [] searchFields = (String []) Arrays.asList(
                                    list.getSelectedValues()).toArray(
                                            new String [list.getSelectedIndices().length]);

                            if (searchFields.length == 0) {
                                JOptionPane.showMessageDialog(indexLocationEditButton, "At least one search field is required.");
                                return;
                            }

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
