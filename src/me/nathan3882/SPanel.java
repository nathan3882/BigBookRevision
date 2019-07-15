package me.nathan3882;

import javax.swing.*;

public class SPanel {

    private final JPanel thePanel;
    private final String referenceName;
    private BusinessRevision businessRevision;

    public SPanel(BusinessRevision businessRevision) {
        if (businessRevision.getActivePanelClass() == null) {
            businessRevision.setActivePanelClass(this);
        }
        SPanel active = businessRevision.getActivePanelClass();
        this.thePanel = active.getPanel();
        this.referenceName = active.getPanelReferenceName();
    }

    public JPanel getPanel() {
        return thePanel;
    }

    public String getPanelReferenceName() {
        return referenceName;
    }

    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(getPanel(), message);
    }
}
