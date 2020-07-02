package extensions.fastmap.FurniData;

import jdk.jfr.Category;

public class FurniData {
    String name;
    public String getName() { return name; }
    String category;
    public String getCategory() { return category; }
    String description;
    public int w;
    public int h;

    boolean stand_on = false;
    public boolean canStandOn(){ return stand_on; };
    boolean sit_on = false;
    public boolean canSitOn(){ return sit_on; };
    boolean lay_on = false;
    public boolean canLayOn(){ return lay_on; };
    public void setStand(boolean on){ stand_on = on; }
    public void setSit(boolean on){ sit_on = on; }
    public void setLay(boolean on){ lay_on = on; }
    FurniData(String _n, String _c, String _d, int _w, int _h){
        name = _n;
        category = _c;
        description = _d;
        w = _w;
        h = _h;
    }

}
