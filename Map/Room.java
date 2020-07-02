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
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class Room {

    public static String separator;
    static {
        try{
            separator = new String(new byte[]{13}, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public static Integer TILE_SIZE = 25;

    HashMap<Vector2d, RoomTile> room;

    public RoomTile getTile(int x, int y){ return room.get(new Vector2d(x, y)); };

    public boolean isEmpty(){ return room == null || room.isEmpty(); };

    private int room_width;
    private int room_height;

    public int width(){ return room_width; };
    public int height(){ return room_height; };

    String hero_name = "lukasdispo";
    public String getHeroName(){ return hero_name; };
    HEntity hero = null;
    Vector2d hero_position = null;

    public HEntity getHero(){ return hero; };
    public void setHero(HEntity hero){ this.hero = hero; };
    public Vector2d getHeroPosition(){ return hero_position; };
    public void setHeroPosition(Vector2d position){ this.hero_position = position; };

    private final RoomPlayerManager players;
    private final RoomMovingManager movings;
    private final FastMap parent;
    private final HashMap<Integer, HFloorItem> items;

    public RoomPlayerManager getPlayers(){ return players; };
    public RoomMovingManager getMovings(){ return movings; };

    public HashSupport getHash(){ return parent.getHash(); };

    public Room(FastMap parent, int room_id){
        this.parent = parent;
        this.room = new HashMap<>();
        this.players = new RoomPlayerManager(this);
        this.movings = new RoomMovingManager(this);
        this.items = new HashMap<Integer, HFloorItem>();

        // Hash
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RoomFloorItems", this::floor_items);
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "AddFloorItem", this::add_item);
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RemoveFloorItem", this::remove_item);
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "FloorItemUpdate", this::update_item);
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RoomHeightMap", this::generate_height_map);
    }

    public FurniData getFurniData(int id_furni){ return parent.getFurniData(id_furni); };


    public boolean transitable(Vector2d pos){
        if(pos.getX() < 0 || pos.getX() >= room_width || pos.getY() < 0 || pos.getY() >= room_height) return false;
        return room.get(pos).isTransitable();
    };

    public boolean line_transitable(Vector2d a, Vector2d b){

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

    public AStarNode AStar(Vector2d init, Vector2d goal){
        return parent.AStar(init, goal);
    }


    private void add_furni_to_cells(HFloorItem item){
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

    public void floor_items(HMessage message){
        if(room.isEmpty()) return; // Wait until Map is fetched

        HFloorItem[] floor_items = HFloorItem.parse(message.getPacket());

        for(HFloorItem floor_item : floor_items){
            items.put(floor_item.getId(), floor_item);
            if(movings.get(floor_item.getId()) == null) {
                add_furni_to_cells(floor_item);
            }
        }
    }

    private void remove_furni_from_cells(HFloorItem item){
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

    public void add_item(HMessage message){
        if(room.isEmpty()) return; // Wait until Map is fetched

        HPacket packet = message.getPacket();
        HFloorItem floor_item = new HFloorItem(packet);
        items.put(floor_item.getId(), floor_item);
        add_furni_to_cells(floor_item);
    }

    public void remove_item(HMessage message){
        if(room.isEmpty()) return; // Wait until Map is fetched

        HPacket packet = message.getPacket();

        int id_item = Integer.parseInt(packet.readString());
        boolean a = packet.readBoolean();
        int b = packet.readInteger();
        int c = packet.readInteger();
        remove_furni_from_cells(items.get(id_item));
        items.remove(id_item);
    }

    public void update_item(HMessage message){
        if(room.isEmpty()) return; // Wait until Map is fetched

        HPacket packet = message.getPacket();
        HFloorItem floor_item = new HFloorItem(packet);

        // Update Old Information
        if(items.containsKey(floor_item.getId())){
            remove_furni_from_cells(items.get(floor_item.getId()));
        }
        // Update
        items.put(floor_item.getId(), floor_item);
        add_furni_to_cells(floor_item);
    }

    public void generate_height_map(HMessage message){
        if(!room.isEmpty()) return; // Wait until Map is fetched

        HPacket packet = message.getPacket();

        int a = packet.readInteger();
        byte b = packet.readByte();

        String[] map_string = packet.readString().split(separator);

        room_height = map_string.length;
        room_width = map_string[0].length();
        for(int j = 0; j < room_height; ++j){
            String row = map_string[j];
            for(int i = 0; i < room_width; ++i){
                room.put(new Vector2d(i, j), new RoomTile(this, row.charAt(i) == 'x'));
            }
        }
    }

    public void render(GraphicsContext ctx){
        if(room.isEmpty()) return; // Wait until Map is fetched

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
                RoomTile cell = room.get(new Vector2d(i, j));
                if(!cell.is_wall) {
                    if (cell.height_value > 0.0) {
                        int hue = (int) (cell.height_value  / 20.0 * 360.0);
                        ctx.setFill(Color.hsb(hue, 1.0, 0.80));
                        ctx.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
                        if(cell.can_sit){
                            ctx.setFill(Color.BLACK);
                            ctx.fillRect(2, 2, 4, TILE_SIZE - 4);
                        }
                    }
                    ctx.setStroke(Color.BLACK);
                    ctx.strokeRect(0, 0, TILE_SIZE, TILE_SIZE);
                }
                ctx.translate(TILE_SIZE, 0);
            }
            ctx.restore();
            ctx.translate(0, TILE_SIZE);
        }
        ctx.restore();

        // Movings
        movings.render(ctx);

        // Draw Users
        players.render(ctx);

        // parent.getMouseManager().render(ctx);

        ctx.restore();
    }


}
