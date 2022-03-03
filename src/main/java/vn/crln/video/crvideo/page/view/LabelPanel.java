package vn.crln.video.crvideo.page.view;

import vn.crln.video.crvideo.control.CRButtonLabel;
import vn.crln.video.crvideo.control.CRDefaultTableModel;
import vn.crln.video.crvideo.control.CRLabelImage;
import vn.crln.video.crvideo.model.CRBoundingBox;
import vn.crln.video.crvideo.model.CRImage;
import vn.crln.video.crvideo.model.CRLabel;
import vn.crln.video.crvideo.model.CRProject;
import vn.crln.video.crvideo.page.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LabelPanel extends JPanel implements CRLabelImage.LabelImageDoneListener {
    private MainFrame owner;
    public LabelPanel setOwner(MainFrame mainFrame) {
        this.owner = mainFrame;
        return this;
    }

    private java.util.List<String> curVideos = null;
    private String curVideoPath = null;
    private java.util.List<CRImage> curImages = null;
    private java.util.List<CRLabel> curLabels = null;

    private JTable tableVideo;
    private JTable tableImage;
    private JPanel panelLabelButtons;

    public LabelPanel() {
        initUI();
    }

    private JPanel panelToolbox;
    private CRLabelImage labelImageControl;

    private void initUI() {
        setLayout(new GridBagLayout());
        //
        panelToolbox = new JPanel();
        labelImageControl = new CRLabelImage();
        labelImageControl.addLabelImageDoneListener(this);
        JSplitPane panelMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                panelToolbox, labelImageControl);
        panelMain.setDividerLocation(150);
        //
        initPanelToolbox();
        //
        tableVideo = initTableVideo(new JTable());
        tableImage = initTableImage(new JTable());
        JScrollPane scrollVideo = new JScrollPane(tableVideo);
        JScrollPane scrollImage = new JScrollPane(tableImage);
        JSplitPane splitPanel1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                scrollVideo, scrollImage);
        splitPanel1.setOneTouchExpandable(true);
        splitPanel1.setDividerLocation(200);
        JSplitPane splitPanel2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                splitPanel1, panelMain);
        splitPanel2.setOneTouchExpandable(true);
        splitPanel2.setDividerLocation(500);
        //
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1; c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(splitPanel2, c);
    }

    public void updateProjectUI() {
        loadAllVideos(tableVideo);
        clearAllImages(tableImage);
        loadListLabel();
    }

    private JTable initTableVideo(JTable table) {
        table.setFillsViewportHeight(true);
        CRDefaultTableModel model = new CRDefaultTableModel();
        model.setDefaultEditable(false);
        model.addColumn("Path");
        table.setModel(model);
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    int rowIdx = table.getSelectedRow();
                    if (rowIdx >= 0) {
                        onSelectVideo(curVideos.get(rowIdx));
                    }
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
        //
        return table;
    }
    private JTable initTableImage(JTable table) {
        table.setFillsViewportHeight(true);
        CRDefaultTableModel model = new CRDefaultTableModel();
        model.setDefaultEditable(false);
        model.addColumn("ID");
        model.addColumn("Path");
        table.setModel(model);
        //
        table.getColumn("ID").setPreferredWidth(60);
        table.getColumn("Path").setPreferredWidth(300);
        //
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    int rowIdx = table.getSelectedRow();
                    if (rowIdx >= 0) {
                        onSelectImage(curImages.get(rowIdx));
                    }
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
        //
        return table;
    }
    private void initPanelToolbox() {
        panelToolbox.setLayout(new GridBagLayout());
        //
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        //
        panelLabelButtons = new JPanel();
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        panelLabelButtons.setLayout(layout);
        JScrollPane scrollButtons = new JScrollPane(panelLabelButtons);
        panelToolbox.add(scrollButtons, c);
    }

    private void loadAllVideos(JTable table) {
        CRDefaultTableModel model = (CRDefaultTableModel)table.getModel();
        model.setRowCount(0); // clear all
        if (owner != null && owner.getProject() != null) {
            curVideos = new ArrayList<>();
            File directory = new File(owner.getProject().getVideoDirectory());
            if (directory.exists()) {
                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File f, String name) {
                        // We want to find only .c files
                        return name.endsWith(".mp4");
                    }
                };
                File[] files = directory.listFiles(filter);
                Arrays.sort(files, (o1, o2) -> o1.getAbsolutePath().compareTo(o2.getAbsolutePath()));
                if (files != null) {
                    for (File file : files) {
                        curVideos.add(file.getAbsolutePath());
                        model.addRow(new Object[]{file.getAbsolutePath()});
                    }
                }
            }
        }
    }
    private void onSelectVideo(String videoPath) {
        curVideoPath = videoPath;
        loadAllImages(videoPath, tableImage);
    }

    private void clearAllImages(JTable table) {
        CRDefaultTableModel model = (CRDefaultTableModel)table.getModel();
        model.setRowCount(0); // clear all
    }

    private void loadAllImages(String videoPath, JTable table) {
        CRDefaultTableModel model = (CRDefaultTableModel)table.getModel();
        model.setRowCount(0); // clear all
        curImages = owner.getAppDBService().loadImageOfVideo(
                owner.getProject(), videoPath
        );
        //
        for (CRImage image : curImages) {
            model.addRow(new Object[] {
                    image.getId(),
                    image.getImagePath()
            });
        }
    }

    private void onSelectImage(CRImage image) {
        labelImageControl.setImage(image);
        List<CRBoundingBox> boundingBoxes = owner.getAppDBService().loadBoundingBoxOfImage(curLabels, owner.getProject(), image);
        labelImageControl.setBoundingBoxes(boundingBoxes);
    }

    private void loadListLabel() {
        panelLabelButtons.removeAll();
        curLabels = owner.getAppDBService().loadLabelOfProjects(owner.getProject().getId());
        for (CRLabel label : curLabels) {
            CRButtonLabel btn = new CRButtonLabel(label);
            btn.addActionListener(e -> {
                onSelectLabel(btn.getCRLabel());
            });
            panelLabelButtons.add(btn);
        }
    }

    private void onSelectLabel(CRLabel label) {
        labelImageControl.setCurrentLabel(label);
    }

    @Override
    public void labelImageDone(CRProject project, CRImage image, List<CRBoundingBox> boundingBoxes) {
        owner.getAppDBService().saveBoundingBoxes(project, image, boundingBoxes);
    }
}
