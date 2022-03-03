package vn.crln.video.crvideo.page.view;

import vn.crln.video.crvideo.control.CRDefaultTableModel;
import vn.crln.video.crvideo.model.CRLabel;
import vn.crln.video.crvideo.model.CRProject;
import vn.crln.video.crvideo.model.Size;
import vn.crln.video.crvideo.page.MainFrame;
import vn.crln.video.crvideo.service.data.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

public class ProjectPanel extends JPanel {
    private MainFrame owner;
    public ProjectPanel setOwner(MainFrame mainFrame) {
        this.owner = mainFrame;
        return this;
    }

    private JTextField videoPathTextField;
    private JTextField imagePathTextField;
    private JSpinner frameWidthSpinner;
    private JSpinner frameHeightSpinner;
    private JTable tableLabel;
    private JTextField labelNameTextField;
    private JTextField labelDescriptionTextField;
    private JPanel labelColorView;

    public ProjectPanel() {
        initUI();
    }

    void initUI() {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints c = new GridBagConstraints();
        // group nhãn
        JPanel groupLabel = new JPanel();
        groupLabel.setBorder(BorderFactory.createTitledBorder("Nhãn"));
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        add(groupLabel, c);
        initGroupPanel(groupLabel);
        // panel video source
        JPanel panelVideoSource = new JPanel();
        c.gridx = 0; c.gridy = 1;
        c.weightx = 1; c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(panelVideoSource, c);
        initVideoSourcePanel(panelVideoSource);
        // panel target image
        JPanel panelTargetImage = new JPanel();
        c.gridx = 0; c.gridy = 2;
        c.weightx = 1; c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(panelTargetImage, c);
        initTargetImagePanel(panelTargetImage);
        // panel frame size
        JPanel panelFrameSize = new JPanel();
        c.gridx = 0; c.gridy = 3;
        c.weightx = 1; c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(panelFrameSize, c);
        initFrameSizePanel(panelFrameSize);
    }

    private void initGroupPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        //
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0; c.weighty = 0;
        JPanel panelAdd = new JPanel();
        panel.add(panelAdd, c);
        //
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        tableLabel = new JTable();
        tableLabel.setFillsViewportHeight(true);
        JScrollPane scrollTableLabel = new JScrollPane(tableLabel);
        panel.add(scrollTableLabel, c);
        CRDefaultTableModel model = new CRDefaultTableModel();
        model.setDefaultEditable(false);
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Description");
        model.addColumn("Color");
        tableLabel.setModel(model);
        //
        tableLabel.getColumn("ID").setPreferredWidth(80);
        tableLabel.getColumn("Name").setPreferredWidth(200);
        tableLabel.getColumn("Description").setPreferredWidth(800);
        tableLabel.getColumn("Color").setPreferredWidth(100);
        // panelAdd
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        panelAdd.setLayout(layout);
        JLabel lbl = new JLabel("Tên nhãn:");
        labelNameTextField = new JTextField();
        labelNameTextField.setPreferredSize(new Dimension(300, 20));
        panelAdd.add(lbl);
        panelAdd.add(labelNameTextField);
        //
        lbl = new JLabel("Mô tả:");
        labelDescriptionTextField = new JTextField();
        labelDescriptionTextField.setPreferredSize(new Dimension(300, 20));
        panelAdd.add(lbl);
        panelAdd.add(labelDescriptionTextField);
        //
        lbl = new JLabel("Màu:");
        labelColorView = new JPanel();
        labelColorView.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        labelColorView.setPreferredSize(new Dimension(120, 20));
        labelColorView.setCursor(new Cursor(Cursor.HAND_CURSOR));
        labelColorView.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                Color color = JColorChooser.showDialog(owner, "Select a color",
                        labelColorView.getBackground());
                if (color != null) {
                    labelColorView.setBackground(color);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        panelAdd.add(lbl);
        panelAdd.add(labelColorView);
        //
        JButton btn = new JButton("Thêm");
        panelAdd.add(btn);
        btn.addActionListener(e -> {
            addNewLabel();
        });
    }

    private void initVideoSourcePanel(JPanel panel) {
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        panel.setLayout(layout);
        //
        JLabel lbl = new JLabel("Video Source:");
        panel.add(lbl);
        videoPathTextField = new JTextField();
        videoPathTextField.setPreferredSize(new Dimension(700, 20));
        panel.add(videoPathTextField);
        //
        JButton btn = new JButton("...");
        btn.addActionListener(e -> {
            JFileChooser fileChooserDialog = new JFileChooser();
            fileChooserDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooserDialog.setMultiSelectionEnabled(false);
            if (fileChooserDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File folderPath = fileChooserDialog.getSelectedFile();
                videoPathTextField.setText(folderPath.getAbsolutePath());
                onNewProjectValue("VideoPath", folderPath.getAbsolutePath());
            }
        });
        panel.add(btn);
    }
    private void initTargetImagePanel(JPanel panel) {
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        panel.setLayout(layout);
        //
        JLabel lbl = new JLabel("Target Image:");
        panel.add(lbl);
        imagePathTextField = new JTextField();
        imagePathTextField.setPreferredSize(new Dimension(700, 20));
        panel.add(imagePathTextField);
        JButton btn = new JButton("...");
        btn.addActionListener(e -> {
            JFileChooser fileChooserDialog = new JFileChooser();
            fileChooserDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooserDialog.setMultiSelectionEnabled(false);
            if (fileChooserDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File folderPath = fileChooserDialog.getSelectedFile();
                imagePathTextField.setText(folderPath.getAbsolutePath());
                onNewProjectValue("ImagePath", folderPath.getAbsolutePath());
            }
        });
        panel.add(btn);
    }
    private void initFrameSizePanel(JPanel panel) {
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        panel.setLayout(layout);
        //
        JLabel lbl = new JLabel("Frame Width:");
        panel.add(lbl);
        frameWidthSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));
        frameWidthSpinner.setEditor(new JSpinner.NumberEditor(frameWidthSpinner, "#,##0"));
        frameWidthSpinner.setPreferredSize(new Dimension(300, 20));
        panel.add(frameWidthSpinner);
        lbl = new JLabel("Height:");
        panel.add(lbl);
        frameHeightSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));
        frameHeightSpinner.setEditor(new JSpinner.NumberEditor(frameHeightSpinner, "#,##0"));
        frameHeightSpinner.setPreferredSize(new Dimension(300, 20));
        panel.add(frameHeightSpinner);
        //
        frameWidthSpinner.addChangeListener(e -> {
            int w = (int)((JSpinner)e.getSource()).getValue();
            int h = (int)(frameHeightSpinner.getValue());
            if (w > 0 & h > 0) {
                onNewProjectValue("FrameSize", new Size(w, h));
            }
        });
        frameHeightSpinner.addChangeListener(e -> {
            int w = (int)(frameWidthSpinner.getValue());
            int h = (int)((JSpinner)e.getSource()).getValue();
            if (w > 0 & h > 0) {
                onNewProjectValue("FrameSize", new Size(w, h));
            }
        });
    }

    private boolean noTriggerChanged = false;
    public void updateProjectUI() {
        noTriggerChanged = true;
        videoPathTextField.setText(owner.getProject().getVideoDirectory());
        imagePathTextField.setText(owner.getProject().getFrameDirectory());
        frameWidthSpinner.setValue(owner.getProject().getFrameDestinationSize().getWidth());
        frameHeightSpinner.setValue(owner.getProject().getFrameDestinationSize().getHeight());
        //
        reloadLabelOfProject();
        //
        noTriggerChanged = false;
    }

    private void reloadLabelOfProject() {
        CRDefaultTableModel model = (CRDefaultTableModel)tableLabel.getModel();
        model.setRowCount(0); // clear
        //
        java.util.List<CRLabel> list = owner.getAppDBService().loadLabelOfProjects(owner.getProject().getId());
        for (CRLabel label : list) {
            model.addRow(new Object[] {
                    label.getId(),
                    label.getName(),
                    label.getDescription(),
                    "#" + Integer.toHexString(label.getColor().getRGB())
            });
        }
    }

    private void onNewProjectValue(String property, Object newValue) {
        if (noTriggerChanged) return;
        switch (property) {
            case "Name":
                owner.getProject().setName((String) newValue);
                break;
            case "Description":
                owner.getProject().setDescription((String)newValue);
                break;
            case "VideoPath":
                owner.getProject().setVideoDirectory((String)newValue);
                break;
            case "ImagePath":
                owner.getProject().setFrameDirectory((String)newValue);
                break;
            case "FrameSize":
                owner.getProject().setFrameDestinationSize((Size)newValue);
                break;
        }
        owner.updateProjectInfo();
    }

    private void addNewLabel() {
        String name = labelNameTextField.getText();
        String description = labelDescriptionTextField.getText();
        Color color = labelColorView.getBackground();
        if (!Util.Check.Str.isWhiteStringOrEmpty(name)
        && color != null) {
            if (owner.getAppDBService().addLabelToProject(owner.getProject(),
                    name, description, color)) {
                reloadLabelOfProject();
            }
        } else {
            JOptionPane.showMessageDialog(owner, "Tên không được bỏ trống",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
