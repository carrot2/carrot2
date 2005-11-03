package carrot2.demo.settings;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.stachoodev.carrot.filter.lingo.lsicluster.LsiConstants;

/**
 * A Swing dialog for Lingo.
 * 
 * @author Dawid Weiss
 */
final class LingoSettingsDialog extends JPanel {
    private LingoClassicSettings settings;

    private SpinnerNumberModel clusterAssignmentSpinnerModel;
    private SpinnerNumberModel candidateClusterSpinnerModel;

    public LingoSettingsDialog(LingoClassicSettings settings) {
        this.settings = settings;
        buildGui();
    }

    private void buildGui() {
        JLabel label;
        HashMap params = settings.getRequestParams();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // LsiConstants.CLUSTER_ASSIGNMENT_THRESHOLD
        {
            double value = Double.parseDouble((String) params.get(LsiConstants.CLUSTER_ASSIGNMENT_THRESHOLD));

            final IntScale scaler = new IntScale(0.01, 5, 0.05);
            label = new JLabel("Cluster assignment threshold.");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.add(label);

            final JSlider assignmentThresholdSlider = new JSlider(
                    SwingConstants.HORIZONTAL, scaler.minInt, scaler.maxInt, scaler.toInt(value));
            assignmentThresholdSlider.setMinorTickSpacing(1);
            assignmentThresholdSlider.setMajorTickSpacing(10);
            assignmentThresholdSlider.setPaintLabels(true);
            assignmentThresholdSlider.setPaintTicks(true);
            assignmentThresholdSlider.setSnapToTicks(true);
            assignmentThresholdSlider.addChangeListener(new ChangeListener()
                {
                    public void stateChanged(ChangeEvent e)
                    {
                        if (assignmentThresholdSlider.getValueIsAdjusting())
                            return;
                        int value = assignmentThresholdSlider.getValue();
                        HashMap params = settings.getRequestParams();
                        params.put(LsiConstants.CLUSTER_ASSIGNMENT_THRESHOLD,
                                Double.toString(scaler.fromInt(value)));
                        settings.setRequestParams(params);
                    }
                });
            assignmentThresholdSlider.setLabelTable(scaler.createLabels(10));
            
            
            this.add(assignmentThresholdSlider);
            assignmentThresholdSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        // LsiConstants.CANDIDATE_CLUSTER_THRESHOLD
        {
            this.add(Box.createRigidArea(new Dimension(0, 5)));
            
            double value = Double.parseDouble((String) params.get(LsiConstants.CANDIDATE_CLUSTER_THRESHOLD));
            double min = 0.05;
            double max = 10;
            double step = 0.10;
            label = new JLabel("Candidate cluster threshold (" + min + " -- " + max + ")");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.add(label);
            candidateClusterSpinnerModel = new SpinnerNumberModel(value, min, max, step); 
            final JSpinner candidateClusterSpinner = new JSpinner(candidateClusterSpinnerModel);
            candidateClusterSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.add(candidateClusterSpinner);
        }
    }
}
