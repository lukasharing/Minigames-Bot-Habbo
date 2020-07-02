package extensions.fastmap.Map;

import extensions.fastmap.FurniData.FurniData;
import gearth.extensions.parsers.HFloorItem;

import java.util.HashMap;
import java.util.Map;

public class RoomTile {
    HashMap<Integer, HFloorItem> furnis;
    double height_value;
    boolean is_transitable;
    public boolean isTransitable(){ return is_transitable; };

    boolean can_sit;
    boolean is_wall;

    Room parent;
    RoomTile(Room parent, boolean wall){
        reset();

        this.parent = parent;
        this.furnis = new HashMap<Integer, HFloorItem>();
        this.is_wall = wall;
        this.is_transitable = !wall;
    }

    void reset(){
        this.height_value = 0.0;
        this.is_transitable = !is_wall;
    }

    void calculate(HFloorItem item){
        FurniData furni_data = parent.getFurniData(item.getTypeId());
        double height = item.getHeight();
        // There are some cases where it has no height and can not pass through
        if(item.getHeight() == 0.0 && !furni_data.canStandOn()){
            height = 1.0;
        }
        height_value = Math.max(height_value, item.getTile().getZ() + height);
        if(furni_data.canSitOn()) is_transitable = can_sit = true;
        if(!furni_data.canStandOn()) is_transitable = false;
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
}
