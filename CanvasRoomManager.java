package extensions.fastmap;

import extensions.fastmap.Map.Room;
import extensions.fastmap.MiniGames.ScapeBeastGame;
import extensions.fastmap.MiniGames.TypeMiniGame;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

import javax.vecmath.Vector2d;

public class CanvasRoomManager {

    public Canvas canvas;
    private Vector2d mouse_position;

    private final FastMap parent;
    CanvasRoomManager(FastMap parent){
        this.parent = parent;
        this.mouse_position = new Vector2d(-1.0, -1.0);
    }

    public void canvas_mousemove(MouseEvent mouseEvent) {
        if(parent.getRoom().isEmpty()) return; // Wait until Map is fetched

        int padding_x = (int)(canvas.getWidth() - parent.getRoom().width() * Room.TILE_SIZE) / 2;
        int padding_y = (int)(canvas.getHeight() - parent.getRoom().height() * Room.TILE_SIZE) / 2;

        mouse_position = new Vector2d(
                (int)(mouseEvent.getX() - padding_x) / Room.TILE_SIZE,
                (int)(mouseEvent.getY() - padding_y) / Room.TILE_SIZE
        );
    }

    public void canvas_clicked(MouseEvent mouseEvent) {
        if(parent.getRoom().isEmpty()) return;

        if (parent.AStar(parent.getRoom().getHeroPosition(), mouse_position) != null) {
            if (parent.isCurrentGame(TypeMiniGame.HUYE_ENEMIGO)) {
                ((ScapeBeastGame) parent.getCurrentGame()).setDestiny(mouse_position);
            } else {
                parent.sendToServer("RoomUserWalk", (int)mouse_position.getX(), (int)mouse_position.getY());
            }
        }
    }

}
