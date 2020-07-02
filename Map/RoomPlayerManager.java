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
import java.util.HashMap;
import java.util.Map;

public class RoomPlayerManager {

    HashMap<Integer, HEntity> users;
    HashMap<Integer, HPoint> users_position;

    Room parent;

    RoomPlayerManager(Room parent){
        this.parent = parent;
        this.users = new HashMap<Integer, HEntity>(75);
        this.users_position = new HashMap<Integer, HPoint>(75);

        // Room Users
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RoomUsers", this::add_user);
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RoomUserStatus", this::update_user);
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RoomUserRemove", this::remove_user);
    }

    public void add_user(HMessage message) {
        if(this.parent.room.isEmpty()) return; // Wait until Map is fetched

        HPacket packet = message.getPacket();

        HEntity[] entities = HEntity.parse(packet);
        for(HEntity entity : entities){
            users.put(entity.getIndex(), entity);
            users_position.put(entity.getIndex(), entity.getTile());

            if(entity.getName().equals(this.parent.getHeroName())){
                this.parent.setHero(entity);
                this.parent.setHeroPosition(new Vector2d(entity.getTile().getX(), entity.getTile().getY()));
            }
        }
    }

    public void update_user(HMessage message) {
        if(this.parent.room.isEmpty()) return; // Wait until Map is fetched

        HPacket packet = message.getPacket();

        HEntityUpdate[] entities_updates = HEntityUpdate.parse(message.getPacket());
        for(HEntityUpdate entity_update : entities_updates){
            users_position.put(entity_update.getIndex(), entity_update.getTile());
            if(users.get(entity_update.getIndex()).getName().equals(parent.getHeroName())){
                parent.setHeroPosition(new Vector2d(entity_update.getTile().getX(), entity_update.getTile().getY()));
            }
        }
    }

    public void remove_user(HMessage message) {
        if(this.parent.room.isEmpty()) return; // Wait until Map is fetched

        HPacket packet = message.getPacket();
        int index_user = Integer.parseInt(packet.readString());
        users.remove(index_user);
        users_position.remove(index_user);
    }


    public void render(GraphicsContext ctx) {
        for(Map.Entry<Integer, HEntity> entry : users.entrySet()) {
            HEntity entity = entry.getValue();
            HPoint position = users_position.get(entity.getIndex());
            int padding = 2;
            int radius = Room.TILE_SIZE - 2 * padding;
            ctx.save();
            ctx.translate(Room.TILE_SIZE * position.getX(), Room.TILE_SIZE * position.getY());
            ctx.setFill(parent.getHeroName().equals(entity.getName()) ? Color.GREEN : Color.ORANGE);
            ctx.fillOval(padding, padding, radius, radius);
            ctx.restore();
        }
    }

    public double h(Vector2d pos) {
        double heuristic = 0.0;
        for (Map.Entry<Integer, HEntity> entry : users.entrySet()) {
            HPoint position = entry.getValue().getTile();
            Vector2d p = new Vector2d(position.getX(), position.getY());
            p.sub(pos);
            double length = p.length();
            if(length < 2.0) {
                heuristic += Math.exp(-length) * 5.0;
            }
        }
        return heuristic;
    }
}
