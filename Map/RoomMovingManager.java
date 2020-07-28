package extensions.fastmap.Map;

import extensions.fastmap.FastMap;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.vecmath.Vector2d;
import java.util.HashMap;
import java.util.Map;

public class RoomMovingManager {

    HashMap<Integer, MovingObject> movings;
    public HashMap<Integer, MovingObject> getElements(){ return movings; };

    private Room parent;

    RoomMovingManager(Room parent){
        this.parent = parent;
        this.movings = new HashMap<Integer, MovingObject>();

        // Hash
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "ObjectOnRoller", this::moving_object);
    };


    public void clear(){
        this.movings.clear();
    };

    public void moving_object(HMessage message) {
        if(!parent.isCreated()) return;

        HPacket packet = message.getPacket();

        int fx = packet.readInteger();
        int fy = packet.readInteger();
        int tx = packet.readInteger();
        int ty = packet.readInteger();
        int c = packet.readInteger();
        int id_furni = packet.readInteger();

        String zs = packet.readString();
        // There are some entities that has no z value, for example player on a roller.
        if(zs.isEmpty()) return;
        double z = Double.parseDouble(zs);

        int g = packet.readInteger();
        byte h = packet.readByte();
        int pi = packet.readInteger();

        if(movings.containsKey(id_furni)){
            movings.get(id_furni).set(tx, ty, z);
            parent.update_item(tx, ty, parent.getFurniByID(id_furni));
        }else{
            movings.put(id_furni, new MovingObject(fx, fy, z));
        }
    };

    public MovingObject get(Integer idx){ return movings.get(idx); };


    public void update(double delta) {

        float amplification = 1.1f;
        float h = (float) Math.min(delta * amplification, 1.0); // [0 - 1) -> [0 - 1.1) -> [0 - 1]

        for (Map.Entry<Integer, MovingObject> entry : movings.entrySet()) {
            MovingObject moving = entry.getValue();
            moving.interpolation2D(h);
        }

    };

    public void render(GraphicsContext ctx) {
        // Draw Moving Entities
        for (Map.Entry<Integer, MovingObject> entry : movings.entrySet()) {
            MovingObject moving = entry.getValue();
            int padding = 4;
            int radius = Room.TILE_SIZE - 2 * padding;
            ctx.save();
                ctx.translate(Room.TILE_SIZE * moving.ix(), Room.TILE_SIZE * moving.iy());
                ctx.setFill(Color.RED);
                ctx.fillOval(padding, padding, radius, radius);
            ctx.restore();
        }
    };
}
