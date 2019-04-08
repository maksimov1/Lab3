package ru.nsu.fit.g16202.maksimov;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

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

    public static void drawLine(BufferedImage img, int x1, int y1, int x2, int y2){
        Graphics2D gimpacts = img.createGraphics();
        /*
        gimpacts.setComposite(AlphaComposite.Clear);
        gimpacts.setColor(new Color(0, true));
        gimpacts.fillRect(0,0, img.getWidth(), img.getHeight());
        gimpacts.setComposite(AlphaComposite.DstOver);
        gimpacts.setColor(new Color(0));*/
        gimpacts.drawLine(x1, y1, x2, y2);
    }

}
