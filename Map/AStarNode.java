package extensions.fastmap.Map;

import gearth.extensions.parsers.HEntity;
import gearth.extensions.parsers.HPoint;

import javax.vecmath.Vector2d;
import java.util.HashMap;
import java.util.Map;

public class AStarNode implements Comparable<AStarNode>{
    private AStar parent;
    private AStarNode from;
    public AStarNode getFrom(){ return from; };
    private Vector2d pos;
    public Vector2d getPosition(){ return pos; };
    private double h;
    private double g;
    private double fcal;
    public double f(){ return fcal; };

    int x(){ return (int)pos.x; }
    int y(){ return (int)pos.y; }

    AStarNode(AStar parent, AStarNode r, Vector2d p){
        this.parent = parent;
        this.from = r;
        this.pos  = p;
        this.h = 0.0;
        this.g = 0.0;
        this.fcal = 0.0;
    }

    public void f(Vector2d goal, RoomMovingManager movings, RoomPlayerManager users){
        this.h = parent.h(this, goal, movings, users);
        if(this.from != null){
            g();
        }
        this.fcal = h + g;
    };

    private void g(){
        Vector2d dir = (Vector2d)this.from.getPosition().clone();
        dir.sub(pos);
        this.g = this.from.g + dir.length();
    };

    @Override
    public int compareTo(AStarNode o) {
        return (int)Math.signum(fcal - o.fcal);
    }
}
