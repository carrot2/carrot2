
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
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import carrot2.demo.DemoContext;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.impl.ClustersConsumerOutputComponent;
import com.dawidweiss.carrot.core.local.impl.FileLocalOutputComponent;

/**
 * @author Stanislaw Osinski
 */
public class QuerySaveDialog
{
    /** */
    private JDialog dialog;
    private Frame owner;

    /** */
    private DemoContext demoContext;

    /** */
    private String query;
    private String processId;
    private int requestedResults;
    private JTextField fileName;
    private JCheckBox saveClusters;

    /**
     * @param owner
     * @param demoContext
     * @param query
     */
    public QuerySaveDialog(Frame owner, DemoContext demoContext, String query,
        String processId, int requestedResults)
    {
        this.demoContext = demoContext;
        this.query = query;
        this.owner = owner;
        this.processId = processId;
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
                + " save query", true);
            dialog.setModal(true);
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
     * @return
     */
    private JPanel buildUI()
    {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        JPanel buttonPanel = buildButtonPanel();
        JPanel contentPanel = buildContentPanel();

        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
        mainPanel.add(contentPanel, BorderLayout.PAGE_START);

        return mainPanel;
    }

    /**
     * @return
     */
    private JPanel buildContentPanel()
    {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(5, 5));

        JLabel label = new JLabel("File name");
        fileName = new JTextField();
        fileName.setPreferredSize(new Dimension(220, 22));
        fileName.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                save();
            }
        });

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 4));
        labelPanel.add(label);
        contentPanel.add(labelPanel, BorderLayout.LINE_START);

        saveClusters = new JCheckBox("Save clusters");
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(fileName, BorderLayout.PAGE_START);
        centerPanel.add(saveClusters, BorderLayout.PAGE_END);
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        JButton chooseButton = new JButton("Browse");
        JPanel choosePanel = new JPanel();
        choosePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        choosePanel.add(chooseButton);
        contentPanel.add(choosePanel, BorderLayout.LINE_END);

        File queriesDir = new File("queries");
        File outputDir;
        if (queriesDir.exists() && queriesDir.isDirectory())
        {
            outputDir = queriesDir;
        }
        else
        {
            outputDir = new File(System.getProperty("user.dir"));
        }
        File outputFile = new File(outputDir, query + ".xml");

        final JFileChooser fileChooser = new JFileChooser(outputFile
            .getParentFile());
        fileChooser.setSelectedFile(outputFile);
        fileName.setText(outputFile.getAbsolutePath());
        chooseButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION)
                {
                    fileName.setText(fileChooser.getSelectedFile()
                        .getAbsolutePath());
                }
            }
        });

        return contentPanel;
    }

    /**
     * @return
     */
    private JPanel buildButtonPanel()
    {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 5));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dialog.setVisible(false);
            }
        });

        JButton okButton = new JButton("Save");
        okButton.setDefaultCapable(true);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                save();
            }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    /**
     * 
     */
    private void save()
    {
        Map requestParams = new HashMap();
        requestParams.put(LocalInputComponent.PARAM_REQUESTED_RESULTS, Integer
            .toString(requestedResults));
        try
        {
            ProcessingResult result = demoContext.getController().query(
                processId, query, requestParams);
            java.util.List rawClusters = ((ClustersConsumerOutputComponent.Result) result
                .getQueryResult()).clusters;
            FileLocalOutputComponent.saveRawClusters(rawClusters, query,
                new File(fileName.getText()), saveClusters.isSelected());
            dialog.setVisible(false);
        }
        catch (MissingProcessException e1)
        {
            throw new RuntimeException(e1);
        }
        catch (Exception e1)
        {
            throw new RuntimeException(e1);
        }
    }
}
