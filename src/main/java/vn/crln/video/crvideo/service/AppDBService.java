package vn.crln.video.crvideo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.crln.video.crvideo.model.CRBoundingBox;
import vn.crln.video.crvideo.model.CRImage;
import vn.crln.video.crvideo.model.CRLabel;
import vn.crln.video.crvideo.model.CRProject;
import vn.crln.video.crvideo.service.data.Util;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppDBService {
    @Autowired
    DatabaseService databaseService;

    public List<CRProject> loadAllProjects() {
        List<CRProject> retList = new ArrayList<>();
        for (Map map : databaseService.queryList("select * from project")) {
            CRProject project = new CRProject()
                    .setId(Util.Data.getInt(map, "Id"))
                    .setName(Util.Data.getString(map, "Name"))
                    .setDescription(Util.Data.getString(map, "Description"))
                    .setFrameDestinationSize(Util.Data.getSize(map, "FrameWidth", "FrameHeight"))
                    .setCreatedDate(Util.Data.getDate(map, "CreatedDate"))
                    .setModifyDate(Util.Data.getDate(map, "ModifyDate"))
                    .setVideoDirectory(Util.Data.getString(map, "VideoPath"))
                    .setFrameDirectory(Util.Data.getString(map, "ImagePath"));
            retList.add(project);
        }
        return retList;
    }

    public boolean saveProject(CRProject project) {
        String sql = "update project set name=?, description=?, " +
                "videopath=?, imagepath=?, modifydate=strftime('%d/%m/%Y %H:%M:%S','now')," +
                "FrameWidth=?, FrameHeight=? " +
                "where id=?";
        return databaseService.update(sql, project.getName(), project.getDescription(),
                project.getVideoDirectory(), project.getFrameDirectory(),
                project.getFrameDestinationSize().getWidth(),
                project.getFrameDestinationSize().getHeight(),
                project.getId()) > 0;
    }

    public boolean addProject(String name, String description,
                              String videoPath, String imagePath, Long frameWidth, Long frameHeight) {
        String sql = "insert into project (id, name, description, createddate, modifydate, videopath, imagepath, framewidth, frameheight) " +
                "values ((select ifnull(max(id), 0) + 1 from project), ?,?," +
                "strftime('%d/%m/%Y %H:%M:%S','now'),strftime('%d/%m/%Y %H:%M:%S','now'),?,?,?,?)";
        return databaseService.update(sql, name, description, videoPath, imagePath, frameWidth, frameHeight) > 0;
    }

    public List<CRLabel> loadLabelOfProjects(long projectid) {
        String sql = "select Id, Color, Name, Description from object where projectid=?";
        List<CRLabel> retList = new ArrayList<>();
        for (Map map : databaseService.queryList(sql, projectid)) {
            retList.add(new CRLabel()
                    .setId(Util.Data.getInt(map, "Id"))
                    .setName(Util.Data.getString(map, "Name"))
                    .setDescription(Util.Data.getString(map, "Description"))
                    .setColor(new Color((int)(long)Util.Data.getInt(map, "Color")))
            );
        }
        return retList;
    }

    public boolean addLabelToProject(CRProject project, String name, String description, Color color) {
        String sql = "insert into object (projectid, id, name, description, color)\n" +
                "values (?, ifnull((select max(id) from object where projectid=?), 0) + 1,\n" +
                "?, ?, ?)";
        return databaseService.update(sql, project.getId(), project.getId(), name, description, color.getRGB()) > 0;
    }

    public CRImage addImageToProject(CRProject project, String videopath, Integer framePosition) {
        int newImageId = databaseService.query(Integer.class, "select ifnull(max(imageid), 0) + 1 as imageid from image where projectid=?",
                project.getId());
        String imagePath = Paths.get(project.getFrameDirectory(),
                String.format("image_%d_%d.jpg", project.getId(), newImageId)).toAbsolutePath().toString();
        if (databaseService.update("insert into image (projectid, imageid, path, labeled, videopath, frameposition)\n" +
                "values (?, ?, ?, ?, ?, ?)",
                project.getId(), newImageId, imagePath, 0, videopath, framePosition) > 0) {
            return new CRImage()
                    .setId(newImageId)
                    .setProject(project)
                    .setVideoPath(videopath)
                    .setImagePath(imagePath)
                    .setLabeled(false)
                    .setFramePosition(framePosition);
        } else {
            return null;
        }
    }
    public boolean deleteImageFromProject(CRProject project, String videoPath, int imageId) {
        if (databaseService.update("delete from image where ProjectId = ? and ImageId = ?", project.getId(), imageId) > 0) {
            databaseService.update("delete from boundingboxlabel where projectid = ? and imageid = ?", project.getId(), imageId);
            return true;
        }
        return false;
    }

    public List<CRImage> loadImageOfVideo(CRProject project, String videopath) {
        String sql = "select * from image where projectid=? and videopath=?";
        List<Map> all = databaseService.queryList(sql, project.getId(), videopath);
        List<CRImage> retList = new ArrayList<>();
        for (Map map : all) {
            retList.add(new CRImage()
                    .setId(Util.Data.getInt(map, "ImageId"))
                    .setVideoPath(videopath)
                    .setImagePath(Util.Data.getString(map, "Path"))
                    .setLabeled(Util.Data.getBool(map, "labeled"))
                    .setProject(project)
                    .setFramePosition(Util.Data.getInt(map, "FramePosition"))
            );
        }
        return retList;
    }

    public List<CRBoundingBox> loadBoundingBoxOfImage(
            List<CRLabel> labelList,  CRProject project, CRImage image) {
        Map<Integer, CRLabel> labelMap = new HashMap<>();
        for (CRLabel label : labelList) {
            labelMap.put(label.getId(), label);
        }
        //
        String sql = "select * from boundingboxlabel where projectid=? and imageid=?";
        List<Map> all = databaseService.queryList(sql, project.getId(), image.getId());
        List<CRBoundingBox> retList = new ArrayList<>();
        for (Map map : all) {
            retList.add(new CRBoundingBox()
                    .setLabel(labelMap.get(Util.Data.getInt(map, "objectid")))
                    .setBound(Util.Data.getBound(map, "x1", "y1", "x2", "y2"))
            );
        }
        return retList;
    }

    public boolean clearAllBoundingBoxes(CRProject project, CRImage image) {
        return databaseService.update("delete from boundingboxlabel where projectid=? and imageid=?",
                project.getId(),
                image.getId()) > 0;
    }

    public boolean addBoundingBox(CRProject project, CRImage image, CRBoundingBox boundingBox) {
        String sql = "insert into boundingboxlabel (projectid, imageid, objectid, x1, y1, x2, y2)\n" +
                "values (?, ?, ?, ?, ?, ?, ?)";
        return databaseService.update(sql, project.getId(), image.getId(),
                boundingBox.getLabel().getId(),
                boundingBox.getBound().getX1(),
                boundingBox.getBound().getY1(),
                boundingBox.getBound().getX2(),
                boundingBox.getBound().getY2()) > 0;
    }

    public boolean saveBoundingBoxes(CRProject project, CRImage image, List<CRBoundingBox> boundingBoxes) {
        // clear all boundingbox of image
        clearAllBoundingBoxes(project, image);
        // add new all
        for (CRBoundingBox boundingBox : boundingBoxes) {
            addBoundingBox(project, image, boundingBox);
        }
        return true;
    }
}
