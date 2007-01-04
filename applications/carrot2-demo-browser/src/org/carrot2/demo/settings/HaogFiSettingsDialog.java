
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

import javax.swing.JPanel;

import org.carrot2.filter.haog.fi.FIConstants;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A settings panel for {@link org.carrot2.demo.settings.HaogFiSettings}. 
 *  
 * @author Karol Gołembniak
 */
class HaogFiSettingsDialog extends JPanel {
    private final transient HaogFiSettings settings;

    public HaogFiSettingsDialog(HaogFiSettings settings) {
        this.settings = settings;
        buildGui();
    }

    private void buildGui() {
        this.setLayout(new BorderLayout());
        
        final DefaultFormBuilder builder = 
            new DefaultFormBuilder(new FormLayout("pref", ""));
        
        builder.appendSeparator("Input processing");
        builder.append(ThresholdHelper.createDoubleThreshold(settings, FIConstants.IGNORED_WORD_IF_IN_MORE_DOCS,
                "Ignore word if in more docs (%):", 0, 1, 0.01, 0.25));

        builder.appendSeparator("Frequent sets generation");
        builder.append(ThresholdHelper.createDoubleThreshold(settings, FIConstants.MIN_SUPPORT,
                "Minimum word support(affects processing time):", 0, 1, 0.01, 0.25));
        builder.append(ThresholdHelper.createIntegerThreshold(settings, FIConstants.MAX_ITEMSETS_GENERATION_TIME,
                "Maximum time for itemsets generation:", 0, 60, 1, 10));

        builder.appendSeparator("Cluster processing");
        builder.append(ThresholdHelper.createDoubleThreshold(settings, FIConstants.LINK_TRESHOLD,
                "Link threshold:", 0, 1, 0.1, 0.25));
        builder.append(ThresholdHelper.createIntegerThreshold(settings, FIConstants.MAX_PRESENTED_CLUSTERS,
                "Max presented clusters:", 0, 500, 10, 100));
        builder.append(ThresholdHelper.createIntegerThreshold(settings, FIConstants.MIN_CLUSTER_SIZE,
                "Min base cluster size:", 2, 30, 0, 10));

        builder.appendSeparator("Label creation");
        builder.append(ThresholdHelper.createIntegerThreshold(settings, FIConstants.MAX_PHRASE_LENGTH,
                "Maximum label length :", 2, 10, 1, 1));

        builder.appendSeparator("HAOG hierarchy creation way");
        builder.append(ThresholdHelper.createIntegerThreshold(settings, FIConstants.HIERATCHY_CREATION_WAY,
                "Simple (grandchild)/Full (kernel):", 0, 1, 1, 1));

        this.add(builder.getPanel(), BorderLayout.CENTER);
    }
}
