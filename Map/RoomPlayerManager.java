package extensions.fastmap.Map;

import extensions.fastmap.Map.Room;
import gearth.extensions.parsers.HEntity;
import gearth.extensions.parsers.HEntityUpdate;
import gearth.extensions.parsers.HPoint;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.util.HashMap;
import java.util.Map;

public class RoomPlayerManager {

    HashMap<Integer, HEntity> users;
    public HashMap<Integer, HEntity> getElements(){ return users; };

    private Room parent;

    RoomPlayerManager(Room parent){
        this.parent = parent;
        this.users = new HashMap<Integer, HEntity>(75);

        // Room Users
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RoomUsers", this::add_user);
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RoomUserStatus", this::update_user);
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RoomUserRemove", this::remove_user);
    }

    public void clear() {
        this.users.clear();
    }

    public void add_user(HMessage message) {
        if(this.parent.room.isEmpty()) return; // Wait until Map is fetched

        HPacket packet = message.getPacket();

        HEntity[] entities = HEntity.parse(packet);
        for(HEntity entity : entities){
            users.put(entity.getIndex(), entity);

            if(entity.getName().equals(this.parent.getHeroName())){
                parent.setHero(entity);
            }
        }
    }

    public void update_user(HMessage message) {
        if(this.parent.room.isEmpty()) return; // Wait until Map is fetched

        HPacket packet = message.getPacket();
        HEntityUpdate[] entities_updates = HEntityUpdate.parse(message.getPacket());
        for(HEntityUpdate entity_update : entities_updates){
            users.get(entity_update.getIndex()).tryUpdate(entity_update);
        }
    }

    public void remove_user(HMessage message) {
        if(this.parent.room.isEmpty()) return; // Wait until Map is fetched

        HPacket packet = message.getPacket();
        int index_user = Integer.parseInt(packet.readString());
        users.remove(index_user);
    }


    public void render(GraphicsContext ctx) {
        for(Map.Entry<Integer, HEntity> entry : users.entrySet()) {
            HEntity entity = entry.getValue();
            int padding = 2;
            int radius = Room.TILE_SIZE - 2 * padding;
            ctx.save();
            ctx.translate(Room.TILE_SIZE * entity.getTile().getX(), Room.TILE_SIZE * entity.getTile().getY());
            ctx.setFill(parent.getHeroName().equals(entity.getName()) ? Color.GREEN : Color.ORANGE);
            ctx.fillOval(padding, padding, radius, radius);
            ctx.restore();
        }
    }
}
