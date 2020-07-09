package extensions.fastmap.Map;

import extensions.fastmap.*;
import extensions.fastmap.FurniData.FurniData;
import gearth.extensions.extra.harble.HashSupport;
import gearth.extensions.parsers.*;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Room {

    public static String separator;
    static {
        try{
            separator = new String(new byte[]{13}, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private int room_id;
    public void setRoomID(int room_id){ this.room_id = room_id; };

    public static Integer TILE_SIZE = 25;

    HashMap<Vector2d, RoomTile> room;

    public RoomTile getTile(int x, int y){ return room.get(new Vector2d(x, y)); };

    public boolean isCreated(){ return room_id >= 0 && room.size() > 0; };

    private int room_width = 0;
    private int room_height = 0;

    public int width(){ return room_width; };
    public int height(){ return room_height; };

    String hero_name = "lukasdispo";
    public String getHeroName(){ return hero_name; };
    HEntity hero = null;

    public HEntity getHero(){ return hero; };
    public void setHero(HEntity hero){ this.hero = hero; };
    public Vector2d getHero2DPosition(){ return new Vector2d(hero.getTile().getX(), hero.getTile().getY()); };
    public Vector3d getHero3DPosition(){ return new Vector3d(hero.getTile().getX(), hero.getTile().getY(), hero.getTile().getZ()); };

    private final RoomPlayerManager players;
    private final RoomMovingManager movings;
    private final FastMap parent;
    public FastMap getParent(){ return parent; };

    private final HashMap<Integer, HFloorItem> items;
    public HFloorItem getFurniByID(Integer id){ return items.get(id); };

    public RoomPlayerManager getPlayers(){ return players; };
    public RoomMovingManager getMovings(){ return movings; };

    public HashSupport getHash(){ return parent.getHash(); };

    public Room(FastMap parent){
        this.room_id = -1;
        this.parent = parent;
        this.room = new HashMap<>();
        this.players = new RoomPlayerManager(this);
        this.movings = new RoomMovingManager(this);
        this.items = new HashMap<Integer, HFloorItem>();

        // PRIMARY Hash
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RoomHeightMap", this::generate_height_map);
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RoomFloorItems", this::floor_items);

        // SECONDARY Hash
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "AddFloorItem", this::add_item);
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "FloorItemUpdate", this::update_item);
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RemoveFloorItem", this::remove_item);
    }

    public void clear() {
        this.room_id = -1;
        this.players.clear();
        this.movings.clear();
        this.items.clear();
        this.room.clear();
    }

    public FurniData getFurniData(int id_furni){ return parent.getFurniData(id_furni); };

    public boolean transitable(Vector2d pos){
        if(pos.getX() < 0 || pos.getX() >= room_width || pos.getY() < 0 || pos.getY() >= room_height) return false;
        for (Map.Entry<Integer, HEntity> entry : players.getElements().entrySet()) {
            if(!entry.getValue().getName().equals(hero_name)) {
                HPoint tile = entry.getValue().getTile();
                Vector2d player_position = new Vector2d(tile.getX(), tile.getY());
                if (player_position.equals(pos)) {
                    return false;
                }
            }
        }

        return room.get(pos).isTransitable();
    };

    public boolean segmentTransitable(Vector2d a, Vector2d b){

        double ddx = b.getX() - a.getX();
        double ddy = b.getY() - a.getY();
        double dx = Math.abs(ddx);
        double dy = -Math.abs(ddy);
        double sx = Math.signum(ddx);
        double sy = Math.signum(ddy);
        double err = dx + dy;


        Vector2d c = (Vector2d)a.clone();
        while (true) {

            if (!transitable(c)) {
                return false;
            }

            if (c.getX() == b.getX() && c.getY() == b.getY()) break;

            double e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                c.add(new Vector2d(sx, 0.0));
            }
            if (e2 <= dx) {
                err += dx;
                c.add(new Vector2d(0.0, sy));
            }
        }
        return true;
    }

    public void add_furni_to_tiles(HFloorItem item){
        FurniData furnidata = parent.getFurniData(item.getTypeId());
        int p = (item.getFacing().equals(HDirection.North) || item.getFacing().equals(HDirection.South)) ? 0 : 1;
        for (int j = 0; j < furnidata.h; ++j) {
            for (int i = 0; i < furnidata.w; ++i) {
                int x = item.getTile().getX() + j * p + (1 - p) * i;
                int y = item.getTile().getY() + i * p + (1 - p) * j;
                room.get(new Vector2d(x, y)).addFurni(item);
            }
        }
    }

    public void remove_furni_from_tiles(HFloorItem item){
        FurniData furnidata = parent.getFurniData(item.getTypeId());
        int p = (item.getFacing().equals(HDirection.North) || item.getFacing().equals(HDirection.South)) ? 0 : 1;
        for (int j = 0; j < furnidata.h; ++j) {
            for (int i = 0; i < furnidata.w; ++i) {
                int x = item.getTile().getX() + j * p + (1 - p) * i;
                int y = item.getTile().getY() + i * p + (1 - p) * j;
                room.get(new Vector2d(x, y)).removeFurni(item);
            }
        }
    }

    // PRIMARY HASH
    public void generate_height_map(HMessage message){
        if(isCreated()) return;

        HPacket packet = message.getPacket();

        int a = packet.readInteger();
        byte b = packet.readByte();

        String[] map_string = packet.readString().split(separator);

        room_height = map_string.length;
        room_width = map_string[0].length();
        parent.writeToConsole("1. Creating Room of " + room_width + "x" + room_height + " tiles");
        for(int j = 0; j < room_height; ++j){
            String row = map_string[j];
            for(int i = 0; i < room_width; ++i){
                room.put(new Vector2d(i, j), new RoomTile(this, row.charAt(i) == 'x'));
            }
        }
    }

    public void floor_items(HMessage message){
        if(!isCreated()) return;

        HFloorItem[] floor_items = HFloorItem.parse(message.getPacket());

        for(HFloorItem floor_item : floor_items){
            items.put(floor_item.getId(), floor_item);
            if(movings.get(floor_item.getId()) == null) {
                add_furni_to_tiles(floor_item);
            }
        }
        parent.writeToConsole("2. Placing Furnis to room");
    }

    public void add_item(HMessage message){
        if(!isCreated()) return;

        HPacket packet = message.getPacket();
        HFloorItem floor_item = new HFloorItem(packet);
        items.put(floor_item.getId(), floor_item);
        add_furni_to_tiles(floor_item);
    }

    public void remove_item(HMessage message){
        if(!isCreated()) return;

        HPacket packet = message.getPacket();

        int id_item = Integer.parseInt(packet.readString());
        boolean a = packet.readBoolean();
        int b = packet.readInteger();
        int c = packet.readInteger();
        remove_furni_from_tiles(items.get(id_item));
        items.remove(id_item);
    }

    public void update_item(HMessage message){
        if(!isCreated()) return;

        HPacket packet = message.getPacket();
        HFloorItem floor_item = new HFloorItem(packet);

        // Update Old Information
        if(items.containsKey(floor_item.getId())){
            remove_furni_from_tiles(items.get(floor_item.getId()));
        }
        // Update
        items.put(floor_item.getId(), floor_item);
        add_furni_to_tiles(floor_item);
    }

    public void update_item(int x, int y, HFloorItem floor_item){
        // Update Old Information
        remove_furni_from_tiles(floor_item);
        floor_item.setTile(new HPoint(x, y, floor_item.getTile().getZ()));
        add_furni_to_tiles(floor_item);
    }

    public void render(GraphicsContext ctx){
        if(!isCreated()) return;

        int canvas_width = (int)parent.getCanvas().getWidth();
        int canvas_height = (int)parent.getCanvas().getHeight();

        ctx.clearRect(0, 0, canvas_width, canvas_height);

        int padding_x = (canvas_width - room_width * TILE_SIZE) / 2;
        int padding_y = (canvas_height - room_height * TILE_SIZE) / 2;
        ctx.save();
        ctx.translate(padding_x, padding_y);
        // Draw Grid and HeightMap
        ctx.save();
        for(int j = 0; j < room_height; ++j){
            ctx.save();
            for(int i = 0; i < room_width; ++i){
                RoomTile tile = room.get(new Vector2d(i, j));
                tile.render(ctx);
                ctx.translate(TILE_SIZE, 0);
            }
            ctx.restore();
            ctx.translate(0, TILE_SIZE);
        }
        ctx.restore();

        // Draw Minigame
        if(parent.getCurrentGame() != null){
            parent.getCurrentGame().render(ctx);
        }

        // Movings
        movings.render(ctx);

        // Draw Users
        players.render(ctx);

        // Daw Canvas UI
        parent.getEvents().render(ctx);

        ctx.restore();
    }
}
