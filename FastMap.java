package extensions.fastmap;

import extensions.fastmap.FurniData.FurniData;
import extensions.fastmap.FurniData.FurniDataManager;
import extensions.fastmap.Map.AStar;
import extensions.fastmap.Map.AStarNode;
import extensions.fastmap.Map.Room;
import extensions.fastmap.MiniGames.HandItemGame;
import extensions.fastmap.MiniGames.MiniGameController;
import extensions.fastmap.MiniGames.ScapeBeastGame;
import extensions.fastmap.MiniGames.TypeMiniGame;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionInfo;

import gearth.extensions.extra.harble.HashSupport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javax.swing.Timer;
import javax.vecmath.Vector2d;
import java.net.URISyntaxException;

import static javax.swing.JOptionPane.showMessageDialog;

@ExtensionInfo(
        Title = "FastMap",
        Description = "FastMap for Habbo",
        Version = "1.0",
        Author = "lukasharing"
)

public class FastMap extends ExtensionForm {

    public Button sillas_locas;
    public Button huye_enemigo;
    public Button lista_compra;
    public HBox buttons_minigame;

    private HashSupport hash_manager;

    public HashSupport getHash(){ return hash_manager; };

    // Mini Games
    private HandItemGame hand_item_game;
    private ScapeBeastGame scape_beast_game;
    private FurniDataManager furnis_data;

    public FurniData getFurniData(int id_furni){ return furnis_data.get(id_furni); };

    private boolean play = false;

    private CanvasRoomManager canvas_manager;

    @FXML
    private Canvas canvas;
    public Canvas getCanvas(){ return canvas; };

    private GraphicsContext ctx;

    private Scene scene;

    private Room current_room;
    private AStar search_algorithm;
    public AStarNode AStar(Vector2d init, Vector2d goal){ return search_algorithm.algorithm(init, goal); };

    public Room getRoom(){ return this.current_room; };

    private MiniGameController current_game = null;
    public MiniGameController getCurrentGame(){ return current_game; };

    public static void main(String[] args) {
        runExtensionForm(args, FastMap.class);
    }

    public static String getFolderPath() {
        File Dir = null;
        try {
            Dir = new File(FastMap.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            if (Dir.getName().equals("Extensions")) {
                Dir = Dir.getParentFile();
            }
        } catch (URISyntaxException e) {}

        return Dir + File.separator + "FastMoving";
    }

    public  void reset_room_data(HMessage message){
        HPacket packet = message.getPacket();

        int room_id = packet.readInteger();
        int a = packet.readInteger();
        int b = packet.readInteger();

        if(a == 1 && b == 0) return;

        current_room = new Room(this, room_id);
    };

    public void game_option(javafx.event.ActionEvent actionEvent) {
        final javafx.scene.Node source = (javafx.scene.Node) actionEvent.getSource();

        frames_to_play = 0;

        for(Node children : buttons_minigame.getChildren()){
            if(children.getTypeSelector().equals("Button")){
                children.getStyleClass().remove("selected");
            }
        }

        if(current_game != null) {
            current_game = null;
        }else{
            source.getStyleClass().add("selected");
            if(source == sillas_locas) {
                current_game = scape_beast_game;
            }else if(source == huye_enemigo) {
                current_game = scape_beast_game;
            }else if(source == lista_compra) {
                current_game = hand_item_game;
            }
            current_game.init();
        }
    }

    int frames_to_play = 0;
    public void tick(){
        this.render();
        if(current_game != null) {
            this.update();
        }
    }

    public void render(){

    }

    public void update(){
            if(frames_to_play == 0){
                current_game.update();
            }
            frames_to_play = (frames_to_play + 1) % 5;

    }

    @Override
    protected void initExtension() {
        // Init Elements
        ctx = canvas.getGraphicsContext2D();
        hash_manager = new HashSupport(this);

        // Canvas Manager
        canvas_manager = new CanvasRoomManager(this);

        // Furni Data
        furnis_data = new FurniDataManager(this);

        // Mini Games
        hand_item_game = new HandItemGame(this);
        scape_beast_game = new ScapeBeastGame(this);

        // A Star
        search_algorithm = new AStar(this);

        hash_manager.intercept(HMessage.Direction.TOSERVER, "RequestRoomData", this::reset_room_data);


        new Timer(1000/30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        }).start();
    }

    public void sendToServer(String hashOrName, Object... objects){ hash_manager.sendToServer(hashOrName, objects); };


    public boolean isCurrentGame(TypeMiniGame current_type_game){
        switch(current_type_game){
            case LISTA_COMPRA: return current_game == hand_item_game;
            case HUYE_ENEMIGO: return current_game == scape_beast_game;
        }
        return false;
    }

    @Override
    public ExtensionForm launchForm(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(FastMap.class.getResource("fastmap.fxml"));
        Parent root = loader.load();

        stage.setTitle("Habbo Fast Moving");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);

        scene.getStylesheets().add(FastMap.class.getResource("styles.css").toExternalForm());

        return loader.getController();
    }

    public void canvas_clicked(MouseEvent mouseEvent) { canvas_manager.canvas_clicked(mouseEvent); }
    public void canvas_mousemove(MouseEvent mouseEvent) { canvas_manager.canvas_mousemove(mouseEvent); }
}
