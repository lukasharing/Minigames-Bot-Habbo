package extensions.fastmap.Map;

import gearth.extensions.parsers.HEntity;
import gearth.extensions.parsers.HPoint;

import javax.vecmath.Vector2d;
import java.util.HashMap;
import java.util.Map;

public class AStarNode implements Comparable<AStarNode>{
    AStarNode from;
    Vector2d pos;
    double h;
    double g;
    public double fcal;

    int x(){ return (int)pos.x; }
    int y(){ return (int)pos.y; }

    AStarNode(AStarNode r, Vector2d p){
        this.from = r;
        this.pos  = p;
        this.h = 0.0;
        this.g = 0.0;
        this.fcal = 0.0;
    }

    public void f(Vector2d goal, RoomMovingManager movings, RoomPlayerManager users){
        h(goal, movings, users);
        g();
        this.fcal = h + g;
    };

    public void h(Vector2d goal, RoomMovingManager movings, RoomPlayerManager users){
        Vector2d goal_copy = (Vector2d)pos.clone();
        goal_copy.sub(pos);

        double heuristic = 0.0;
        heuristic += movings.h(pos);
        heuristic += users.h(pos);

        this.h = goal_copy.length() + heuristic;
    };

    private void g(){
        this.g = this.from.g + 1.0;
    };

    @Override
    public int compareTo(AStarNode o) {
        return (int)Math.signum(fcal - o.fcal);
    }
}
