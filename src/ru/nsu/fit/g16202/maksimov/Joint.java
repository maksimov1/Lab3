package ru.nsu.fit.g16202.maksimov;

public class Joint {
    private Double x;
    private Double y;
    private int u;
    private int v;
    private Double value;
    private int marker;

    public Joint(Double _x, Double _y){
        x = _x;
        y = _y;
        value = Controller.function(x, y);
    }

    public void setU(int u) {
        this.u = u;
    }

    public int getU() {
        return u;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public int getMarker() {
        return marker;
    }

    public void calculateMarker(Double fvalue){
        if (value > fvalue)
            marker = 1;
        if (value < fvalue)
            marker = -1;
        if (value == fvalue){
            marker = 0;
            System.out.println("OOPSIE");
        }

    }

    public Double getValue() {
        return value;
    }
}
