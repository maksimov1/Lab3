package ru.nsu.fit.g16202.maksimov;

public class Side {
    private Joint j1;
    private Joint j2;
    private int x;
    private int y;
    public Side(Joint _j1, Joint _j2, Double fvalue, int stepu, int stepv){
        if(_j1.getMarker() == 0 || _j2.getMarker() == 0)
            System.out.println("0 MARK IN SIDE !!!!!!");
        if(_j1.getU() == _j2.getU()){
            j1 = _j1.getV() < _j2.getV() ? _j1 : _j2;
            j2 = _j1.getV() < _j2.getV() ? _j2 : _j1;
            x = j1.getU();
            y = j1.getV() + (int)((j2.getV() - j1.getV()) * Math.abs(fvalue - j1.getValue())/(Math.abs(fvalue - j2.getValue()) + Math.abs(fvalue - j1.getValue())));
        }else{
            j1 = _j1.getU() < _j2.getU() ? _j1 : _j2;
            j2 = _j1.getU() < _j2.getU() ? _j2 : _j1;
            y = j1.getV();
            x = j1.getU() + (int)((j2.getU() - j1.getU()) * Math.abs(fvalue - j1.getValue())/(Math.abs(fvalue - j2.getValue()) + Math.abs(fvalue - j1.getValue())));
        }
        //j1 = _j1;
        //j2 = _j2;
        //x = (int) (0.5*(j1.getU() + j2.getU()));
        //y = (int) (0.5*(j1.getV() + j2.getV()));

        /*if (j1.getU() == j2.getU()){
            x = j1.getU();
        }
        else{
            x = (int)(j1.getU() + (fvalue - j1.getValue()) * stepu/(j2.getValue() - fvalue));
        }

        if (j1.getV() == j2.getV()){
            y = j1.getV();
        }
        else{
            y = (int)(j1.getV() + (fvalue - j1.getValue()) * stepv/(j2.getValue() - fvalue));
        }*/
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
