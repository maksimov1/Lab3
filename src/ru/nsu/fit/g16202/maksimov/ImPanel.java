package ru.nsu.fit.g16202.maksimov;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImPanel extends JPanel {
    private BufferedImage canvas;

    public ImPanel(){
        super();
    }

    public void setCanvas(BufferedImage canvas) {
        this.canvas = canvas;


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvas != null){
            g.drawImage(canvas, 0, 0, canvas.getWidth(), canvas.getHeight(), this);
        }

    }
}
