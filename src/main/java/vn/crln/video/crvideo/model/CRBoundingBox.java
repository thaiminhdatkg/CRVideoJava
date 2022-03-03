package vn.crln.video.crvideo.model;

public class CRBoundingBox {
    private CRLabel label;
    private Bound bound;

    public CRLabel getLabel() {
        return label;
    }

    public CRBoundingBox setLabel(CRLabel label) {
        this.label = label;
        return this;
    }

    public Bound getBound() {
        return bound;
    }

    public CRBoundingBox setBound(Bound bound) {
        this.bound = bound;
        return this;
    }
}
