package vn.crln.video.crvideo.model;

import java.awt.*;

public class CRLabel {
    private Integer id;
    private String name;
    private String description;
    private Color color;

    public Integer getId() {
        return id;
    }

    public CRLabel setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CRLabel setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CRLabel setDescription(String description) {
        this.description = description;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public CRLabel setColor(Color color) {
        this.color = color;
        return this;
    }
}
