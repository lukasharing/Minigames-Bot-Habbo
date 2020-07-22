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

    public double h(AStarNode node, Vector2d goal, RoomMovingManager movings, RoomPlayerManager users){ return parent.h(node, goal, movings, users); };

    public AStarNode algorithm(Vector2d init, Vector2d goal) {
        Room room = parent.getRoom();

        PriorityQueue<AStarNode> open = new PriorityQueue<>();
        AStarNode a_from = new AStarNode(this,null, init);
        a_from.f(goal, room.getMovings(), room.getPlayers());
        open.add(a_from);

        AStarNode[][] visited = new AStarNode[room.height()][room.width()];
        visited[(int)init.getY()][(int)init.getX()] = a_from;

        while (!open.isEmpty()) {
            AStarNode expanded = open.poll();

            if (expanded.getPosition().equals(goal)) {
                return expanded;
            }

            int x = expanded.x(), y = expanded.y();

            Vector2d left = new Vector2d(x - 1, y + 0);
            if(room.transitable(left)){
                AStarNode a_top = visited[y + 0][x - 1];
                AStarNode child = new AStarNode(this, expanded, new Vector2d(x - 1, y + 0));
                child.f(goal, room.getMovings(), room.getPlayers());

                if (a_top == null || (a_top != null && child.f() <= a_top.f())) {
                    open.add(child);
                    visited[y + 0][x - 1] = child;
                }
            }

            Vector2d right = new Vector2d(x + 1, y + 0);
            if(room.transitable(left)){
                AStarNode a_top = visited[y + 0][x + 1];
                AStarNode child = new AStarNode(this, expanded, new Vector2d(x + 1, y + 0));
                child.f(goal, room.getMovings(), room.getPlayers());

                if (a_top == null || (a_top != null && child.f() <= a_top.f())) {
                    open.add(child);
                    visited[y + 0][x + 1] = child;
                }
            }

            Vector2d top = new Vector2d(x + 0, y - 1);
            if(room.transitable(left)){
                AStarNode a_top = visited[y - 1][x + 0];
                AStarNode child = new AStarNode(this, expanded, new Vector2d(x + 0, y - 1));
                child.f(goal, room.getMovings(), room.getPlayers());

                if (a_top == null || (a_top != null && child.f() <= a_top.f())) {
                    open.add(child);
                    visited[y - 1][x + 0] = child;
                }
            }


            Vector2d bottom = new Vector2d(x + 0, y + 1);
            if(room.transitable(left)){
                AStarNode a_top = visited[y + 1][x + 0];
                AStarNode child = new AStarNode(this, expanded, new Vector2d(x + 0, y + 1));
                child.f(goal, room.getMovings(), room.getPlayers());

                if (a_top == null || (a_top != null && child.f() <= a_top.f())) {
                    open.add(child);
                    visited[y + 1][x + 0] = child;
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
