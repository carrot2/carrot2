package carrot2.demo.settings;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import carrot2.demo.swing.util.JThreshold;

import com.stachoodev.carrot.filter.lingo.lsicluster.LsiConstants;

/**
 * Settings dialog for Lingo classic.
 * 
 * @author Dawid Weiss
 */
final class LingoSettingsDialog extends JPanel {
    private LingoClassicSettings settings;

    public LingoSettingsDialog(LingoClassicSettings settings) {
        this.settings = settings;
        buildGui();
    }

    private void buildGui() {
        HashMap params = settings.getRequestParams();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // LsiConstants.CLUSTER_ASSIGNMENT_THRESHOLD
        {
            double value = Double.parseDouble((String) params.get(LsiConstants.CLUSTER_ASSIGNMENT_THRESHOLD));
            final JThreshold comp = new JThreshold("Cluster assignment threshold.", 0.01, 4, 0.1, 1.0);
            comp.setValue(value);
            comp.addChangeListener(new ChangeListener()
                {
                    public void stateChanged(ChangeEvent e)
                    {
                        HashMap params = settings.getRequestParams();
                        params.put(LsiConstants.CLUSTER_ASSIGNMENT_THRESHOLD,
                                Double.toString(comp.getValue()));
                        settings.setRequestParams(params);
                    }
                });

            this.add(comp);
            comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        this.add(Box.createRigidArea(new Dimension(0, 5)));

        // LsiConstants.CANDIDATE_CLUSTER_THRESHOLD
        {
            double value = Double.parseDouble((String) params.get(LsiConstants.CANDIDATE_CLUSTER_THRESHOLD));
            final JThreshold comp = new JThreshold("Candidate cluster threshold.", 0.05, 5, 0.1, 1.0);
            comp.setValue(value);
            comp.addChangeListener(new ChangeListener()
                {
                    public void stateChanged(ChangeEvent e)
                    {
                        HashMap params = settings.getRequestParams();
                        params.put(LsiConstants.CANDIDATE_CLUSTER_THRESHOLD,
                                Double.toString(comp.getValue()));
                        settings.setRequestParams(params);
                    }
                });

            this.add(comp);
            comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
    }
}
