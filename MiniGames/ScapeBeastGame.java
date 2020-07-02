package extensions.fastmap.MiniGames;

import extensions.fastmap.FastMap;
import extensions.fastmap.Map.AStar;
import extensions.fastmap.Map.AStarNode;
import extensions.fastmap.Map.Room;
import extensions.fastmap.MiniGames.MiniGameController;

import javax.vecmath.Vector2d;

public class ScapeBeastGame extends MiniGameController {

    // Huye
    int RADIUS = 3;
    double EnemyFactor = 100.0;

    private Vector2d destiny = null;
    public void setDestiny(Vector2d new_destiny){ this.destiny = new_destiny; };

    private final FastMap parent;
    public ScapeBeastGame(FastMap parent){
        this.parent = parent;
    }

    @Override
    public void update() {
        Room room = parent.getRoom();
        Vector2d hero_position = room.getHeroPosition();

        double f = 1e10;
        Vector2d best = null;

        if(destiny == null) {
            for (int j = -RADIUS; j <= RADIUS; ++j) {
                for (int i = -RADIUS; i <= RADIUS; ++i) {
                    Vector2d possible = new Vector2d(hero_position.getX() + i, hero_position.getY() + j);
                    if (room.line_transitable(possible, hero_position)) {
                        AStarNode path = parent.getRoom().AStar(hero_position,  possible);
                        if (path != null && path.fcal < f) {
                            f = path.fcal;
                            best = possible;
                        }
                    }
                }
            }
        }else{
            AStarNode path = parent.getRoom().AStar(hero_position, destiny);
            best = AStar.backtracking_action(path);
            if(best == null){
                destiny = null;
            }
        }

        if(best != null){
            parent.sendToServer("RoomUserWalk", (int)best.x, (int)best.y);
        }
    }
}
