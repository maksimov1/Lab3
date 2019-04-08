package ru.nsu.fit.g16202.maksimov;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FieldPanel extends JPanel {
    private BufferedImage field;
    public FieldPanel(){
        super();
    }

    public void setField(BufferedImage field) {
        this.field = field;
    }

    public BufferedImage getField() {
        return field;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (field != null){
            g.drawImage(field, 0, 0, field.getWidth(), field.getHeight(), this);
        }
    }
}
