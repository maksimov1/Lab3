package ru.nsu.fit.g16202.maksimov;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Stack;

@SuppressWarnings("Duplicates")
public class Painter {
    public static void drawLegend(BufferedImage legend, ArrayList<Color> colors, int n, Double min, Double max){
        int i;
        int count = 0;
        int j = 0;
        int step = (int) Math.round(legend.getWidth()/n);
        for (i = 0; i < n; i++){
            for (j = i * step; (j <= (i + 1) * (step)) && (j < legend.getWidth()); j++){
                for (int h = 15; h < legend.getHeight(); h++){
                    legend.setRGB(j, h, colors.get(i).getRGB());
                }
            }
        }
        if (j != legend.getWidth()){
            for (; j < legend.getWidth(); j++){
                for (int h = 15; h < legend.getHeight(); h++){
                    legend.setRGB(j, h, colors.get(n-1).getRGB());
                }
            }
        }
        Graphics2D graphics = legend.createGraphics();
        for (i = 1; i < n; i++){
            graphics.setFont(new Font("TimesRoman", Font.PLAIN, 12));
            graphics.setColor(Color.black);
            DecimalFormat df = new DecimalFormat("###.##");
            df.setRoundingMode(RoundingMode.CEILING);
            DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(dfs);
            graphics.drawString(df.format((max - min) * i /n + min ), i * step - 13 , 10);

        }

    }

    public static Color interpol(int j, int step, Color left, Color right){
        int redleft = right.getRed();
        int greenleft = right.getGreen();
        int blueleft = right.getBlue();
        int redright = left.getRed();
        int greenright = left.getGreen();
        int blueright = left.getBlue();

        int newred = 0;
        int newgreen = 0;
        int newblue = 0;
        newred = (int) Math.round(redleft * ((double)(j % (step))/step) + redright * ((double)(step - (j % (step)))/step));
        newgreen = (int) Math.round(greenleft * ((double)(j % (step))/step) + greenright * ((double)(step - (j % (step)))/step));
        newblue = (int) Math.round(blueleft * ((double)(j % (step))/step) + blueright * ((double)(step - (j % (step)))/step));
        return new Color(newred, newgreen, newblue);
    }

    public static Color interp(double j, double x, double y, Color left, Color right){
//        int absx = Math.abs(x);
//        int absy = Math.abs()
//        int newx;
//        int newy;
        int redleft = left.getRed();
        int greenleft = left.getGreen();
        int blueleft = left.getBlue();
        int redright = right.getRed();
        int greenright = right.getGreen();
        int blueright = right.getBlue();

        int newred = 0;
        int newgreen = 0;
        int newblue = 0;
        newred = (int) Math.round(redleft * (y - j)/(y - x) + redright * (j - x)/(y - x));
        newgreen = (int) Math.round(greenleft * (y - j)/(y - x) + greenright * (j - x)/(y - x));
        newblue = (int) Math.round(blueleft * (y - j)/(y - x) + blueright * (j - x)/(y - x));

        return new Color(newred, newgreen, newblue);

    }

    public static void drawInterestingLegend(BufferedImage legend, ArrayList<Color> colors, int n, Double min, Double max){
        int i;
        int count = 0;
        int j = 0;
        int step = (int) Math.round((double) legend.getWidth()/(n - 1));
        for (i = 0; i < n - 1; i++){
            for (j = i * step; (j <= (i + 1) * (step)) && (j < legend.getWidth()); j++){
                int fillcolor = interpol(j, step, colors.get(i), colors.get(i+1)).getRGB();
                for (int h = 15; h < legend.getHeight(); h++){
                    legend.setRGB(j, h, fillcolor);
                }
            }
        }
        /*if (j != legend.getWidth()){
            for (; j < legend.getWidth(); j++){
                for (int h = 15; h < legend.getHeight(); h++){
                    legend.setRGB(j, h, colors.get(n-1).getRGB());
                }
            }
        }*/
        step = (int) Math.round((double) legend.getWidth()/(n));
        Graphics2D graphics = legend.createGraphics();
        for (i = 1; i < n; i++){
            graphics.setFont(new Font("TimesRoman", Font.PLAIN, 12));
            graphics.setColor(Color.black);
            DecimalFormat df = new DecimalFormat("###.##");
            df.setRoundingMode(RoundingMode.CEILING);
            DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            df.setDecimalFormatSymbols(dfs);
            graphics.drawString(df.format((max - min) * i /n + min ), i * step - 13 , 10);

        }

    }

    public static void drawLine(Graphics2D isographics, int x1, int y1, int x2, int y2, Color color){
        isographics.setColor(color);
        /*
        gimpacts.setComposite(AlphaComposite.Clear);
        gimpacts.setColor(new Color(0, true));
        gimpacts.fillRect(0,0, img.getWidth(), img.getHeight());
        gimpacts.setComposite(AlphaComposite.DstOver);
        gimpacts.setColor(new Color(0));*/
        isographics.drawLine(x1, y1, x2, y2);
    }

    public static void findSpans(BufferedImage in, Span span, Stack<Span> spans, int borderColor, int newColor){
        int y = span.getY() + 1;
        for (; y >= span.getY()-2; y -= 2){
            if (y > in.getHeight() - 1 || y < 0)
                continue;
            boolean draw = false;
            int xleft = span.getX1();
            //System.out.println(xleft + " " + y);
            while((in.getRGB(xleft, y) != borderColor && (in.getRGB(xleft, y) != newColor) && xleft >= 1)){
                xleft--;
            }
            for (int xi = xleft; xi <= span.getX2() || draw; xi++){
                if ((in.getRGB(xi, y) == borderColor) || (in.getRGB(xi, y) == newColor)){
                    if (draw){
                        //drawSpan(in, new Span(xleft, xi, y));
                        spans.push(new Span(xleft, xi - 1, y));
                        draw = false;
                    }
                }
                else{
                    if(!draw){
                        xleft = xi;
                        draw = true;
                    }
                }
                if(xi == in.getWidth() - 1){
                    if(draw){
                        spans.push(new Span(xleft, xi - 1, y));
                        draw = false;
                    }
                    break;
                }


            }

        }

    }

    public static void drawSpan(BufferedImage in, Span span, int color){
        for(int xi = span.getX1(); xi <= span.getX2(); xi++){
            in.setRGB(xi, span.getY(), color);
        }

    }

    public static void spanFill(BufferedImage in, int x, int y, int borderColor, int newColor){
        if (x < 0 || x > in.getWidth() - 1|| y < 0 || y > in.getHeight() - 1)
            return;
        if ((in.getRGB(x, y) == borderColor) || (in.getRGB(x, y) == newColor)){
            return;
        }
        Stack<Span> spans = new Stack<>();
        int xi = x;
        int yi = y;
        while((in.getRGB(xi, yi) != borderColor) && (in.getRGB(xi, yi) != newColor)){
            xi--;
            if(xi <= 0)
                break;
        }
        xi++;
        int xleft = xi;
        xi = x;
        while((in.getRGB(xi, yi) != borderColor) && (in.getRGB(xi, yi) != newColor)){
            xi++;
            if(xi >= in.getWidth() - 1)
                break;
        }
        xi--;
        //System.out.println(xleft + " " + xi + " " + y);
        spans.push(new Span(xleft, xi, y));
        while(!spans.empty()){
            Span span = spans.pop();
            findSpans(in, span, spans, borderColor, newColor);
            drawSpan(in, span, newColor);
        }
    }

}
