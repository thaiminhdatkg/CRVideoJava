package vn.crln.video.crvideo.model;

import java.awt.*;

public class Bound {
    private Integer x1;
    private Integer y1;
    private Integer x2;
    private Integer y2;

    public Integer getX1() {
        return x1;
    }

    public Bound setX1(Integer x1) {
        this.x1 = x1;
        return this;
    }

    public Integer getY1() {
        return y1;
    }

    public Bound setY1(Integer y1) {
        this.y1 = y1;
        return this;
    }

    public Integer getX2() {
        return x2;
    }

    public Bound setX2(Integer x2) {
        this.x2 = x2;
        return this;
    }

    public Integer getY2() {
        return y2;
    }

    public Bound setY2(Integer y2) {
        this.y2 = y2;
        return this;
    }

    public Bound addX1(Integer dx) {
        x1 += dx;
        return this;
    }
    public Bound addY1(Integer dy) {
        y1 += dy;
        return this;
    }
    public Bound addX2(Integer dx) {
        x2 += dx;
        return this;
    }
    public Bound addY2(Integer dy) {
        y2 += dy;
        return this;
    }

    public int getWidth() {
        return x2 - x1 + 1;
    }
    public int getHeight() {
        return y2 - y1 + 1;
    }

    public Bound(Bound src) {
        this.x1 = src.x1;
        this.y1 = src.y1;
        this.x2 = src.x2;
        this.y2 = src.y2;
    }

    public Bound(Integer x1, Integer y1, Integer x2, Integer y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Bound(Point p, Size size) {
        this.x1 = p.x;
        this.y1 = p.y;
        this.x2 = p.x + size.getWidth() - 1;
        this.y2 = p.y + size.getHeight() - 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Bound)) return false;
        Bound b = (Bound)obj;
        return x1 != null && y1 != null && x2 != null && y2 != null
                && x1.equals(b.x1) && y1.equals(b.y1) && x2.equals(b.x2) && y2.equals(b.y2);
    }

    public boolean contains(int x, int y) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }
    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }
    public void translate(int dx, int dy) {
        x1 += dx; y1 += dy;
        x2 += dx; y2 += dy;
    }
}
