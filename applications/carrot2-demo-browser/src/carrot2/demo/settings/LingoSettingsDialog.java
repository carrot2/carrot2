package carrot2.demo.settings;

import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.stachoodev.carrot.filter.lingo.lsicluster.LsiConstants;

/**
 * A Swing dialog for Lingo.
 * 
 * @author Dawid Weiss
 */
final class LingoSettingsDialog extends JPanel {

    private HashMap params;

    private SpinnerNumberModel clusterAssignmentSpinnerModel;
    private SpinnerNumberModel candidateClusterSpinnerModel;

    public LingoSettingsDialog(HashMap params) {
        this.params = new HashMap(params);
        buildGui();
    }

    private void buildGui() {
        JLabel label;
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // LsiConstants.CLUSTER_ASSIGNMENT_THRESHOLD
        double value = Double.parseDouble((String) params.get(LsiConstants.CLUSTER_ASSIGNMENT_THRESHOLD));
        double min = 0.05;
        double max = 10;
        double step = 0.10;
        label = new JLabel("Cluster assignment threshold (" + min + " -- " + max + ")");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(label);
        clusterAssignmentSpinnerModel = new SpinnerNumberModel(value, min, max, step); 
        final JSpinner clusterAssignmentSpinner = new JSpinner(clusterAssignmentSpinnerModel);
        clusterAssignmentSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(clusterAssignmentSpinner);

        this.add(Box.createRigidArea(new Dimension(0, 5)));
        
        value = Double.parseDouble((String) params.get(LsiConstants.CANDIDATE_CLUSTER_THRESHOLD));
        min = 0.05;
        max = 10;
        step = 0.10;
        label = new JLabel("Candidate cluster threshold (" + min + " -- " + max + ")");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(label);
        candidateClusterSpinnerModel = new SpinnerNumberModel(value, min, max, step); 
        final JSpinner candidateClusterSpinner = new JSpinner(candidateClusterSpinnerModel);
        candidateClusterSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(candidateClusterSpinner);
    }

    public HashMap getParams() {
        this.params.put(LsiConstants.CLUSTER_ASSIGNMENT_THRESHOLD,
                clusterAssignmentSpinnerModel.getValue().toString());
        this.params.put(LsiConstants.CANDIDATE_CLUSTER_THRESHOLD,
                candidateClusterSpinnerModel.getValue().toString());
        
        return params;
    }
}
