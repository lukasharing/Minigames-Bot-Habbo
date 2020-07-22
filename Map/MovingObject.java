package extensions.fastmap.Map;

import javax.vecmath.Vector2d;

public class MovingObject {
    private int x;
    public int x(){ return x; }
    private int y;
    public int y(){ return y; }
    private double z;
    public double z(){ return z; }
    MovingObject(int _x, int _y, double _z){
        x = _x;
        y = _y;
        z = _z;
    }
    public void set(int _x, int _y, double _z){
        x = _x;
        y = _y;
        z = _z;
    }
    public Vector2d position(){
        return new Vector2d(x, y);
    }
}
