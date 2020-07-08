package extensions.fastmap;

import extensions.fastmap.Map.Room;
import extensions.fastmap.Map.RoomTile;
import extensions.fastmap.MiniGames.ScapeBeastGame;
import extensions.fastmap.MiniGames.TypeMiniGame;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javax.vecmath.Vector2d;

public class EventController {


    private Vector2d tile_index;

    private final FastMap parent;
    EventController(FastMap parent){
        this.parent = parent;
        this.tile_index = new Vector2d(-1.0, -1.0);
    }

    public void canvas_mousemove(MouseEvent mouseEvent) {
        // Wait until Map is fetched
        if(parent.getRoom() == null) return;

        int padding_x = (int)(parent.getCanvas().getWidth() - parent.getRoom().width() * Room.TILE_SIZE) / 2;
        int padding_y = (int)(parent.getCanvas().getHeight() - parent.getRoom().height() * Room.TILE_SIZE) / 2;

        tile_index = new Vector2d(
                (int)(mouseEvent.getX() - padding_x) / Room.TILE_SIZE,
                (int)(mouseEvent.getY() - padding_y) / Room.TILE_SIZE
        );
    }

    public void canvas_clicked(MouseEvent mouseEvent) {
        if(parent.getRoom() == null) return;
        // Check for existing tile
        RoomTile tile = parent.getRoom().getTile((int)tile_index.x, (int)tile_index.y);
        if(tile == null || !tile.isTransitable()) return;

        if (parent.isCurrentGame(TypeMiniGame.HUYE_ENEMIGO)) {
            ((ScapeBeastGame) parent.getCurrentGame()).setDestiny(tile_index);
        }else{
            parent.sendToServer("RoomUserWalk", (int)tile_index.getX(), (int)tile_index.getY());
        }
    }

    public void render(GraphicsContext ctx){

        RoomTile tile = parent.getRoom().getTile((int)tile_index.x, (int)tile_index.y);
        if(tile != null){
            if(tile.isTransitable()) {
                ctx.save();
                ctx.translate(tile_index.x * Room.TILE_SIZE, tile_index.y * Room.TILE_SIZE);
                ctx.setFill(Color.BLUE);
                ctx.fillRect(2, 2, Room.TILE_SIZE - 4, Room.TILE_SIZE - 4);
                ctx.restore();
            }
        }

    }

}
