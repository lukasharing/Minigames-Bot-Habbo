package extensions.fastmap.MiniGames;

import extensions.fastmap.FastMap;
import extensions.fastmap.Map.*;
import extensions.fastmap.MiniGames.MiniGameController;
import gearth.extensions.parsers.HEntity;
import gearth.extensions.parsers.HPoint;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.util.Map;

public class ScapeBeastGame extends MiniGameController {

    // Huye
    private int RADIUS = 4;
    private int ticks;


    private AStarNode best_path = null;
    private Vector2d destiny;
    public void setDestiny(Vector2d new_destiny){ this.destiny = new_destiny; };

    private AStar search_algorithm;

    public ScapeBeastGame(FastMap parent){
        super(parent);
        this.destiny = null;
        this.search_algorithm = new AStar(this);
    }

    public double hMovings(Vector2d pos) {
        double heuristic = 0.0;
        for (Map.Entry<Integer, MovingObject> entry : parent.getRoom().getMovings().getElements().entrySet()) {
            Vector2d p = entry.getValue().position();
            p.sub(pos);
            heuristic += Math.exp(-p.length() * 2.0) * 50.0;
        }
        return heuristic;
    };

    @Override
    public double h(AStarNode node, Vector2d goal, RoomMovingManager movings, RoomPlayerManager users){
        Vector2d position = node.getPosition();
        Vector2d goal_copy = (Vector2d)goal.clone();
        goal_copy.sub(position);

        double heuristic = 0.0;
        heuristic += hMovings(position);

        return goal_copy.length() + heuristic;
    };

    @Override
    public void init(){
        parent.writeToConsole("INITIALIZE \"SCAPE THE BEAST\" GAME");
        this.ticks = -1;
        this.destiny = null;
    }

    @Override
    public void update() {
        ticks = (ticks + 1) % 10;
        if(ticks != 0) return;

        Room room = parent.getRoom();
        Vector2d hero_position = room.getHero2DPosition();

        // The path has to be optimal and the longest possible
        double best_f = 1e10;
        double longest_path = 0.0;
        if(destiny == null) {

            for (int j = -RADIUS; j <= RADIUS; ++j) {
                for (int i = -RADIUS; i <= RADIUS; ++i) {
                    Vector2d possible = new Vector2d(hero_position.getX() + i, hero_position.getY() + j);
                    if (room.segmentTransitable(possible, hero_position)) {
                        AStarNode path = search_algorithm.algorithm(hero_position,  possible);
                        double current_f = path.f();
                        double path_distance = i * i + j * j;
                        if (path != null && current_f < best_f && path_distance >= longest_path) {
                            best_f = current_f;
                            longest_path = path_distance;
                            best_path = path;
                        }
                    }
                }
            }
        }else{
            best_path = search_algorithm.algorithm(hero_position, destiny);
        }

        if(best_path != null){
            if(best_path.getPosition().equals(hero_position)){
                destiny = null;
            }else{
                Vector2d best = best_path.getPosition();//;AStar.backtracking_action(best_path);
                parent.sendToServer("RoomUserWalk", (int)best.x, (int)best.y);
            }
        }
    }

    public void render(GraphicsContext ctx){

        AStarNode backtracking = best_path;

        if(backtracking == null) return;

        ctx.save();
            ctx.translate(backtracking.getPosition().getX() * Room.TILE_SIZE, backtracking.getPosition().getY() * Room.TILE_SIZE);
            ctx.setFill(Color.ORANGE);
            ctx.fillRect(0, 0, Room.TILE_SIZE, Room.TILE_SIZE);
        ctx.restore();

        if(backtracking.getFrom() == null) return;

        // Backtracking
        while (backtracking.getFrom().getFrom() != null) {
            backtracking = backtracking.getFrom();
            ctx.save();
                ctx.translate(backtracking.getPosition().getX() * Room.TILE_SIZE, backtracking.getPosition().getY() * Room.TILE_SIZE);
                ctx.setFill(Color.ORANGERED);
                ctx.fillRect(0, 0, Room.TILE_SIZE, Room.TILE_SIZE);
            ctx.restore();
        }

    }
}
