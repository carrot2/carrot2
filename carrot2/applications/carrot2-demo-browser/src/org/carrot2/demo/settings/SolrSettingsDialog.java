
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

package org.carrot2.demo.settings;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import org.carrot2.util.StringUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Visual component for {@link SolrSettings}.
 * 
 * @author Stanislaw Osinski
 */
public class SolrSettingsDialog extends JPanel
{
    private static final long serialVersionUID = 1L;

    private final transient SolrSettings settings;

    public SolrSettingsDialog(SolrSettings settings)
    {
        this.settings = settings;
        buildGui();
    }

    private void buildGui()
    {
        this.setLayout(new BorderLayout());

        final DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
            "fill:200px:grow, 4dlu, pref", ""));

        builder.appendSeparator("Solr service configuration");

        String url = buildUrl(settings.solrUrlBase, settings.solrQueryString);

        final JTextField serviceUrl;
        serviceUrl = new JTextField(url);
        serviceUrl.setEditable(false);
        serviceUrl.setCaretPosition(0);
        builder.append(serviceUrl);

        final JButton configureButton = new JButton("Configure");
        configureButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                openSolrConfigDialog(settings, serviceUrl);
            }
        });
        builder.append(configureButton);
        builder.nextLine();

        this.add(builder.getPanel(), BorderLayout.CENTER);
    }

    private String buildUrl(String solrUrlBase, String solrQueryString)
    {
        if (StringUtils.isBlank(solrUrlBase) || StringUtils.isBlank(solrQueryString))
        {
            return "";
        }
        else
        {
            return solrUrlBase + "/" + solrQueryString;
        }
    }

    private void openSolrConfigDialog(SolrSettings solrSettings, JTextComponent serviceUrl)
    {
        final DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(
            "pref, pref, fill:350px:grow"));

        builder.appendSeparator("Solr service");

        final JTextField urlBase = new JTextField(solrSettings.solrUrlBase);
        builder.append("URL base:", urlBase);
        builder.nextLine();

        final JTextField queryString = new JTextField(solrSettings.solrQueryString);
        builder.append("Query string:", queryString);
        builder.nextLine();

        builder.appendSeparator("Field mappings");

        final JTextField solrIdField = new JTextField(solrSettings.solrIdField);
        builder.append("ID field:", solrIdField);
        builder.nextLine();

        final JTextField solrTitleField = new JTextField(solrSettings.solrTitleField);
        builder.append("Title field:", solrTitleField);
        builder.nextLine();

        final JTextField solrSnippetField = new JTextField(solrSettings.solrSnippetField);
        builder.append("Snippet field:", solrSnippetField);
        builder.nextLine();

        final JTextField solrUrlField = new JTextField(solrSettings.solrUrlField);
        builder.append("URL field:", solrUrlField);
        builder.nextLine();

        builder.appendSeparator("Custom transformation XSLT (optional)");

        final JTextField solrXslt = new JTextField(solrSettings.solrXslt);
        builder.append("XSLT URL:", solrXslt);
        builder.nextLine();

        final int result = JOptionPane.showConfirmDialog(serviceUrl, builder.getPanel(),
            "Configure Solr", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION)
        {
            serviceUrl.setText(buildUrl(urlBase.getText(), queryString.getText()));
            serviceUrl.setCaretPosition(0);

            solrSettings.setConfig(urlBase.getText(), queryString.getText(), solrXslt
                .getText(), solrIdField.getText(), solrTitleField.getText(),
                solrSnippetField.getText(), solrUrlField.getText());
        }
    }
}
