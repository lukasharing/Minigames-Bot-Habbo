package extensions.fastmap.Map;
import extensions.fastmap.FastMap;

import javax.vecmath.Vector2d;
import java.util.PriorityQueue;

public class AStar {

    private final FastMap parent;
    public AStar(FastMap parent){
        this.parent = parent;
    }

    public AStarNode algorithm(Vector2d init, Vector2d goal) {
        Room room = parent.getRoom();

        PriorityQueue<AStarNode> open = new PriorityQueue<>();
        AStarNode a_from = new AStarNode(null, init);
        a_from.h(goal, room.getMovings(), room.getPlayers());
        open.add(a_from);

        AStarNode[][] visited = new AStarNode[room.height()][room.width()];
        visited[(int)init.getY()][(int)init.getX()] = a_from;

        while (!open.isEmpty()) {
            AStarNode expanded = open.poll();

            // Coords
            if (expanded.equals(goal)) {
                return expanded;
            }

            int x = expanded.x(), y = expanded.y();
            for(int v = -1; v <= 1; ++v){
                for(int w = -1; w <= 1; ++w){
                    Vector2d posibility = new Vector2d(x + w, y + v);
                    if(room.transitable(posibility)){
                        AStarNode a_top = visited[y + v][x + w];
                        AStarNode child = new AStarNode(expanded, new Vector2d(x + w, y + v));
                        child.f(goal, room.getMovings(), room.getPlayers());

                        if (a_top == null || (a_top != null && child.g <= a_top.g)) {
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
        if(path == null || path.from == null) return null;

        // Backtracking
        while (path.from.from != null) {
            path = path.from;
        }

        return path.pos;
    };

}
