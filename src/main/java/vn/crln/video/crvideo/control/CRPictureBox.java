package vn.crln.video.crvideo.control;

import vn.crln.video.crvideo.model.Size;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

public class CRPictureBox extends JPanel {
    private BufferedImage image;
    private Point drawImagePosition;
    private Size drawImageSize;
    private double currentScaleRatio;

    public CRPictureBox() {
        setDoubleBuffered(true);
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                recomputeImageSize();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }

    public BufferedImage getImage() {
        return image;
    }
    public void setImage(BufferedImage image) {
        this.image = image;
        recomputeImageSize();
        repaint();
    }

    private void recomputeImageSize() {
        if (image != null) {
            int tryNewHeight = (int)(image.getHeight() * (getSize().width * 1.0 / image.getWidth()));
            if (tryNewHeight <= getSize().height)
            {
                drawImageSize = new Size(getSize().width, tryNewHeight);
            }
            else
            {
                int tryNewWidth = (int)(image.getWidth() * (getSize().height * 1.0 / image.getHeight()));
                drawImageSize = new Size(tryNewWidth, getSize().height);
            }
            currentScaleRatio = drawImageSize.getWidth() * 1.0f / image.getWidth();
            drawImagePosition = new Point((getSize().width - drawImageSize.getWidth()) / 2,
                    (getSize().height - drawImageSize.getHeight()) / 2);
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        // draw image
        if (image != null && drawImagePosition != null && drawImageSize != null) {
            g.drawImage(image, drawImagePosition.x, drawImagePosition.y,
                    drawImageSize.getWidth(), drawImageSize.getHeight(), null);
        }
    }
}
