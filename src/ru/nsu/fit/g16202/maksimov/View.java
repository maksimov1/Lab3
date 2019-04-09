package ru.nsu.fit.g16202.maksimov;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class View {
    private JFrame mainframe;
    private FieldPanel fieldpanel;
    private ImPanel legendpanel;
    private BufferedImage legend;
    private Controller controller;
    private BufferedImage isolines;
    private int dragx;
    private int dragy;
    private Color isoline;
    private JToolBar toolbar;
    private JToggleButton interesting;
    private  JToggleButton showisolines;
    private JToggleButton showgrid;
    private JToggleButton turnonspanfill;
    private BottomHint hint;

    public View(Controller _controller){
        controller = _controller;
        mainframe = new JFrame();
        mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fieldpanel = new FieldPanel();
        toolbar = new JToolBar();
        interesting = new JToggleButton(new ImageIcon("newgame.png"));
        interesting.setToolTipText("Turn On Blur");
        interesting.setPreferredSize(new Dimension(20,20));
        interesting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.changeInteresting();

            }
        });

        showisolines = new JToggleButton(new ImageIcon("isolines.png"));
        showisolines.setToolTipText("Show Isolines For Key Values");
        showisolines.setPreferredSize(new Dimension(20,20));
        showisolines.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.changeIsolines();

            }
        });

        showgrid = new JToggleButton(new ImageIcon("grid.png"));
        showgrid.setToolTipText("Show Grid");
        showgrid.setPreferredSize(new Dimension(20,20));
        showgrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.changeGrid();

            }
        });

        turnonspanfill = new JToggleButton(new ImageIcon("span.png"));
        turnonspanfill.setToolTipText("Use Span Fill to Generate Field");
        turnonspanfill.setPreferredSize(new Dimension(20,20));
        turnonspanfill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.swapSpan();

            }
        });

        toolbar.setFloatable(false);
        toolbar.setPreferredSize(new Dimension(200, 20));
        toolbar.add(interesting);
        toolbar.add(showisolines);
        toolbar.add(showgrid);
        toolbar.add(turnonspanfill);
        fieldpanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getX() + " " + e.getY());
                if (isolines != null && fieldpanel.isShowIsolines()){
                    if (isolines.getRGB(e.getX(), e.getY()) != controller.getIsoline().getRGB()){
                        controller.drawIsolines(controller.getValues().get(e.getY()).get(e.getX()), isolines);
                        //System.out.println("Click @ y" + e.getY() + " x" + e.getX());
                        repaint();
                    }else{
                        //System.out.println("Click on isoline");
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {
                hint.setHinttext("");

            }
        });
        fieldpanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if ((isolines != null) && fieldpanel.isShowIsolines() && (e.getX() < isolines.getWidth()) && (e.getY() < isolines.getHeight())
                && (e.getX()>=0) && (e.getY() >=0)){
                    if (isolines.getRGB(e.getX(), e.getY()) != controller.getIsoline().getRGB()){
                        controller.drawIsolines(controller.getValues().get(e.getY()).get(e.getX()), isolines);
                        //System.out.println("Click @ y" + e.getY() + " x" + e.getX());
                        repaint();
                    }else{
                        //System.out.println("Click on isoline");
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                DecimalFormat df = new DecimalFormat("###.###");
                df.setRoundingMode(RoundingMode.CEILING);
                DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
                dfs.setDecimalSeparator('.');
                df.setDecimalFormatSymbols(dfs);
                hint.setHinttext("x = " + df.format(controller.translateUtoX(e.getX())) + " y = " +df.format(controller.translateVtoY(e.getY()))
                + " f(x, y) = " + df.format(controller.getValue(e.getX(), e.getY())));

            }
        });
        legendpanel = new ImPanel();
        fieldpanel.setPreferredSize(new Dimension(600,400));
        legendpanel.setPreferredSize(new Dimension(500, 40));
        hint = new BottomHint();

        //fieldpanel.setBackground(Color.CYAN);
        //legendpanel.setBackground(Color.BLUE);
        mainframe.setLayout(new BorderLayout());
        mainframe.add(toolbar, BorderLayout.NORTH);
        JPanel mainpanel = new JPanel();
        mainpanel.setLayout(new BorderLayout());
        mainpanel.add(fieldpanel, BorderLayout.NORTH);
        mainpanel.add(legendpanel, BorderLayout.SOUTH);
        mainpanel.setPreferredSize(new Dimension(600, 440));
        //mainframe.add(fieldpanel, BorderLayout.CENTER);
        //mainframe.add(legendpanel, BorderLayout.SOUTH);
        mainframe.add(mainpanel, BorderLayout.CENTER);
        mainframe.add(hint, BorderLayout.SOUTH);
        mainframe.pack();
    }


    public void setController(Controller controller) {
        this.controller = controller;
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
    public  void setGrid(BufferedImage _grid){ fieldpanel.setGrid(_grid);}
    public  void setShowGrid(boolean b) {fieldpanel.setShowGrid(b);}
    public  void setIsolines(BufferedImage _iso){ fieldpanel.setIsolines(_iso);
    isolines = _iso;}
    public  void setShowIsolines(boolean b) {fieldpanel.setShowIsolines(b);}
    public boolean showIsolines(){
        return fieldpanel.isShowIsolines();
    }

    public boolean isShowGrid(){
        return fieldpanel.isShowGrid();
    }

    public boolean isSpan(){
        return turnonspanfill.isSelected();
    }
    //

    public void setLegend(BufferedImage _legend){
        legend = _legend;
        legendpanel.setCanvas(_legend);
    }


    public BufferedImage getField() {
        return fieldpanel.getField();
    }
}
