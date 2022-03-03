package vn.crln.video.crvideo.page.view;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import vn.crln.video.crvideo.control.CRDefaultTableModel;
import vn.crln.video.crvideo.control.CRPictureBox;
import vn.crln.video.crvideo.model.CRImage;
import vn.crln.video.crvideo.page.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class ExtractFramePanel extends JPanel {
    private MainFrame owner;
    public ExtractFramePanel setOwner(MainFrame mainFrame) {
        this.owner = mainFrame;
        return this;
    }
    private java.util.List<String> curVideos = null;
    private String curVideoPath = null;
    private java.util.List<CRImage> curImages = null;

    private JTable tableVideo;
    private JTable tableImage;
    private CRPictureBox pictureBox;

    public ExtractFramePanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());

        tableVideo = initTableVideo(new JTable());
        tableImage = initTableImage(new JTable());

        JScrollPane scrollVideo = new JScrollPane(tableVideo);
        JScrollPane scrollImage = new JScrollPane(tableImage);

        JPanel panelMain = initMainPanel(new JPanel());

        JSplitPane splitPanel1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                scrollVideo, panelMain);
        splitPanel1.setOneTouchExpandable(true);
        splitPanel1.setDividerLocation(200);
        JSplitPane splitPanel2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                splitPanel1, scrollImage);
        splitPanel2.setOneTouchExpandable(true);
        splitPanel2.setDividerLocation(1100);
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
    }

    private JButton btnPlay = null;
    private JButton btnSave = null;
    private JButton btnSaveAll = null;
    private JButton btnDeleteSelectedImages = null;

    private JPanel initMainPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        //
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        //
        JButton btn = btnSave = new JButton(">>>");
        btn.setEnabled(false);
        btn.addActionListener((e) -> {
            saveImage(null);
        });
        panel.add(btn, c);
        //
        btn = btnSaveAll = new JButton("Tách tự động");
        btn.addActionListener((e) -> {
            extractAllFrames();
        });
        c.gridy = 2;
        panel.add(btn, c);
        //
        c.gridx = 0; c.gridy = 0;
        btn = btnPlay = new JButton("Play video");
        btn.addActionListener((e) -> {
            if (playingVideo) {
                // stop
                stopVideo();
            } else {
                // play
                autoExtractAll = false;
                playVideo(curVideoPath);
            }
        });
        panel.add(btn, c);
        //
        c.gridy = 3;
        btn = btnDeleteSelectedImages = new JButton("Xóa ảnh đã chọn");
        btn.addActionListener((e) -> {
            deleteSelectedImages();
        });
        panel.add(btn, c);
        //
        c.gridx = 0; c.gridy = 4;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1; c.weighty = 1;
        //
        pictureBox = new CRPictureBox();
        panel.add(pictureBox, c);
        //
        return panel;
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
        return table;
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
        if (curVideoPath != null && curVideoPath.equals(videoPath)) return;
        curVideoPath = videoPath;
        loadAllImages(videoPath, tableImage);
        //
        // playVideo(videoPath);
        stopVideo();
    }

    private void deleteSelectedImages() {
        int[] selectedRowIdxes = tableImage.getSelectedRows();
        for (int i = selectedRowIdxes.length - 1; i >= 0; i--) {
            deleteImage(selectedRowIdxes[i]);
        }
    }
    private void deleteImage(int rowIdx) {
        //
        Integer imageid = Integer.parseInt(tableImage.getValueAt(rowIdx, 0).toString());
        String imagePath = tableImage.getValueAt(rowIdx, 1).toString();
        //
        if (owner.getAppDBService().deleteImageFromProject(owner.getProject(), curVideoPath, imageid)) {
            // remove file
            try {
                Files.delete(new File(imagePath).toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //
            int modelIndex = tableImage.convertRowIndexToModel(rowIdx); // converts the row index in the view to the appropriate index in the model
            CRDefaultTableModel model = (CRDefaultTableModel)tableImage.getModel();
            model.removeRow(modelIndex);
        }
    }

    private FFmpegFrameGrabber frameGrabber = null;

    private Thread curThread = null;
    private boolean playingVideo = false;
    private int videoFrameCurrentPosition = 0;
    private boolean autoExtractAll = true;

    private void stopVideo() {
        if (!playingVideo) return;
        //
        if (frameGrabber != null) {
            try {
                if (playingVideo) {
                    playingVideo = false;
                    try {
                        curThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                frameGrabber.stop();
                frameGrabber.release();
                frameGrabber = null;
            } catch (FFmpegFrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }
        //
        SwingUtilities.invokeLater(() -> {
            onStoppedVideo();
        });
    }
    private void onStoppedVideo() {
        btnPlay.setText("Play video");
        btnSave.setEnabled(false);
        btnSaveAll.setEnabled(true);
        autoExtractAll = false;
    }
    private void playVideo(String videoPath) {
        if (playingVideo) return; // do nothing
        //
        stopVideo();
        //
        curThread = new Thread(() -> {
            try {
                frameGrabber = new FFmpegFrameGrabber(videoPath);
                frameGrabber.start();
                playingVideo = true;
                double totalWaitFrame = 0;
                double waitFrame = 1000 / frameGrabber.getVideoFrameRate();
                videoFrameCurrentPosition = 0;
                while (playingVideo) {
                    Frame frame = frameGrabber.grabImage();
                    BufferedImage bi = new Java2DFrameConverter().convert(frame);
                    if (bi == null) break;
                    SwingUtilities.invokeLater(() -> {
                        pictureBox.setImage(bi);
                    });
                    try {
                        if (!autoExtractAll) {
                            Thread.sleep((int) waitFrame);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    totalWaitFrame += waitFrame;
                    if (totalWaitFrame > 1000) {
                        totalWaitFrame = 0;
                        if (autoExtractAll) {
                            saveImage(bi);
                        }
                    }
                    videoFrameCurrentPosition++;
                }
                SwingUtilities.invokeLater(() -> {
                    stopVideo();
                });
            } catch (FFmpegFrameGrabber.Exception e) {
                e.printStackTrace();
            }
        });
        curThread.start();
        SwingUtilities.invokeLater(() -> {
            onPlayingVideo();
        });
    }
    private void onPlayingVideo() {
        btnPlay.setText("Stop video");
        btnSave.setEnabled(true);
        btnSaveAll.setEnabled(false);
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

    private void saveImage(BufferedImage bi) {
        BufferedImage img = bi;
        if (bi == null)
            img = pictureBox.getImage();
        CRImage newImage = owner.getAppDBService().addImageToProject(owner.getProject(),
                curVideoPath, videoFrameCurrentPosition);
        if (newImage != null) {
            curImages.add(newImage);
            ((CRDefaultTableModel)tableImage.getModel()).addRow(new Object[] {
                    newImage.getId(),
                    newImage.getImagePath()
            });
            //
            //img = scaleImage(img);
            try {
                ImageIO.write(img, "png", new File(newImage.getImagePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void extractAllFrames() {
        autoExtractAll = true;
        playVideo(curVideoPath);
    }

    private BufferedImage scaleImage(BufferedImage src) {
        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage dest = new BufferedImage(owner.getProject().getFrameDestinationSize().getWidth(),
                owner.getProject().getFrameDestinationSize().getHeight(), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(owner.getProject().getFrameDestinationSize().getWidth() * 1.0 / w,
                owner.getProject().getFrameDestinationSize().getHeight() * 1.0 / h);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        dest = scaleOp.filter(src, dest);
        return dest;
    }
}
