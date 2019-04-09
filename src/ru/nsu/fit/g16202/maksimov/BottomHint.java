package ru.nsu.fit.g16202.maksimov;

import javax.swing.*;
import java.awt.*;

public class BottomHint extends JPanel {
    private JLabel hinttext;
    public BottomHint(){
        super();
        hinttext = new JLabel();
        hinttext.setPreferredSize(new Dimension(400, 20));
        this.setLayout(new BorderLayout());
        this.setPreferredSize(hinttext.getPreferredSize());
        this.add(hinttext, BorderLayout.NORTH);
    }

    public void setHinttext(String txt){
        if (txt == ""){
            //this.setVisible(false);
            hinttext.setText("");
            return;
        }
        hinttext.setText(txt);
        this.repaint();
        this.setVisible(true);
    }
}
