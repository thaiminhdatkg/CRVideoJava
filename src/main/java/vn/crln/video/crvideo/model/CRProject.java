package vn.crln.video.crvideo.model;

import java.util.Date;

public class CRProject {
    private Integer id;
    private String name ;
    private String description;
    private String videoDirectory;
    private String frameDirectory;
    private Size frameDestinationSize;
    private Date createdDate;
    private Date modifyDate;

    public Integer getId() {
        return id;
    }

    public CRProject setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CRProject setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CRProject setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getVideoDirectory() {
        return videoDirectory;
    }

    public CRProject setVideoDirectory(String videoDirectory) {
        this.videoDirectory = videoDirectory;
        return this;
    }

    public String getFrameDirectory() {
        return frameDirectory;
    }

    public CRProject setFrameDirectory(String frameDirectory) {
        this.frameDirectory = frameDirectory;
        return this;
    }

    public Size getFrameDestinationSize() {
        return frameDestinationSize;
    }

    public CRProject setFrameDestinationSize(Size frameDestinationSize) {
        this.frameDestinationSize = frameDestinationSize;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public CRProject setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public CRProject setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
        return this;
    }
}
