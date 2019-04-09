package ru.nsu.fit.g16202.maksimov;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;

public class Controller {
    private View view;
    private int k;
    private int m;
    private int n;
    private Double min;
    private Double max;
    private ArrayList<Color> colors;
    private Color isoline;
    private BufferedImage field;
    private BufferedImage interestingfield;
    private BufferedImage spanfield;
    private Double a;
    private Double b;
    private Double c;
    private Double d;
    private int u0, v0, u1, v1;
    private ArrayList<ArrayList<Double>> values;
    private ArrayList<ArrayList<Joint>> joints;
    private int stepu;
    private int stepv;
    private Graphics2D fieldg;
    private BufferedImage isolines;
    private Graphics2D isographics;
    private BufferedImage grid;
    private BufferedImage legend;
    private Double eps = 0.00005d;
    private BufferedImage interestinglegend;
    private BufferedImage clearisolines;
    private  ArrayList<ArrayList<Double>> legendvalues;
    public Controller(){
        a = -5d;
        b = 5d;
        c = -5d;
        d = 5d;
        u0 = 0;
        v0 = 0;
        u1 = 600;
        v1 = 400;
        view = new View(this);
        try {
            getParams();
        } catch (IOException e) {
            view.showFileError();
            return;
        }
        field = new BufferedImage(u1, v1, BufferedImage.TYPE_INT_RGB);
        interestingfield = new BufferedImage(u1, v1, BufferedImage.TYPE_INT_RGB);
        spanfield = new BufferedImage(u1, v1, BufferedImage.TYPE_INT_RGB);
        calculateMaxMin();
        setJoints();
        view.setField(field);
        legend = new BufferedImage(600, 50, BufferedImage.TYPE_INT_RGB);
        interestinglegend =  new BufferedImage(600, 50, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = legend.createGraphics();
        graphics.setPaint ( new Color ( 255, 255, 255 ) );
        graphics.fillRect ( 0, 0, legend.getWidth(), legend.getHeight());
        graphics.setColor(Color.black);

        graphics = interestinglegend.createGraphics();
        graphics.setPaint ( new Color ( 255, 255, 255 ) );
        graphics.fillRect ( 0, 0, interestinglegend.getWidth(), interestinglegend.getHeight());
        graphics.setColor(Color.black);

        Painter.drawInterestingLegend(interestinglegend, colors, n+1, min, max);
        Painter.drawLegend(legend, colors, n+1, min, max);
        view.setLegend(legend);
        drawInterestingField(interestingfield, values);
        drawField(field, values);
        //Painter.drawLine(field, 0, 0, 300, 300);
        fieldg = field.createGraphics();
        isolines = new BufferedImage(u1, v1, BufferedImage.TYPE_INT_ARGB);

        isographics = isolines.createGraphics();
        isographics.setComposite(AlphaComposite.Clear);
        isographics.setColor(new Color(0, true));
        isographics.fillRect(0,0, isolines.getWidth(), isolines.getHeight());
        isographics.setComposite(AlphaComposite.DstOver);
        isographics.setColor(isoline);

        view.setIsolines(isolines);
        view.setShowIsolines(false);

        for (int i = 0; i < n+1; i++) {
            drawIsolines((double) (min + (max - min) * (i + 1) / (n + 1)), isolines);
        }
        spanfield = deepCopy(isolines);
        clearisolines = deepCopy(isolines);
        drawSpanField(spanfield);
        drawGrid();
        view.setGrid(grid);
        view.setShowGrid(false);
        view.repaint();
        view.setVisible(true);

    }

    public void drawSpanField(BufferedImage isolines){
        //int i, j;
        for (int i = 0; i < n+1; i++){
            //drawIsolines(((double)(max - min)*i)/(n+1));
            drawIsolines((double) (min + (max - min)*(i + 1)/(n+1)), isolines);
            boolean isSpanned = false;
            boolean donotspan = false;
            for (ArrayList<Joint> row : joints){
                for (Joint joint : row){
                    Joint njoint;
                    //if (joint.getV() >= isolines.getHeight() || joint.getV() < 0 ||
                    //joint.getU()>=isolines.getWidth() || joint.getU() < 0){
                    if(false){
                        int ju = joint.getU();
                        int jv = joint.getV();
                        if (ju >= isolines.getWidth()){
                            ju = isolines.getWidth() - 1;
                        }
                        if (ju < 0){
                            ju = 0;
                        }
                        if (jv >= isolines.getHeight()){
                            jv = isolines.getHeight() - 1;
                        }
                        if (jv < 0){
                            jv = 0;
                        }
                        njoint = new Joint(translateUtoX(ju), translateVtoY(jv));
                        njoint.calculateMarker((min + (max - min)*(i + 1)/(n+1)));
                    }else
                    {
                        njoint = joint;
                    }
                    if ((njoint.getMarker() == -1) && (njoint.getU() < isolines.getWidth()) && (njoint.getV() < isolines.getHeight())){
                        int k = 0;
                        donotspan = false;
                        for (k = 0; k <= n; k++){
                            if (isolines.getRGB(njoint.getU(), njoint.getV()) == (colors.get(k)).getRGB())
                                donotspan = true;
                        }
                        if (!donotspan)
                            Painter.spanFill(isolines, njoint.getU(), njoint.getV(), isoline.getRGB(), colors.get(i).getRGB());

                        //isSpanned = true;
                        //interestingfield.setRGB(joint.getU(), joint.getV(), Color.black.getRGB());
                        //interestingfield.setRGB(joint.getU(), joint.getV()+1, Color.black.getRGB());
                        //interestingfield.setRGB(joint.getU(), joint.getV()+2, Color.black.getRGB());
                        //break;
                    }
                }
            }
//            for(ArrayList<Joint> row : joints){
//                for (Joint joint : row){
//                    System.out.print(joint.getMarker() + " ");
//                }
//                System.out.println();
//            }
            //view.repaint();
            //view.setVisible(true);
        }

    }
    public void swapSpan(){
        if (view.getField() == field) {
            view.setField(spanfield);
            view.repaint();
        }else
        if (view.getField() == spanfield){
            view.setField(field);
            view.repaint();
        }
    }
    public void changeInteresting(){
        if (view.getField() == field || view.getField() == spanfield) {
            view.setField(interestingfield);
            view.setLegend(interestinglegend);
        }
        else{
            if (view.isSpan())
                view.setField(spanfield);
            else
                view.setField(field);
            view.setLegend(legend);
        }
        view.repaint();
    }

    public void changeIsolines(){
        view.setShowIsolines(!view.showIsolines());
        view.repaint();
        // CLEAR ISOLINES

    }

    public void changeGrid(){
        view.setShowGrid(!view.isShowGrid());
        view.repaint();

    }

    public void drawGrid(){
        grid = new BufferedImage(u1, v1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gridgraphics = grid.createGraphics();
        gridgraphics.setComposite(AlphaComposite.Clear);
        gridgraphics.setColor(new Color(0, true));
        gridgraphics.fillRect(0,0, grid.getWidth(), grid.getHeight());
        gridgraphics.setComposite(AlphaComposite.DstOver);
        gridgraphics.setColor(Color.BLACK);

        for (int i = 0; i < k - 1; i++){
            for (int j=0; j < m - 1; j++){
                gridgraphics.drawLine(i * stepu, j * stepv, (i + 1) * stepu, j *stepv);
                gridgraphics.drawLine(i * stepu, j * stepv, i * stepu, (j+1) *stepv);
            }
        }
        for (int i = 0; i < k - 1; i++){
            gridgraphics.drawLine((m-1) * stepu, i * stepv, (m-1) * stepu, (i+1) *stepv);
        }
        for (int i = 0; i < m - 1; i++){
            gridgraphics.drawLine(i * stepu, (k-1) * stepv, (i+1) * stepu, (k-1) *stepv);
        }
    }

    public void drawIsolines(Double fvalue, BufferedImage img){
        int i,j;
        for (ArrayList<Joint> row : joints){
            for (Joint joint : row){
                joint.calculateMarker(fvalue);
            }
        }
        for (i = 0; i < m; i++){
            for (j = 0; j < k; j++){
                Joint leftupper = joints.get(i+1).get(j);
                Joint rightupper = joints.get(i+1).get(j+1);
                Joint leftlower = joints.get(i).get(j);
                Joint rightlower = joints.get(i).get(j+1);
                drawByJoints(leftupper, rightupper, leftlower, rightlower, fvalue, img);

            }
        }
        /*
        for (ArrayList<Joint> row : joints){
            for (Joint joint : row){
                System.out.print(joint.getMarker());
            }
            System.out.println();
        }*/
    }

    private void drawByJoints(Joint leftupper, Joint rightupper, Joint leftlower, Joint rightlower, Double fvalue, BufferedImage img){
        ArrayList<Joint> rect = new ArrayList<>();
        ArrayList<Side> diffsides = new ArrayList<>();
        if (leftupper.getMarker() == 0 || rightupper.getMarker() == 0 || leftlower.getMarker() == 0 ||
                rightlower.getMarker() == 0){
            System.out.println("3 catched");
            leftupper.calculateMarker(fvalue + eps);
            rightupper.calculateMarker(fvalue + eps);
            leftlower.calculateMarker(fvalue + eps);
            rightlower.calculateMarker(fvalue + eps);
            drawByJoints(leftupper, rightupper, leftlower, rightlower, fvalue + eps, img);
            leftupper.calculateMarker(fvalue);
            rightupper.calculateMarker(fvalue);
            leftlower.calculateMarker(fvalue);
            rightlower.calculateMarker(fvalue);
            return;
        }
        int count = 0;
        rect.add(leftupper);
        rect.add(rightupper);
        rect.add(rightlower);
        rect.add(leftlower);
        count+=addIfDifferent(leftupper, rightupper, diffsides, fvalue);
        count+=addIfDifferent(leftupper, leftlower, diffsides, fvalue);
        count+=addIfDifferent(rightlower, rightupper, diffsides, fvalue);
        count+=addIfDifferent(rightlower, leftlower, diffsides, fvalue);
        if (count == 2){
            drawTwo(diffsides, img);
        }
        if(count == 4){
            //System.out.println("OOPSIE " + count);
            Joint middle = new Joint((leftlower.getX() + rightlower.getX())*0.5, (leftlower.getY() + rightupper.getY())*0.5);
            middle.setU(translateXtoU(middle.getX()));
            middle.setV(translateYtoV(middle.getY()));
            Side s1 = new Side(leftupper, rightupper, fvalue, stepu, stepv);
            Side s2 = new Side(rightupper, rightlower, fvalue, stepu, stepv);
            Side s3 = new Side(leftlower, rightlower, fvalue, stepu, stepv);
            Side s4 = new Side(leftupper, leftlower, fvalue, stepu, stepv);
            ArrayList<Side> sides1 = new ArrayList<>();
            ArrayList<Side> sides2 = new ArrayList<>();
            if(leftupper.getMarker() == 1){
                if (middle.getMarker() == 1){
                    sides1.add(s1);
                    sides1.add(s2);
                    sides2.add(s3);
                    sides2.add(s4);
                }else{
                    sides1.add(s2);
                    sides1.add(s3);
                    sides2.add(s4);
                    sides2.add(s1);
                }
            }else{
                if (middle.getMarker() == -1){
                    sides1.add(s1);
                    sides1.add(s2);
                    sides2.add(s3);
                    sides2.add(s4);
                }else{
                    sides1.add(s2);
                    sides1.add(s3);
                    sides2.add(s4);
                    sides2.add(s1);
                }

            }
            drawTwo(sides1, img);
            drawTwo(sides2, img);
        }
        if(count == 3)
            System.out.println("STILL 3 AFTER EPS");



    }

    public ArrayList<ArrayList<Double>> getValues() {
        return values;
    }

    private void drawTwo(ArrayList<Side> diffsides, BufferedImage img){
        Graphics2D grph = img.createGraphics();
        Painter.drawLine(grph, diffsides.get(0).getX(), diffsides.get(0).getY(),
                diffsides.get(1).getX(), diffsides.get(1).getY(), isoline);
    }
    private int addIfDifferent(Joint j1, Joint j2, ArrayList<Side> diffsides, Double fvalue){
        if (j1.getMarker() == 0 || j2.getMarker() == 0)
            System.out.println("333333333333333333333333333");
        if (j1.getMarker() != j2.getMarker()) {
            diffsides.add(new Side(j1, j2, fvalue, stepu, stepv));
            return 1;
        }
        return 0;
    }


    public void setJoints(){
        joints = new ArrayList<>();
        stepu = (int) ((u1 - u0) / (k - 1)) + 1;
        stepv = (int) ((v1 - v0) / (m-1)) + 1;
        for (int i = 0; i <= m + 1; i++){
            ArrayList<Joint> row = new ArrayList<>();
            for (int j = 0; j <= k + 1; j++){
                Joint joint = new Joint(translateUtoX( stepu * j), translateVtoY(stepv * i));
                //joint.setU(translateXtoU(joint.getX()) >= field.getWidth() ? field.getWidth() - 1 : translateXtoU(joint.getX()));
                //joint.setV(translateYtoV(joint.getY()) >= field.getHeight() ? field.getHeight() - 1 : translateYtoV(joint.getY()));
                joint.setU(translateXtoU(joint.getX()));
                joint.setV(translateYtoV(joint.getY()));


                row.add(joint);
            }
            joints.add(row);
        }
    }

    public void calculateMaxMin(){
        values = new ArrayList<>();
        max = function(translateUtoX(0), translateVtoY(0));
        min = function(translateUtoX(0), translateVtoY(0));
        for (int j = 0; j < field.getHeight(); j++){
            ArrayList<Double> row = new ArrayList<>();
            for (int i = 0; i < field.getWidth(); i++){
                Double value = function(translateUtoX(i), translateVtoY(j));
                if (value > max)
                    max = value;
                if (value < min)
                    min = value;
                row.add(value);
            }
            values.add(row);
        }
    }
    public void drawField(BufferedImage _field, ArrayList<ArrayList<Double>> _values){
        int i, j;
        for (j = 0; j < _field.getHeight(); j++){
            for (i = 0; i < _field.getWidth(); i++){
                Double value = _values.get(j).get(i);
                _field.setRGB(i, j, colors.get(getClosestColor(value)).getRGB());
                //field.setRGB(i, j, legend.getRGB((int) Math.round((values.get(j).get(i)) * ((double)legend.getWidth()/(max - min))), legend.getHeight() - 5));
            }
        }
    }

    public void drawInterestingField(BufferedImage _field, ArrayList<ArrayList<Double>> _values){
        int i, j;
        for (j = 0; j < _field.getHeight(); j++){
            for (i = 0; i < _field.getWidth(); i++){
                Double value = _values.get(j).get(i);
                Double colorstep = ((max - min)/(n + 1));
                int leftdot = closestMiddle(value);
                int rightdot = closestMiddle(value) + 1;
                Color leftcolor = colors.get(leftdot);
                Color rightcolor = colors.get(rightdot);
                Color thiscolor = Painter.interp(value, leftdot * (max - min)/(n) + min, rightdot * (max - min)/(n) + min, leftcolor, rightcolor);
                //int numleft = getClosestNum(value);
                //int numright = getClosestNum(value + (max - min)/(n + 1));
                //Color leftcolor = colors.get(getClosestColor(value));
                //Color rightcolor = colors.get(getClosestColor(value));
                _field.setRGB(i, j, thiscolor.getRGB());
                //field.setRGB(i, j, legend.getRGB((int) Math.round((values.get(j).get(i)) * ((double)legend.getWidth()/(max - min))), legend.getHeight() - 5));
            }
        }
    }

    public int closestMiddle(Double value){
        Double step = (max - min)/n;
        //if (value > max){
        //    return n;
        //}
        if(value > max){
            return 0;
        }
        if (value <= max && value >=min){
            int retval = (int)Math.abs((value - min)/step);
            if (retval == n){
                return n-1;
            }
            else{
                return retval;
            }
        }
        return  0;

    }
    private void getParams() throws IOException {
        FileReader fr = new FileReader(new File("config.txt"));
        BufferedReader reader = new BufferedReader(fr);
        String linec;
        int i = 0;
        n = 0;
        colors = new ArrayList<>();
        while ((linec = reader.readLine()) != null){
            if ((linec.charAt(0) == '/') && (linec.charAt(1) == '/'))
                continue;
            String line =(linec.split("//"))[0];
            String[] linenosp = line.split(" |\t");
            if (i == 0){
                k = Integer.valueOf(linenosp[0]);
                m = Integer.valueOf(linenosp[1]);
            }
            if (i == 1){
                n = Integer.valueOf(linenosp[0]);
            }
            if (i > 1 && i < 3 + n){
                colors.add(new Color(Integer.valueOf(linenosp[0]), Integer.valueOf(linenosp[1]), Integer.valueOf(linenosp[2])));
            }

            if(i == 3+n){
                isoline = (new Color(Integer.valueOf(linenosp[0]), Integer.valueOf(linenosp[1]), Integer.valueOf(linenosp[2])));
            }
            if (i>3+n)
                throw new IOException("wrong file format");
            i++;
        }
        System.out.println(isoline.toString());

    }

    public static Double function(Double x, Double y){
        //return Math.sin(Math.pow(x,2) + Math.pow(y,2)) + Math.sin(x*y);
        //return Math.pow(Math.pow(y*0.5, 2) + Math.pow(x*0.5, 2), 0.25);
        return (Math.sqrt(Math.pow(2*x, 2) + Math.pow(y,2))) + 2*Math.sin(x) + Math.cos(y);
        //return Math.abs(Math.sin(x*0.25));
        //return 1/(Math.pow(x,2) + Math.pow(y,2) + 0.5);
        // (5 - (x^2 + y^2 + 1)^0.5) - exp(-x) * exp(-y)
        //return 5 - Math.pow((Math.pow(x,2) + Math.pow(y,2) + 1), 0.5) - Math.exp(-x)*Math.exp(-y);
        //return Math.sin(x);
        //return Math.sin(y)*Math.cos(x);
    }

    public int translateXtoU(Double x){
        return ((int)((u1 - u0) * (x - a)/(b - a) + u0 + 0.5d));
    }

    public int translateYtoV(Double y){
        return ((int)((v1 - v0) * (y - c)/(d - c) + v0 + 0.5d));
    }

    public Double translateUtoX(int u){
        return (b-a) * (u - u0)/(u1-u0) + a;
    }

    public Double translateVtoY(int v){
        return (d-c) * (v - v0)/(v1-v0) + c;
    }
    public int getClosestColor(double value){
        if (((int)((value - min) * (n+1)/(max - min))) > n)
            return n;
        if (((int)((value - min) * (n+1)/(max - min))) < 0)
            return 0;

        return ((int)((value - min) * (n+1)/(max - min)));
    }

//    public int getClosestNum(double value){
//        if (((int)((value - min) * (n)/(max - min))) > n-1)
//            return n-1;
//        if (((int)((value - min) * (n)/(max - min))) < 0)
//            return 0;
//
//        return ((int)((value - min) * (n)/(max - min)));
//    }

    public Color getIsoline() {
        return isoline;
    }

    public Double getValue(int _x, int _y){
        return values.get(_y).get(_x);
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
