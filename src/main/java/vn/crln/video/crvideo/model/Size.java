package vn.crln.video.crvideo.model;

public class Size {
    private Integer width;
    private Integer height;

    public Integer getWidth() {
        return width;
    }

    public Size setWidth(Integer width) {
        this.width = width;
        return this;
    }

    public Integer getHeight() {
        return height;
    }

    public Size setHeight(Integer height) {
        this.height = height;
        return this;
    }

    public Size(Integer width, Integer height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Size)) return false;
        Size s = (Size)obj;
        return width != null && height != null && width.equals(s.width) && height.equals(s.height);
    }
}
