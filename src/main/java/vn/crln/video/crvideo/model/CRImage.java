package vn.crln.video.crvideo.model;

public class CRImage {
    private CRProject project;
    private Integer id;
    private String imagePath;
    private Boolean labeled;
    private String videoPath;
    private Integer framePosition;

    public CRProject getProject() {
        return project;
    }

    public CRImage setProject(CRProject project) {
        this.project = project;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public CRImage setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public CRImage setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public Boolean getLabeled() {
        return labeled;
    }

    public CRImage setLabeled(Boolean labeled) {
        this.labeled = labeled;
        return this;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public CRImage setVideoPath(String videoPath) {
        this.videoPath = videoPath;
        return this;
    }

    public Integer getFramePosition() {
        return framePosition;
    }

    public CRImage setFramePosition(Integer framePosition) {
        this.framePosition = framePosition;
        return this;
    }
}
