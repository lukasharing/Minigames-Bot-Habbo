package extensions.fastmap.MiniGames;

import extensions.fastmap.FastMap;
import extensions.fastmap.Map.*;
import javafx.scene.canvas.GraphicsContext;

import javax.vecmath.Vector2d;

public class MiniGameController {

    FastMap parent;
    public FastMap getParent(){ return parent; };

    public MiniGameController(FastMap parent){
        this.parent = parent;
    }
    public Room getRoom(){ return parent.getRoom(); };

    public void update() {
    }

    public void init() {
    }

    public void render(GraphicsContext ctx) {
    }

    public double h(AStarNode node, Vector2d goal, RoomMovingManager movings, RoomPlayerManager users){ return 0.0; }
}
