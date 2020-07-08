package extensions.fastmap.Map;
import extensions.fastmap.FastMap;
import extensions.fastmap.MiniGames.MiniGameController;
import javafx.scene.canvas.GraphicsContext;

import javax.vecmath.Vector2d;
import java.util.PriorityQueue;

public class AStar {

    private final MiniGameController parent;
    public AStar(MiniGameController parent){
        this.parent = parent;
    }

    public double h(Vector2d goal, RoomMovingManager movings, RoomPlayerManager users){ return parent.h(goal, movings, users); };

    public AStarNode algorithm(Vector2d init, Vector2d goal) {
        Room room = parent.getRoom();

        PriorityQueue<AStarNode> open = new PriorityQueue<>();
        AStarNode a_from = new AStarNode(this,null, init);
        a_from.f(goal, room.getMovings(), room.getPlayers());
        open.add(a_from);

        AStarNode[][] visited = new AStarNode[room.height()][room.width()];
        visited[(int)init.getY()][(int)init.getX()] = a_from;

        int ticks = 0;
        while (!open.isEmpty() && ++ticks < 10) {
            AStarNode expanded = open.poll();

            if (expanded.getPosition().equals(goal)) {
                return expanded;
            }

            int x = expanded.x(), y = expanded.y();
            for(int v = -1; v <= 1; ++v){
                for(int w = -1; w <= 1; ++w){
                    Vector2d posibility = new Vector2d(x + w, y + v);
                    if(w != 0 && v != 0 && room.transitable(posibility)){
                        AStarNode a_top = visited[y + v][x + w];
                        AStarNode child = new AStarNode(this, expanded, new Vector2d(x + w, y + v));
                        child.f(goal, room.getMovings(), room.getPlayers());

                        if (a_top == null || (a_top != null && child.f() <= a_top.f())) {
                            open.add(child);
                            visited[y + v][x + w] = child;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Vector2d backtracking_action(AStarNode path){
        if(path == null || path.getFrom() == null) return null;

        // Backtracking
        while (path.getFrom().getFrom() != null) {
            path = path.getFrom();
        }

        return path.getPosition();
    };

}
