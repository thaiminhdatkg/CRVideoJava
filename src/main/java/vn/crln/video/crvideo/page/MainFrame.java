package vn.crln.video.crvideo.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.crln.video.crvideo.model.CRProject;
import vn.crln.video.crvideo.page.view.ExtractFramePanel;
import vn.crln.video.crvideo.page.view.LabelPanel;
import vn.crln.video.crvideo.page.view.ProjectPanel;
import vn.crln.video.crvideo.service.AppDBService;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

@Component
public class MainFrame extends JFrame {
    @Autowired
    OpenProjectFrame openProjectFrame;
    @Autowired
    AppDBService appDBService;

    public AppDBService getAppDBService() {
        return appDBService;
    }

    private ProjectPanel projectPanel;
    private ExtractFramePanel extractFramePanel;
    private LabelPanel labelPanel;
    private CRProject project = null;

    public MainFrame() {
        initUI();
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                openProjectFrame.addOpenProjectListener(project1 -> onSelectProject(project1));
            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    private void initUI() {
        // menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);
        setJMenuBar(menuBar);
        //
        JMenuItem menuFileOpenProject = new JMenuItem("Open Project");
        menuFileOpenProject.addActionListener(e -> {
            openProjectFrame.setVisible(true);
        });
        menuFile.add(menuFileOpenProject);
        menuFile.addSeparator();
        JMenuItem menuFileNewProject = new JMenuItem("New Project");
        menuFile.add(menuFileNewProject);
        menuFile.addSeparator();
        JMenuItem menuFileExit = new JMenuItem("Exit");
        menuFileExit.addActionListener((e) -> {
            System.exit(0);
        });
        menuFile.add(menuFileExit);
        //
        JTabbedPane pane = new JTabbedPane();
        //
        projectPanel = new ProjectPanel().setOwner(this);
        labelPanel = new LabelPanel().setOwner(this);
        extractFramePanel = new ExtractFramePanel().setOwner(this);
        //
        setContentPane(pane);
        //
        setTitle("CRVideo");
        setSize(1366, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //
        //
//        try {
//            var frameGrabber = new FFmpegFrameGrabber("F:\\Frames\\NEW_HOME\\video_01_00_00_ch05.mpg");
//            frameGrabber.start();
//            var frame = frameGrabber.grabImage();
//            var bi = new Java2DFrameConverter().convert(frame);
//            ImageIO.write(bi, "jpg", new File("F:\\Frames\\IMAGES\\datdepzai.jpg"));
//            frameGrabber.stop();
//            frameGrabber.release();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public CRProject getProject() {
        return project;
    }

    void onSelectProject(CRProject project) {
        this.project = project;
        getContentPane().add("Project", projectPanel);
        getContentPane().add("Tách frame", extractFramePanel);
        getContentPane().add("Đánh nhãn", labelPanel);
        //
        projectPanel.updateProjectUI();
        extractFramePanel.updateProjectUI();
        labelPanel.updateProjectUI();
    }

    public void updateProjectInfo() {
        appDBService.saveProject(project);
    }
}
