
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

package org.carrot2.demo.settings;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.carrot2.filter.haog.stc.STCConstants;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A settings panel for {@link org.carrot2.demo.settings.HaogStcSettings}. 
 *  
 * @author Karol Gołembniak
 */
class HaogStcSettingsDialog extends JPanel {
    private final HaogStcSettings settings;

    public HaogStcSettingsDialog(HaogStcSettings settings) {
        this.settings = settings;
        buildGui();
    }

    private void buildGui() {
        this.setLayout(new BorderLayout());
        
        final DefaultFormBuilder builder = 
            new DefaultFormBuilder(new FormLayout("pref", ""));
        
        builder.appendSeparator("Input preprocessing");

        builder.append(ThresholdHelper.createIntegerThreshold(settings, STCConstants.IGNORED_WORD_IF_IN_FEWER_DOCS,
                "Ignore word if in fewer docs:", 2, 10, 1, 1));
        builder.nextLine();
        builder.append(ThresholdHelper.createDoubleThreshold(settings, STCConstants.IGNORED_WORD_IF_IN_MORE_DOCS,
                "Ignore word if in more docs (%):", 0, 1, 0.1, 0.25));

        builder.appendSeparator("Base clusters");

        builder.append(ThresholdHelper.createIntegerThreshold(settings, STCConstants.MAX_BASE_CLUSTERS_COUNT,
                "Max base clusters:", 25, 500, 0, (500-25)/4));
        builder.append(ThresholdHelper.createDoubleThreshold(settings, STCConstants.MIN_BASE_CLUSTER_SCORE,
                "Min base cluster score:", 0, 10, 1, 1));
        builder.append(ThresholdHelper.createIntegerThreshold(settings, STCConstants.MIN_BASE_CLUSTER_SIZE,
                "Min base cluster size:", 2, 20, 0, 4));

        builder.appendSeparator("Merging and output");

        builder.append(ThresholdHelper.createDoubleThreshold(settings, STCConstants.MERGE_THRESHOLD,
                "Merge threshold:", 0, 1, 0.1, 0.25));

        builder.appendSeparator("Label creation");
        builder.append(ThresholdHelper.createIntegerThreshold(settings, STCConstants.MAX_PHRASE_LENGTH,
                "Maximum label length :", 2, 10, 1, 1));
        
        builder.appendSeparator("HAOG hierarchy creation way");
        builder.append(ThresholdHelper.createIntegerThreshold(settings, STCConstants.HIERATCHY_CREATION_WAY,
                "Simple (grandchild)/Full (kernel):", 0, 1, 1, 1));

        this.add(builder.getPanel(), BorderLayout.CENTER);
    }
}
