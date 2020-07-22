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

    private int KERNEL_SIZE = 2;
    private double[][] GaussKernel = new double[][]{
            {0.00366, 0.01465, 0.02564, 0.01465, 0.00366},
            {0.01465, 0.05860, 0.09523, 0.05860, 0.01465},
            {0.02564, 0.09523, 0.15018, 0.09523, 0.02564},
            {0.01465, 0.05860, 0.09523, 0.05860, 0.01465},
            {0.00366, 0.01465, 0.02564, 0.01465, 0.00366}
    };
    private double EnemyFactor = 100.0;

    private double[][] heatmap;

    private AStarNode best_path = null;
    private Vector2d destiny;
    public void setDestiny(Vector2d new_destiny){ this.destiny = new_destiny; };

    private AStar search_algorithm;

    public ScapeBeastGame(FastMap parent){
        super(parent);
        this.destiny = null;
        this.heatmap = null;
        this.search_algorithm = new AStar(this);
    }

    private double discrete_integral(Vector2d pos, int r){
        double ht = 0.0;
        int x = (int)pos.getX();
        int y = (int)pos.getY();
        for (int j = -r; j <= r; ++j) {
            for (int i = -r; i <= r; ++i) {
                if(parent.getRoom().segmentTransitable(pos, new Vector2d(x + i, y + j))) {
                    ht += heatmap[y + j][x + i];
                }
            }
        }
        return ht;
    };

    @Override
    public double h(AStarNode node, Vector2d goal, RoomMovingManager movings, RoomPlayerManager users){
        Vector2d position = node.getPosition();
        Vector2d goal_copy = (Vector2d)goal.clone();
        goal_copy.sub(position);

        return goal_copy.length() + discrete_integral(position, 2) * EnemyFactor;
    };

    @Override
    public void init(){
        parent.writeToConsole("INITIALIZE \"SCAPE THE BEAST\" GAME");
        this.ticks = -1;
        this.destiny = null;
    }

    @Override
    public void update() {
        ticks = ++ticks % 5;
        if(ticks != 0) return;

        Room room = parent.getRoom();
        Vector2d hero_position = room.getHero2DPosition();

        // Clear Heatmap
        for(int j = 0; j < room.height(); ++j){
            for(int i = 0;i < room.width(); ++i){
                heatmap[j][i] = 0.0;
            }
        }

        for (Map.Entry<Integer, MovingObject> entry : parent.getRoom().getMovings().getElements().entrySet()) {
            int x = entry.getValue().x();
            int y = entry.getValue().y();
            for (int j = -KERNEL_SIZE; j <= KERNEL_SIZE; ++j) {
                for (int i = -KERNEL_SIZE; i <= KERNEL_SIZE; ++i) {
                    if (room.segmentTransitable(entry.getValue().position(), new Vector2d(x + i, y + j))) {
                        heatmap[y + j][x + i] += GaussKernel[j + KERNEL_SIZE][i + KERNEL_SIZE];
                    }
                }
            }
        }

        // The path has to be optimal and the longest possible
        double best_f = 1e10;
        double longest_path = 0.0;
        if(destiny == null) {
            /*for (int j = -RADIUS; j <= RADIUS; ++j) {
                for (int i = -RADIUS; i <= RADIUS; ++i) {
                    Vector2d possible = new Vector2d(hero_position.getX() + i, hero_position.getY() + j);
                    if (room.segmentTransitable(hero_position, possible)) {
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

        /*if(best_path == null || (best_path != null && best_path.getPosition().equals(hero_position))){
            destiny = null;
        }else{
            //Vector2d best = best_path.getPosition();//;AStar.backtracking_action(best_path);
            //parent.sendToServer("RoomUserWalk", (int)best.x, (int)best.y);
        }*/
    }

    public void render(GraphicsContext ctx){

        AStarNode backtracking = best_path;

        ctx.save();
        for(int j = 0; j < parent.getRoom().height(); ++j){
            ctx.save();
            for(int i = 0; i < parent.getRoom().width(); ++i){
                if(heatmap[j][i] > 0.0) {
                    int hue = (int) (heatmap[j][i] * 360.0);
                    ctx.setFill(Color.hsb(hue, 1.0, 1.0));
                    ctx.fillRect(0, 0, Room.TILE_SIZE, Room.TILE_SIZE);
                }
                ctx.translate(Room.TILE_SIZE, 0);
            }
            ctx.restore();
            ctx.translate(0, Room.TILE_SIZE);
        }
        ctx.restore();

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
