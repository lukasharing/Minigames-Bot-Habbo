package extensions.fastmap.MiniGames;

import extensions.fastmap.FastMap;
import extensions.fastmap.Map.RoomTile;
import gearth.extensions.parsers.HFloorItem;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import javafx.scene.image.Image;

import javax.vecmath.Vector2d;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HandItemGame extends MiniGameController {

    private HashMap<String, Integer> held;

    private long last_pick_time;
    private int found_item_hand_id;
    private boolean next_pick;

    private List<String> possible_pick_item;

    public HandItemGame(FastMap parent) {
        super(parent);
        // Initial Variables
        possible_pick_item = new ArrayList<>();
        next_pick = true;
        found_item_hand_id = -1;
        last_pick_time = System.currentTimeMillis();

        // Held Item Id
        held = new HashMap<String, Integer>();
        held.put("Té", 1);
        held.put("Zumo", 2);
        held.put("Zanahoria", 3); // Agujero Negro
        //parent.zanahoria_img.setImage(new Image("src/main/resources/Zanahoria.png"));
        held.put("Helado", 4);
        held.put("Leche", 5);
        held.put("Grosella", 6);
        held.put("Agua", 7);
        held.put("Café Solo", 8);
        held.put("Agua", 9);
        //held.put("Té", 10);
        held.put("Mocha", 11);
        held.put("Macchiato", 12);
        held.put("Espresso", 13);
        held.put("Filtro", 14);
        held.put("Chocolate caliente", 15);
        held.put("Cappuccino", 16);
        held.put("Java", 17);
        held.put("Grifo", 18);
        held.put("Habbo Cola", 19);
        held.put("Cámara", 20);
        held.put("Hamburguesa", 21);
        held.put("Soda de lima", 22);
        held.put("Habbo Soda de remolacha", 23);
        held.put("Refresco burbujeante de 1978", 24);
        held.put("Brebaje del amor", 25);
        held.put("Calippo", 26);
        //held.put("Té", 27);
        held.put("Sake", 28); // Agujero Negro
        //sake_img.setImage(new Image("extensions/fastmap/assets/Sake.png"));
        held.put("Zumo de tomate", 29);
        held.put("Líquido radioactivo", 30);
        held.put("Champín", 31);
        held.put("Habbo Soda de remolacha", 32);
        held.put("handitem33", 33);
        held.put("Pescado fresco", 34);
        held.put("Champín", 35);
        held.put("pera", 36); // Agujero Negro
        //pera_img.setImage(new Image("extensions/fastmap/assets/pera.png"));
        held.put("Melocotón delicioso", 37); // Agujero Negro
        held.put("naranja", 38); // Agujero Negro
        held.put("Rebanada de queso", 39); // Agujero Negro
        held.put("Zumo de naranja", 40);
        held.put("Sumppi-kuppi", 41);
        held.put("Zumo de naranja", 42);
        held.put("Limonada", 43);
        held.put("Agua galáctica", 44);
        held.put("handitem45", 45);
        held.put("Brebaje Malhumor", 46);
        held.put("handitem47", 47);
        held.put("Chupa Chups", 48);
        held.put("handitem49", 49);
        held.put("Botella de Jugo Bubble", 50);
        held.put("Pipas G", 51);
        held.put("Cheetos", 52);
        held.put("Espresso", 53);
        held.put("Chocapic", 54);
        held.put("Botella de Pepsi", 55);
        held.put("Bolsa de Cheetos", 56);
        held.put("Zumo de uva", 57);
        held.put("Copa de sangre", 58); // Agujero Negro
        held.put("handitem59", 59);
        held.put("Castañas", 60);
        held.put("Sunny", 61);
        held.put("Agua Envenenada", 62);
        held.put("Palomitas", 63);
        held.put("handitem64", 64);
        held.put("Spray", 65);
        held.put("Batido de banana", 66);
        held.put("Chicle azul", 67);
        held.put("Chicle rojo", 68);
        held.put("Chicle verde", 69);
        held.put("Muslo de pollo", 70);
        held.put("Tostada", 71); // Agujero Negro
        held.put("Lata de refresco", 72);
        held.put("Ponche de Huevo", 73);
        held.put("Copa de Brindis", 74);
        held.put("Helado de fresa", 75);
        held.put("Helado de menta", 76);
        held.put("Helado de chocolate", 77);
        held.put("handitem78", 78);
        held.put("Algodón de azúcar rosa", 79);
        held.put("Algodón de azúcar azul", 80);
        held.put("Perrito caliente", 81);
        held.put("Telescopio", 82);
        held.put("Zumo de manzana", 83);
        held.put("Galleta de Jengibre", 84);
        held.put("Americano", 85);
        held.put("Frappuccino", 86);
        held.put("Cubo con agua", 87);
        held.put("Botella de cowboy", 88);
        held.put("Cupcake", 89);
        held.put("handitem90", 90);
        held.put("handitem91", 91);
        held.put("Chicle Azul", 92);
        held.put("Chicle Rojo", 93);
        held.put("Chicle Verde", 94);
        held.put("handitem95", 95);
        held.put("Trozo de tarta", 96);
        held.put("Croissant", 97);
        held.put("Tomate", 98);
        held.put("Berenjena", 99);
        held.put("Repollo", 100);
        held.put("Jugo Bubble con Gas", 101);
        held.put("Bebida Energética", 102);
        held.put("¡Banana!", 103);
        held.put("Aguacate", 104);
        held.put("Uvas", 105);
        held.put("Batido", 106);
        held.put("Zumito Vegetal", 107);
        held.put("handitem108", 108);
        held.put("Hamburguesa", 109);
        held.put("handitem110", 110);
        held.put("cangrejo", 111);
        held.put("Chili rojo", 112);
        held.put("Smoothie de cítricos", 113);
        held.put("Smoothie verde", 114);
        held.put("Smoothie de bayas", 115);
        held.put("Limón", 116);
        held.put("Cookie", 117);
        held.put("Ramune Rosa", 118);
        held.put("Ramune Azul", 119);
        held.put("Granizado de arándanos", 120);

        held.put("Píldoras", 1013); // Agujero Negro
        held.put("Jeringuilla", 1014); // Agujero Negro
        held.put("Bolsa de Residuos Tóxicos", 1015); // Agujero Negro
        held.put("handitem1016", 1016);
        held.put("handitem1017", 1017);
        held.put("handitem1018", 1018);
        held.put("Flor Bolly", 1019); // Agujero Negro
        held.put("Globo", 1029); // Agujero Negro
        held.put("HiPad", 1030);
        held.put("Antorcha Habbo-lympix", 1031); // Agujero Negro
        held.put("Comandante Tom", 1032); // Agujero Negro
        held.put("OVNI", 1033); // Agujero Negro
        held.put("Cosa alienígena", 1034); // Agujero Negro
        held.put("Llave inglesa", 1035); // Agujero Negro
        held.put("Patito de goma", 1036); // Agujero Negro
        held.put("Serpiente", 1037); // Agujero Negro
        held.put("Palo", 1038); // Agujero Negro
        held.put("Mano Cortada", 1039);
        held.put("Corazón", 1040);
        held.put("Pincel", 1051);  // Agujero Negro

        // Hash
        parent.getHash().intercept(HMessage.Direction.TOCLIENT, "RoomUserHandItem", this::HandItemPacket);
    }

    @Override
    public void init(){
        parent.writeToConsole("INITIALIZE \"HAND ITEM\" GAME");
        possible_pick_item.clear();
        found_item_hand_id = -1;
        next_pick = true;
        last_pick_time = 0L;

        if(parent.queso.isSelected()){ possible_pick_item.add(parent.queso.getText()); }
        if(parent.palo.isSelected()){ possible_pick_item.add(parent.palo.getText()); }
        if(parent.serpiente.isSelected()){ possible_pick_item.add(parent.serpiente.getText()); }
        if(parent.patito.isSelected()){ possible_pick_item.add(parent.patito.getText()); }
        if(parent.alienigena.isSelected()){ possible_pick_item.add(parent.alienigena.getText()); }
        if(parent.ovni.isSelected()){ possible_pick_item.add(parent.ovni.getText()); }
        if(parent.comandante.isSelected()){ possible_pick_item.add(parent.comandante.getText()); }
        if(parent.llave.isSelected()){ possible_pick_item.add(parent.llave.getText()); }
        if(parent.te.isSelected()){ possible_pick_item.add(parent.te.getText()); }
        if(parent.tostada.isSelected()){ possible_pick_item.add(parent.tostada.getText()); }
        if(parent.copa_sangre.isSelected()){ possible_pick_item.add(parent.copa_sangre.getText()); }
        if(parent.antorcha.isSelected()){ possible_pick_item.add(parent.antorcha.getText()); }
        if(parent.naranja.isSelected()){ possible_pick_item.add(parent.naranja.getText()); }
        if(parent.pera.isSelected()){ possible_pick_item.add(parent.pera.getText()); }
        if(parent.sake.isSelected()){ possible_pick_item.add(parent.sake.getText()); }
        if(parent.zanahoria.isSelected()){ possible_pick_item.add(parent.zanahoria.getText()); }
        if(parent.zumo_tomate.isSelected()){ possible_pick_item.add(parent.zumo_tomate.getText()); }
        if(parent.melocoton.isSelected()){ possible_pick_item.add(parent.melocoton.getText()); }
        if(parent.globo.isSelected()){ possible_pick_item.add(parent.globo.getText()); }
        if(parent.jeringuilla.isSelected()){ possible_pick_item.add(parent.jeringuilla.getText()); }
        if(parent.pildoras.isSelected()){ possible_pick_item.add(parent.pildoras.getText()); }
        if(parent.flor.isSelected()){ possible_pick_item.add(parent.flor.getText()); }
        if(parent.pez.isSelected()){ possible_pick_item.add(parent.pez.getText()); }
        if(parent.muslo.isSelected()){ possible_pick_item.add(parent.muslo.getText()); }
        if(parent.bolsa.isSelected()){ possible_pick_item.add(parent.bolsa.getText()); }
        if(parent.pincel.isSelected()){ possible_pick_item.add(parent.pincel.getText()); }

        parent.writeToConsole("Items to Pick: " + String.join(", ", possible_pick_item));
    }

    private void HandItemPacket(HMessage message) {
        if(!parent.isCurrentGame(TypeMiniGame.LISTA_COMPRA)) return;

        HPacket packet = message.getPacket();

        int index_player = packet.readInteger();
        int id_hand_item = packet.readInteger();

        if(index_player != parent.getRoom().getHero().getIndex()) return;

        for(String pick_item_name : possible_pick_item){
            if(held.get(pick_item_name) == id_hand_item){
                found_item_hand_id = id_hand_item;
                parent.writeToConsole("Found Item: " + pick_item_name);
                possible_pick_item.clear();
                break;
            }
        }

        next_pick = true;
    }

    @Override
    public void update(){
        Vector2d me_position = parent.getRoom().getHero2DPosition();

        HFloorItem agujero = null;
        for(int j = -1; j <= 1 && agujero == null; ++j){
            for(int i = -1; i <= 1 && agujero == null; ++i){
                Vector2d pos = new Vector2d(i + me_position.getX(), j + me_position.getY());
                RoomTile tile = parent.getRoom().getTile((int)pos.getX(), (int)pos.getY());
                if (tile != null) {
                    HFloorItem current = tile.getItem("Agujero Negro");
                    if (current != null && parent.getFurniData(current.getTypeId()).getCategory().equals("vending_machine")) {
                        agujero = current;
                    }
                }
            }
        }

        long delay_value = 0; //delay.getText().matches("^(0|[1-9]\\d*)") ? 1000 : Long.parseLong(delay.getText());
        if(agujero != null && !possible_pick_item.isEmpty() && next_pick){
            long current_ms = System.currentTimeMillis();
            if((current_ms - last_pick_time) >= delay_value){
                parent.sendToServer("ToggleFloorItem", agujero.getId(), 0);
                last_pick_time = current_ms;
                next_pick = false;
            }
        }
    }
}
