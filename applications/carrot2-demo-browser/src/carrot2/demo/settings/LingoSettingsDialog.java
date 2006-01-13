
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

package carrot2.demo.settings;

import javax.swing.JPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.stachoodev.carrot.filter.lingo.lsicluster.LsiConstants;

/**
 * Settings dialog for Lingo classic.
 * 
 * @author Dawid Weiss
 */
final class LingoSettingsDialog extends JPanel {
    private final LingoClassicSettings settings;

    public LingoSettingsDialog(LingoClassicSettings settings) {
        this.settings = settings;
        buildGui();
    }

    private void buildGui() {
        final DefaultFormBuilder builder = 
            new DefaultFormBuilder(new FormLayout("pref", ""));

        builder.appendSeparator("Lingo classic");
        
        builder.append(ThresholdHelper.createDoubleThreshold(settings, LsiConstants.CLUSTER_ASSIGNMENT_THRESHOLD,
                "Cluster assignment threshold:", 0.01, 4, 0.1, 1.0));
        builder.append(ThresholdHelper.createDoubleThreshold(settings, LsiConstants.CANDIDATE_CLUSTER_THRESHOLD,
                "Candidate cluster threshold:", 0.05, 5, 0.1, 1.0));
        
        this.add(builder.getPanel());
    }
}
