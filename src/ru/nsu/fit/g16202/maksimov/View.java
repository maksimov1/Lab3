package ru.nsu.fit.g16202.maksimov;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class View {
    JFrame mainframe;
    FieldPanel fieldpanel;
    ImPanel legendpanel;
    BufferedImage legend;


    public View(){
        mainframe = new JFrame();
        mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fieldpanel = new FieldPanel();
        legendpanel = new ImPanel();
        fieldpanel.setPreferredSize(new Dimension(600,400));
        legendpanel.setPreferredSize(new Dimension(500, 40));
        legend = new BufferedImage(600, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = legend.createGraphics();
        graphics.setPaint ( new Color ( 255, 255, 255 ) );
        graphics.fillRect ( 0, 0, legend.getWidth(), legend.getHeight());
        graphics.setColor(Color.black);
        legendpanel.setCanvas(legend);
        //fieldpanel.setBackground(Color.CYAN);
        //legendpanel.setBackground(Color.BLUE);
        mainframe.setLayout(new BorderLayout());
        mainframe.add(fieldpanel, BorderLayout.CENTER);
        mainframe.add(legendpanel, BorderLayout.SOUTH);
        mainframe.pack();
    }

    public void showFileError(){
        JOptionPane.showMessageDialog(null, "Wrong file format", "ERROR", JOptionPane.ERROR_MESSAGE);
    }//


    public void setVisible(boolean b){
        mainframe.setVisible(b);
    }

    public void repaint(){
        legendpanel.repaint();
        fieldpanel.repaint();
    }
    public BufferedImage getLegend(){
        return legend;
    }

    public void setField(BufferedImage _field) {
        fieldpanel.setField(_field);
    }
    //


    public BufferedImage getField() {
        return fieldpanel.getField();
    }
}
