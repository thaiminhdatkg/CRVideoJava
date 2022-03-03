package vn.crln.video.crvideo.control;

import javafx.scene.input.KeyCode;
import vn.crln.video.crvideo.model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CRLabelImage extends JPanel implements MouseListener, MouseMotionListener {
    private CRImage curImage = null;
    private BufferedImage image = null;
    private Point drawImagePosition;
    private Size drawImageSize;
    private double currentScaleRatio;
    private CRLabel currentLabel;
    // draw
    private MouseDowningType mouseDowning = MouseDowningType.None;
    private Point lastPointDown = null;
    private CRBoundingBox drawingBoundingBox = null;
    private CRBoundingBox selectingBoundingBox = null;
    private CornerType currentCornerType = CornerType.None;
    //
    private static final Font font = new Font("Arial", Font.BOLD, 8);
    //
    private java.util.List<CRBoundingBox> boundingBoxes = new ArrayList<>();

    public java.util.List<CRBoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }
    public void setBoundingBoxes(java.util.List<CRBoundingBox> boundingBoxes) {
        this.boundingBoxes = boundingBoxes;
        repaint();
    }

    private static class CursorUsed {
        public static final Cursor Default = new Cursor(Cursor.DEFAULT_CURSOR);
        public static final Cursor Cross = new Cursor(Cursor.CROSSHAIR_CURSOR);
        public static class Resize {
            public static final Cursor LT2RB = new Cursor(Cursor.NW_RESIZE_CURSOR);
            public static final Cursor RT2LB = new Cursor(Cursor.NE_RESIZE_CURSOR);
            public static final Cursor LB2RT = new Cursor(Cursor.SW_RESIZE_CURSOR);
            public static final Cursor RB2LT = new Cursor(Cursor.SE_RESIZE_CURSOR);
            public static final Cursor T2B = new Cursor(Cursor.N_RESIZE_CURSOR);
            public static final Cursor B2T = T2B;
            public static final Cursor L2R = new Cursor(Cursor.W_RESIZE_CURSOR);
            public static final Cursor R2L = L2R;
        }
        public static final Cursor MoveAll = new Cursor(Cursor.MOVE_CURSOR);
    }

    public CRLabelImage() {
        setBackground(Color.BLACK);
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
        addMouseListener(this);
        addMouseMotionListener(this);
        //
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "escape-input");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true), "delete-input");
        //
        ActionMap actionMap = getActionMap();
        actionMap.put("escape-input", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onKeyEscapeReleased();
            }
        });
        actionMap.put("delete-input", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onKeyDeleteReleased();
            }
        });
        //
        recomputeImageSize();
    }

    public void setImage(CRImage image) {
        curImage = image;
        loadImage();
        recomputeImageSize();
        repaint();
    }

    private void loadImage() {
        if (curImage != null) {
            image = null;
            try {
                image = ImageIO.read(new File(curImage.getImagePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            image = null;
        }
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

    private void drawBoundingBoxOne(Graphics g, CRBoundingBox boundingBox, boolean absolutePosition,
                                    boolean applyscale, boolean background) {
        Bound boundingBound = new Bound(boundingBox.getBound());
        if (applyscale) {
            boundingBound = scaleBoundToScreen(boundingBound);
        }
        if (!absolutePosition) {
            boundingBound.translate(drawImagePosition.x, drawImagePosition.y);
        }

        Color color = boundingBox.getLabel().getColor();
        if (background) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
            g.fillRect(boundingBound.getX1(), boundingBound.getY1(), boundingBound.getWidth(), boundingBound.getHeight());
        }
        g.setColor(boundingBox.getLabel().getColor());
        g.drawRect(boundingBound.getX1(), boundingBound.getY1(), boundingBound.getWidth(), boundingBound.getHeight());
        //

        g.setFont(font);
        int titleWidth = g.getFontMetrics().stringWidth(boundingBox.getLabel().getName());
        int titleHeight = g.getFontMetrics().getHeight();
        // name
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 150));
        g.fillRect(boundingBound.getX1(), (int)(boundingBound.getY1() - titleHeight - 10),
                (int)(titleWidth + 10), (int)(titleHeight + 10));
        g.setColor(Color.BLACK);
        g.drawString(boundingBox.getLabel().getName(), boundingBound.getX1() + 5,
                boundingBound.getY1() - 5);
    }
    private void drawBoundingBoxes(Graphics g) {
        for (CRBoundingBox bb : boundingBoxes) {
            drawBoundingBoxOne(g, bb, false, true, bb == selectingBoundingBox);
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
            //
            // draw boundingBoxes
            drawBoundingBoxes(g);
            //
            if (drawingBoundingBox != null)
            {
                drawBoundingBoxOne(g, drawingBoundingBox, true, false, true);
            }
        }
    }

    public void setCurrentLabel(CRLabel label) {
        currentLabel = label;
        //
        clearSelectingBoundingBox();
        //
        if (currentLabel != null)
            setCursor(CursorUsed.Cross);
        else
            setCursor(CursorUsed.Default);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (currentLabel != null)
        {
            if (IsPointInImage(e.getPoint()))
            {
                lastPointDown = e.getPoint();
                mouseDowning = MouseDowningType.DrawingBoundingBox;
                drawingBoundingBox = new CRBoundingBox()
                    .setLabel(currentLabel)
                    .setBound(new Bound(lastPointDown, new Size(1, 1)));
            }
        } else if (selectingBoundingBox != null && currentCornerType != CornerType.None)
        {
            lastPointDown = e.getPoint();
            mouseDowning = MouseDowningType.ResizingBoundingBox;
        }
        else
        {
            CRBoundingBox bb = findHitBoundingBox(new Point(e.getPoint().x - drawImagePosition.x, e.getPoint().y - drawImagePosition.y));
            if (bb != null)
            {
                selectingBoundingBox = bb;
                mouseDowning = MouseDowningType.SelectingBoundingBox;
                repaint();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (mouseDowning != MouseDowningType.None)
        {
            if (mouseDowning == MouseDowningType.DrawingBoundingBox)
            {
                if (drawingBoundingBox != null)
                {
                    addNewBoundingBox();
                }
                clearDrawingBoundingBox(false);
            } else if (mouseDowning == MouseDowningType.ResizingBoundingBox)
            {
                clearResizingBoundingBox();
                updatedAllBoundingBoxToDB();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseDowning == MouseDowningType.DrawingBoundingBox)
        {
            Point p2 = e.getPoint();
            p2.x = Math.max(Math.min(p2.x, drawImagePosition.x + drawImageSize.getWidth() - 1), 0);
            p2.y = Math.max(Math.min(p2.y, drawImagePosition.y + drawImageSize.getHeight() - 1), 0);
            //
            Point p1 = lastPointDown;
            //
            Bound r = boundFrom2Points(p1, p2);
            drawingBoundingBox.setBound(r);
            repaint();
        } else if (mouseDowning == MouseDowningType.ResizingBoundingBox && selectingBoundingBox != null)
        {
            if (currentCornerType != CornerType.None)
            {
                int x = scaleScreenToBoundXY(e.getPoint().x - lastPointDown.x);
                int y = scaleScreenToBoundXY(e.getPoint().y - lastPointDown.y);
                lastPointDown = e.getPoint();
                switch (currentCornerType)
                {
                    case TopLeft:
                        selectingBoundingBox.getBound().addX1(x).addY1(y);
                        break;
                    case TopRight:
                        selectingBoundingBox.getBound().addY1(y).addX2(x);
                        break;
                    case BottomLeft:
                        selectingBoundingBox.getBound().addX1(x).addY2(y);
                        break;
                    case BottomRight:
                        selectingBoundingBox.getBound().addX2(x).addY2(y);
                        break;
                    case TopEdge:
                        selectingBoundingBox.getBound().addY1(y);
                        break;
                    case BottomEdge:
                        selectingBoundingBox.getBound().addY2(y);
                        break;
                    case LeftEdge:
                        selectingBoundingBox.getBound().addX1(x);
                        break;
                    case RightEdge:
                        selectingBoundingBox.getBound().addX2(x);
                        break;
                    case Inside:
                        selectingBoundingBox.getBound().addX1(x).addY1(y).addX2(x).addY2(y);
                        break;
                }
                repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouseDowning == MouseDowningType.SelectingBoundingBox && selectingBoundingBox != null)
        {
            currentCornerType = mousePointToCorner(new Point(e.getPoint().x - drawImagePosition.x, e.getPoint().y - drawImagePosition.y), selectingBoundingBox);
            switch (currentCornerType)
            {
                case TopLeft:
                    setCursor(CursorUsed.Resize.LT2RB);
                    break;
                case TopRight:
                    setCursor(CursorUsed.Resize.RT2LB);
                    break;
                case BottomLeft:
                    setCursor(CursorUsed.Resize.LB2RT);
                    break;
                case BottomRight:
                    setCursor(CursorUsed.Resize.RB2LT);
                    break;
                case TopEdge:
                    setCursor(CursorUsed.Resize.T2B);
                    break;
                case BottomEdge:
                    setCursor(CursorUsed.Resize.B2T);
                    break;
                case LeftEdge:
                    setCursor(CursorUsed.Resize.L2R);
                    break;
                case RightEdge:
                    setCursor(CursorUsed.Resize.R2L);
                    break;
                case Inside:
                    setCursor(CursorUsed.MoveAll);
                    break;
                case None:
                default:
                    setCursor(CursorUsed.Default);
                    break;
            }
        }
    }

    private void onKeyEscapeReleased() {
        if (currentLabel != null)
        {
            currentLabel = null;
            setCursor(CursorUsed.Default);
        }
        if (mouseDowning == MouseDowningType.DrawingBoundingBox)
        {
            clearDrawingBoundingBox(true);
        }
        if (mouseDowning == MouseDowningType.SelectingBoundingBox || mouseDowning == MouseDowningType.ResizingBoundingBox)
        {
            clearSelectingBoundingBox();
        }
    }
    private void onKeyDeleteReleased() {
        if (mouseDowning == MouseDowningType.DrawingBoundingBox && drawingBoundingBox != null)
        {
            clearDrawingBoundingBox(false);
        } else if (mouseDowning == MouseDowningType.SelectingBoundingBox && selectingBoundingBox != null)
        {
            deleteSelectingBoundingBox();
        }
    }

    private boolean IsPointInImage(Point p) {
        return new Bound(drawImagePosition, drawImageSize).contains(p.x, p.y);
    }

    private CRBoundingBox findHitBoundingBox(Point p) {
        for (CRBoundingBox bb : boundingBoxes) {
            if (scaleBoundToScreen(bb.getBound()).contains(p)) {
                return bb;
            }
        }
        return null;
    }

    private Bound boundFrom2Points(Point p1, Point p2) {
        return new Bound(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y),
                Math.max(p1.x, p2.x), Math.max(p1.y, p2.y));
    }

    private CornerType mousePointToCorner(Point p, CRBoundingBox boundingBox) {
        Bound rectBound = scaleBoundToScreen(boundingBox.getBound());
        int rangeAccept = 4;
        boolean insideLeft = false;
        boolean insideRight = false;
        boolean insideTop = false;
        boolean insideBottom = false;
        boolean insideRectange = false;
        insideLeft = Math.abs(p.x - rectBound.getX1()) <= rangeAccept;
        insideRight = Math.abs(p.x - rectBound.getX2()) <= rangeAccept;
        insideTop = Math.abs(p.y - rectBound.getY1()) <= rangeAccept;
        insideBottom = Math.abs(p.y - rectBound.getY2()) <= rangeAccept;
        insideRectange = new Bound(rectBound.getX1() - rangeAccept, rectBound.getY1() - rangeAccept,
                rectBound.getX2() + rangeAccept, rectBound.getY2() + rangeAccept).contains(p.x, p.y);
        if (insideRectange)
        {
            if (insideLeft)
            {
                if (insideTop) return CornerType.TopLeft;
                if (insideBottom) return CornerType.BottomLeft;
                return CornerType.LeftEdge;
            }
            if (insideRight)
            {
                if (insideTop) return CornerType.TopRight;
                if (insideBottom) return CornerType.BottomRight;
                return CornerType.RightEdge;
            }
            if (insideTop) return CornerType.TopEdge;
            if (insideBottom) return CornerType.BottomEdge;
            return CornerType.Inside;
        }
        return CornerType.None;
    }

    private void clearResizingBoundingBox() {
        mouseDowning = MouseDowningType.SelectingBoundingBox;
        lastPointDown = null;
        setCursor(CursorUsed.Default);
        repaint();
    }
    private void clearDrawingBoundingBox(boolean resetDrawing) {
        mouseDowning = MouseDowningType.None;
        lastPointDown = null;
        drawingBoundingBox = null;
        if (resetDrawing) {
            setCurrentLabel(null);
        }
        repaint();
    }
    private void clearSelectingBoundingBox() {
        mouseDowning = MouseDowningType.None;
        lastPointDown = null;
        selectingBoundingBox = null;
        setCursor(CursorUsed.Default);
        repaint();
    }

    public void deleteSelectingBoundingBox() {
        if (selectingBoundingBox != null)
        {
            boundingBoxes.remove(selectingBoundingBox);
            selectingBoundingBox = null;
            //
            repaint();
            updatedAllBoundingBoxToDB();
        }
    }

    public void updatedAllBoundingBoxToDB() {
        for (LabelImageDoneListener listener : labelImageDoneListeners) {
            if (listener != null) listener.labelImageDone(curImage.getProject(), curImage, boundingBoxes);
        }
    }

    private Bound scaleScreenToBound(Bound src)
    {
        return new Bound(
                (int)Math.round(src.getX1() / currentScaleRatio),
                (int)Math.round(src.getY1() / currentScaleRatio),
                (int)Math.round(src.getX2() / currentScaleRatio),
                (int)Math.round(src.getY2() / currentScaleRatio)
        );
    }
    private Bound scaleBoundToScreen(Bound src)
    {
        return new Bound(
                (int)Math.round(src.getX1() * currentScaleRatio),
                (int)Math.round(src.getY1() * currentScaleRatio),
                (int)Math.round(src.getX2() * currentScaleRatio),
                (int)Math.round(src.getY2() * currentScaleRatio)
        );
    }
    private int scaleScreenToBoundXY(int XorY)
    {
        return (int)Math.round(XorY / currentScaleRatio);
    }
    private int scaleBoundToScreenXY(int XorY)
    {
        return (int)Math.round(XorY * currentScaleRatio);
    }

    public void addNewBoundingBox()
    {
        drawingBoundingBox.setBound(scaleScreenToBound(new Bound(
                drawingBoundingBox.getBound().getX1() - drawImagePosition.x,
                drawingBoundingBox.getBound().getY1() - drawImagePosition.y,
                drawingBoundingBox.getBound().getX2() - drawImagePosition.x,
                drawingBoundingBox.getBound().getY2() - drawImagePosition.y
        )));
        boundingBoxes.add(drawingBoundingBox);
        //
        repaint();
        //
        updatedAllBoundingBoxToDB();
    }

    public enum MouseDowningType {
        None,
        DrawingBoundingBox,
        ResizingBoundingBox,
        SelectingBoundingBox
    }
    public enum CornerType
    {
        None, TopLeft, TopRight, BottomLeft, BottomRight,
        TopEdge, BottomEdge, LeftEdge, RightEdge,
        Inside
    }

    public interface LabelImageDoneListener{
        void labelImageDone(CRProject project, CRImage image, java.util.List<CRBoundingBox> boundingBoxes);
    }
    private java.util.List<LabelImageDoneListener> labelImageDoneListeners = new ArrayList<>();
    public void addLabelImageDoneListener(LabelImageDoneListener listener) {
        labelImageDoneListeners.add(listener);
    }
    public void removeLabelImageDoneListener(LabelImageDoneListener listener) {
        labelImageDoneListeners.remove(listener);
    }
}
