package me.nathan3882.forms;

import me.nathan3882.BusinessRevision;
import me.nathan3882.SPanel;

import javax.swing.*;
import java.awt.*;

public class CoreForm extends SPanel {

    private static final String PANEL_REFERENCE_NAME = "coreForm";
    private final BusinessRevision businessRevision;
    private JLabel welcomeLabel;
    private JPanel panel;

    public CoreForm(BusinessRevision businessRevision) {
        super(businessRevision);
        this.businessRevision = businessRevision;
        Font font = new Font("Montserrat", Font.PLAIN, 15);
        welcomeLabel.setFont(font);
    }

    @Override
    public JPanel getPanel() {
        return this.panel;
    }

    public String getPanelReferenceName() {
        return PANEL_REFERENCE_NAME;
    }

    public BusinessRevision getBusinessRevision() {
        return businessRevision;
    }


    public JLabel getWelcomeLabel() {
        return welcomeLabel;
    }
}
