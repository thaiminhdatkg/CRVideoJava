package vn.crln.video.crvideo.control;

import vn.crln.video.crvideo.model.CRLabel;

import javax.swing.*;
import java.security.cert.CRL;

public class CRButtonLabel extends JButton {
    private CRLabel label;

    public CRLabel getCRLabel() {
        return label;
    }

    public CRButtonLabel setCRLabel(CRLabel label) {
        this.label = label;
        this.setText(label.getName());
        this.setBackground(label.getColor());
        return this;
    }

    public CRButtonLabel(CRLabel label) {
        this.label = label;
        this.setText(label.getName());
        this.setBackground(label.getColor());
    }
}
