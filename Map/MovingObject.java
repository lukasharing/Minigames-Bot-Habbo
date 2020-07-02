package extensions.fastmap.Map;

import javax.vecmath.Vector2d;

public class MovingObject {
    public int x;
    public int y;
    public double z;
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
