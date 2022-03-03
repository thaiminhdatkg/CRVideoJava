package vn.crln.video.crvideo.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.crln.video.crvideo.control.CRDefaultTableModel;
import vn.crln.video.crvideo.model.CRProject;
import vn.crln.video.crvideo.service.AppDBService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

@Component
public class OpenProjectFrame extends JDialog {
    @Autowired
    AppDBService appDBService;

    private JTable table;

    public OpenProjectFrame() {
        //
        //
        table = new JTable();
        table.setFillsViewportHeight(true);
        setContentPane(new JScrollPane(table));
        //
        table.setModel(new CRDefaultTableModel());
        ((CRDefaultTableModel)table.getModel()).setDefaultEditable(false);
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int row = table.rowAtPoint(point);
                if (e.getClickCount() == 2 && row != -1) {
                    selectAProject(row);
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
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.addColumn("Name");
        model.addColumn("Description");
        //
        setSize(300, 300);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        pack();
        //
        setModal(true);
        //
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                // load list
                showListProjects();
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

    private java.util.List<CRProject> curProjects = new ArrayList<>();

    private void showListProjects() {
        curProjects = appDBService.loadAllProjects();
        selectedProject = null;
        DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
        tableModel.setRowCount(0);
        for (CRProject project : curProjects) {
            tableModel.addRow(new Object[] { project.getName(), project.getDescription() });
        }
    }

    private CRProject selectedProject = null;

    public CRProject getSelectedProject() {
        return selectedProject;
    }

    private void selectAProject(int row) {
        selectedProject = curProjects.get(row);
        for (OpenProjectListener listener : openProjectListeners) {
            listener.selectedProject(selectedProject);
        }
        setVisible(false);
    }

    private java.util.List<OpenProjectListener> openProjectListeners = new ArrayList<>();

    public void addOpenProjectListener(OpenProjectListener listener) {
        openProjectListeners.add(listener);
    }
    public void removeOpenProjectListener(OpenProjectListener listener) {
        openProjectListeners.remove(listener);
    }

    public interface OpenProjectListener {
        void selectedProject(CRProject project);
    }
}
