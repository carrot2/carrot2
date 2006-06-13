
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

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JPanel;

import com.dawidweiss.carrot.filter.stc.StcConstants;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import fuzzyAnts.FuzzyAntsParameters;

/**
 * A settings panel for {@link carrot2.demo.settings.FuzzyAntsSettings}. 
 *  
 * @author Dawid Weiss
 */
class FuzzyAntsSettingsDialog extends JPanel {
    private final FuzzyAntsSettings settings;

    public FuzzyAntsSettingsDialog(FuzzyAntsSettings settings) {
        this.settings = settings;
        buildGui();
    }

    private void buildGui() {
        final DefaultFormBuilder builder = 
            new DefaultFormBuilder(new FormLayout("pref", ""));

        builder.appendSeparator("Ants fuzzification");

        builder.append(ThresholdHelper.createIntegerThreshold(settings, FuzzyAntsParameters.N1,
                "n1", 2, 20, 1, 1));
        builder.nextLine();
        builder.append(ThresholdHelper.createIntegerThreshold(settings, FuzzyAntsParameters.M1,
                "m1", 2, 20, 1, 1));
        builder.nextLine();
        builder.append(ThresholdHelper.createIntegerThreshold(settings, FuzzyAntsParameters.N2,
                "n2", 2, 20, 1, 1));
        builder.nextLine();
        builder.append(ThresholdHelper.createIntegerThreshold(settings, FuzzyAntsParameters.M2,
                "m2", 2, 20, 1, 1));
        
        builder.appendSeparator("Iterations");
        builder.append(ThresholdHelper.createIntegerThreshold(settings, FuzzyAntsParameters.NUMBER_OF_ITERATIONS,
                "Number of terations", 100, 7000, 50, 100));

        this.add(builder.getPanel());
    }
}
