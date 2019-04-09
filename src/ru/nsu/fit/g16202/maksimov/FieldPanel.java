package ru.nsu.fit.g16202.maksimov;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FieldPanel extends JPanel {
    private BufferedImage field;
    private  BufferedImage grid;
    private  boolean showGrid;
    private BufferedImage isolines;
    private boolean showIsolines;
    public FieldPanel(){
        super();
        showGrid = false;
    }

    public void setField(BufferedImage field) {
        this.field = field;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public void setGrid(BufferedImage grid) {
        this.grid = grid;
    }

    public BufferedImage getField() {
        return field;
    }

    public void setIsolines(BufferedImage isolines) {
        this.isolines = isolines;
    }

    public void setShowIsolines(boolean showIsolines) {
        this.showIsolines = showIsolines;
    }

    public boolean isShowIsolines() {
        return showIsolines;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (field != null){
            g.drawImage(field, 0, 0, field.getWidth(), field.getHeight(), this);
        }
        if ((grid != null) && showGrid){
            g.drawImage(grid, 0, 0, grid.getWidth(), grid.getHeight(), this);
        }
        if ((isolines != null) && showIsolines){
            g.drawImage(isolines, 0, 0, isolines.getWidth(), isolines.getHeight(), this);
        }
    }
}
