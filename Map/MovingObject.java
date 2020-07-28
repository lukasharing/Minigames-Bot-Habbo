package extensions.fastmap.Map;

import javax.vecmath.Vector2d;
import java.util.LinkedList;

public class MovingObject {

    private Vector2d position_interpolation;
    public double ix(){ return position_interpolation.getX(); }
    public double iy(){ return position_interpolation.getY(); }
    private Vector2d last_position;

    private LinkedList<Vector2d> position;
    private double z;

    public int x(){ return (int)position.getLast().getX(); }
    public int y(){ return (int)position.getLast().getY(); }
    public double z(){ return z; }

    private float interpolate(float a, float b, float h){
      return (1 - h) * a + h * b;
    };

    public void interpolation2D(float h){
        // Nothing to interpolate
        if(position.size() == 1);
        // Create Interpolation
        Vector2d next = position.getFirst();
        position_interpolation = new Vector2d(
                interpolate((float)last_position.x, (float)next.y, h),
                interpolate((float)last_position.x, (float)next.y, h)
        );
        // If transition has made, then update information.
        if(h >= 1.0){
            last_position = next;
            position.poll();
        }
    };

    MovingObject(int _x, int _y, double _z){
        last_position = position_interpolation = new Vector2d(_x, _y);
        position.add(last_position);
        z = _z;
    }
    public void set(int _x, int _y, double _z){
        position.add(new Vector2d(_x, _y));
        z = _z;
    }
    public Vector2d position(){
        return position.getLast();
    }
}
