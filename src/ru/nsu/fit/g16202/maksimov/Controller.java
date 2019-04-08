package ru.nsu.fit.g16202.maksimov;

import java.awt.*;
import java.awt.image.BufferedImage;
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
    private Double eps = 0.00005d;
    public Controller(){
        a = -5d;
        b = 5d;
        c = -5d;
        d = 5d;
        u0 = 0;
        v0 = 0;
        u1 = 600;
        v1 = 400;
        view = new View();
        try {
            getParams();
        } catch (IOException e) {
            view.showFileError();
        }
        field = new BufferedImage(u1, v1, BufferedImage.TYPE_INT_RGB);

        calculateMaxMin();
        setJoints();
        view.setField(field);
        Painter.drawLegend(view.getLegend(), colors, n + 1, min, max);
        drawField();
        //Painter.drawLine(field, 0, 0, 300, 300);
        fieldg = field.createGraphics();
        //drawIsolines(min + 0.5 * max);
        for (int i = 1; i < n+1; i++){
            //drawIsolines(((double)(max - min)*i)/(n+1));
            drawIsolines((double) (min + (max - min)*i/(n+1)));
        }
        view.repaint();
        view.setVisible(true);

    }

    public void drawIsolines(Double fvalue){
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
                drawByJoints(leftupper, rightupper, leftlower, rightlower, fvalue);

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

    private void drawByJoints(Joint leftupper, Joint rightupper, Joint leftlower, Joint rightlower, Double fvalue){
        ArrayList<Joint> rect = new ArrayList<>();
        ArrayList<Side> diffsides = new ArrayList<>();
        if (leftupper.getMarker() == 0 || rightupper.getMarker() == 0 || leftlower.getMarker() == 0 ||
                rightlower.getMarker() == 0){
            System.out.println("3 catched");
            leftupper.calculateMarker(fvalue + eps);
            rightupper.calculateMarker(fvalue + eps);
            leftlower.calculateMarker(fvalue + eps);
            rightlower.calculateMarker(fvalue + eps);
            drawByJoints(leftupper, rightupper, leftlower, rightlower, fvalue + eps);
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
            drawTwo(diffsides);
        }
        if(count == 4){
            System.out.println("OOPSIE " + count);
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
            drawTwo(sides1);
            drawTwo(sides2);
        }
        if(count == 3)
            System.out.println("STILL 3 AFTER EPS");



    }
    private void drawTwo(ArrayList<Side> diffsides){
        Painter.drawLine(field, diffsides.get(0).getX(), diffsides.get(0).getY(),
                diffsides.get(1).getX(), diffsides.get(1).getY());
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
        stepu = (int) ((u1 - u0) / (k - 1));
        stepv = (int) ((v1 - v0) / (m-1));
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
    public void drawField(){
        int i, j;
        for (j = 0; j < field.getHeight(); j++){
            for (i = 0; i < field.getWidth(); i++){
                Double value = values.get(j).get(i);
                field.setRGB(i, j, colors.get(getClosestColor(value)).getRGB());
            }
        }
        /*for (ArrayList<Joint> row : joints){
            for (Joint joint : row){
                field.setRGB(joint.getU(), joint.getV(), Color.green.getRGB());
                System.out.print(joint.getU()+" "+joint.getV()+ " ");
            }
            System.out.println();
        }*/

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
        return (Math.sin(y) * Math.cos(x));
        //return (Math.sqrt(Math.pow(2*x, 2) + Math.pow(y,2))) + 2*Math.sin(x) + Math.cos(y);
        //return Math.abs(Math.sin(x*0.25));
        //return 1/(Math.pow(x,2) + Math.pow(y,2) + 0.5);
        // (5 - (x^2 + y^2 + 1)^0.5) - exp(-x) * exp(-y)
        //return 5 - Math.pow((Math.pow(x,2) + Math.pow(y,2) + 1), 0.5) - Math.exp(-x)*Math.exp(-y);
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
}
