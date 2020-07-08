package extensions.fastmap.Map;

import extensions.fastmap.FurniData.FurniData;
import gearth.extensions.parsers.HDirection;
import gearth.extensions.parsers.HFloorItem;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class RoomTile {
    private HashMap<Integer, HFloorItem> furnis;
    private double height_value;
    public double getHeight(){ return height_value; };

    public boolean isTransitable(){ return (!is_wall && (can_stand || can_sit)) && (height_value - parent.getHero3DPosition().getZ()) >= -1; };

    private boolean can_sit;
    public boolean canSit(){ return can_sit; };
    private int rotation;
    public int rotation(){ return rotation; };
    private boolean is_wall;
    public boolean isWall(){ return is_wall; };
    private boolean can_stand;
    public boolean canStand(){ return can_stand; };

    Room parent;
    RoomTile(Room parent, boolean wall){
        reset();

        this.parent = parent;
        this.furnis = new HashMap<Integer, HFloorItem>();
        this.is_wall = wall;
        this.can_sit = false;
        this.can_stand = true;
    }

    void reset(){
        this.height_value = 0.0;
        this.can_sit = false;
        this.can_stand = true;
    }

    void calculate(HFloorItem item){
        FurniData furni_data = parent.getFurniData(item.getTypeId());
        // There are some cases where it has no height and can not pass through
        if(item.getHeight() == 0.0 && !furni_data.canStandOn()){
            height_value += 1.0;
        }else{
            height_value = Math.max(height_value, item.getTile().getZ() + item.getHeight());
        }

        can_sit |= furni_data.canSitOn();
        if(furni_data.canSitOn()){
            rotation = (item.getFacing() == HDirection.South) ? 90 :
                       (item.getFacing() == HDirection.North) ? 270 :
                       (item.getFacing() == HDirection.East) ? 0 :
                       (item.getFacing() == HDirection.West) ? 180 : 0;
        }
        can_stand &= furni_data.canStandOn();
    }

    public void addFurni(HFloorItem item){
        calculate(item);
        furnis.put(item.getId(), item);
    }

    public void removeFurni(HFloorItem item){
        furnis.remove(item.getId());
        // Re-Calculate
        reset();
        for(Map.Entry<Integer, HFloorItem> items : furnis.entrySet()){
            calculate(items.getValue());
        }
    }

    public HFloorItem getItem(String item_name) {
        if(is_wall) return null;

        for(HFloorItem item : furnis.values()) {
            if(parent.getFurniData(item.getTypeId()).getName().equals(item_name)) {
                return item;
            }
        }
        return null;
    }

    public void render(GraphicsContext ctx) {
        if(is_wall) return;

        if (height_value > 0.0) {
            int hue = (int) (height_value  / 20.0 * 360.0);
            ctx.setFill(Color.hsb(hue, 1.0, 0.80));
            ctx.fillRect(0, 0, Room.TILE_SIZE, Room.TILE_SIZE);
            if(can_sit){
                ctx.save();
                    ctx.translate(+Room.TILE_SIZE / 2, +Room.TILE_SIZE / 2);
                    ctx.rotate(rotation);
                    ctx.translate(-Room.TILE_SIZE / 2, -Room.TILE_SIZE / 2);
                    ctx.setFill(Color.BLACK);
                    ctx.fillRect(0, 0, 4, Room.TILE_SIZE);
                ctx.restore();
            }
        }
        ctx.setStroke(Color.BLACK);
        ctx.strokeRect(0, 0, Room.TILE_SIZE, Room.TILE_SIZE);
    }
}
