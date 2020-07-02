package extensions.fastmap.Map;

import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.vecmath.Vector2d;
import java.util.HashMap;
import java.util.Map;

public class RoomMovingManager {

    HashMap<Integer, MovingObject> movings;

    Room parent;

    RoomMovingManager(Room parent){
        this.parent = parent;
        this.movings = new HashMap<Integer, MovingObject>();

        // Hash
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "ObjectOnRoller", this::moving_object);
    }

    public void moving_object(HMessage message) {
        if(parent.isEmpty()) return; // Wait until Map is fetched
        HPacket packet = message.getPacket();

        int fx = packet.readInteger();
        int fy = packet.readInteger();
        int tx = packet.readInteger();
        int ty = packet.readInteger();
        int c = packet.readInteger();
        int id_furni = packet.readInteger();
        double z = Double.parseDouble(packet.readString());
        int g = packet.readInteger();
        byte h = packet.readByte();
        int pi = packet.readInteger();

        if(movings.containsKey(id_furni)){
            movings.get(id_furni).set(tx, ty, z);
        }else{
            movings.put(id_furni, new MovingObject(fx, fy, z));
        }
    }

    public MovingObject get(Integer idx){ return movings.get(idx); };

    public void render(GraphicsContext ctx) {
        // Draw Moving Entities
        for (Map.Entry<Integer, MovingObject> entry : movings.entrySet()) {
            MovingObject item_information = entry.getValue();
            int padding = 4;
            int radius = Room.TILE_SIZE - 2 * padding;
            ctx.save();
            ctx.translate(Room.TILE_SIZE * item_information.x, Room.TILE_SIZE * item_information.y);
            ctx.setFill(Color.RED);
            ctx.fillOval(padding, padding, radius, radius);
            ctx.restore();
        }
    }

    public double h(Vector2d pos) {
        double heuristic = 0.0;
        for (Map.Entry<Integer, MovingObject> entry : movings.entrySet()) {
            Vector2d p = entry.getValue().position();
            p.sub(pos);
            double length = p.length();
            if(length < 3.0) {
                heuristic += Math.exp(-p.length() * 2.0) * 10.0;
            }
        }
        return heuristic;
    }
}
