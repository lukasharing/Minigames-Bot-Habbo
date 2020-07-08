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
    private int RADIUS = 3;
    private double EnemyFactor = 100.0;
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
            double length = p.length();
            if(length < 3.0) {
                heuristic += Math.exp(-length) * 10.0;
            }
        }
        return heuristic;
    };

    public double hPlayers(Vector2d pos) {
        double heuristic = 0.0;
        for (Map.Entry<Integer, HEntity> entry : parent.getRoom().getPlayers().getElements().entrySet()) {
            HPoint position = entry.getValue().getTile();
            Vector2d p = new Vector2d(position.getX(), position.getY());
            p.sub(pos);
            double length = p.length();
            if(length < 2.0) {
                heuristic += Math.exp(-length) * 5.0;
            }
        }
        return heuristic;
    };

    @Override
    public double h(Vector2d goal, RoomMovingManager movings, RoomPlayerManager users){
        Vector2d hero = parent.getRoom().getHero2DPosition();
        Vector2d goal_copy = (Vector2d)goal.clone();
        goal_copy.sub(hero);

        double heuristic = 0.0;
        //heuristic += hMovings(hero);
        //heuristic += hPlayers(hero);

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

        double best_f = 1e10;
        if(destiny == null) {
            /*for (int j = -RADIUS; j <= RADIUS; ++j) {
                for (int i = -RADIUS; i <= RADIUS; ++i) {
                    Vector2d possible = new Vector2d(hero_position.getX() + i, hero_position.getY() + j);
                    if (room.segmentTransitable(possible, hero_position)) {
                        AStarNode path = search_algorithm.algorithm(hero_position,  possible);
                        double current_f = path.f();
                        if (path != null && current_f < best_f) {
                            best_f = current_f;
                            best_path = path;
                        }
                    }
                }
            }*/
        }else{
            best_path = search_algorithm.algorithm(hero_position, destiny);
        }

        if(best_path != null){
            if(best_path.getPosition().equals(hero_position)){
                destiny = null;
            }else{
                Vector2d best = AStar.backtracking_action(best_path);
                parent.writeToConsole(":> "+(int) best.x + "," + (int) best.y);
                //parent.sendToServer("RoomUserWalk", (int)best.x, (int)best.y);
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
