package ru.nsu.fit.g16202.maksimov;

public class Span {
    private int x1;
    private int x2;
    private int y;

    public Span(int _x1, int _x2, int _y){
        x1 = _x1;
        x2 = _x2;
        y = _y;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY() {
        return y;
    }
}
