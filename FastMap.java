package extensions.fastmap;

import extensions.fastmap.FurniData.FurniData;
import extensions.fastmap.FurniData.FurniDataManager;
import extensions.fastmap.Map.Room;
import extensions.fastmap.MiniGames.*;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionInfo;

import gearth.extensions.extra.harble.HashSupport;

import java.io.File;
import java.net.URISyntaxException;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;


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


    public CheckBox queso;
    public CheckBox palo;
    public CheckBox serpiente;
    public CheckBox patito;
    public CheckBox alienigena;
    public CheckBox ovni;
    public CheckBox comandante;
    public CheckBox llave;
    public CheckBox te;
    public CheckBox tostada;
    public CheckBox copa_sangre;
    public CheckBox antorcha;
    public CheckBox naranja;
    public CheckBox pera;
    public CheckBox sake;
    public CheckBox zanahoria;
    public ImageView zanahoria_img;
    public CheckBox zumo_tomate;
    public CheckBox melocoton;
    public CheckBox globo;
    public CheckBox jeringuilla;
    public CheckBox pildoras;
    public CheckBox flor;
    public CheckBox pez;
    public CheckBox muslo;
    public CheckBox bolsa;
    public CheckBox pincel;

    private HashSupport hash_manager;

    // Math
    public static double fract(double a) { return a - Math.floor(a); };

    public HashSupport getHash(){ return hash_manager; };

    // Mini Games
    private HandItemGame hand_item_game;
    private ScapeBeastGame scape_beast_game;
    private CrazyChairs crazy_chairs;
    private FurniDataManager furnis_data;

    public FurniData getFurniData(int id_furni){ return furnis_data.get(id_furni); };

    private boolean play = false;

    private EventController events;
    public EventController getEvents(){ return events; };

    @FXML
    private Canvas canvas;
    public Canvas getCanvas(){ return canvas; };

    private GraphicsContext ctx;

    private Room current_room;

    public Room getRoom(){ return current_room.isCreated() ? this.current_room : null; };

    private MiniGameController current_game = null;
    public MiniGameController getCurrentGame(){ return current_game; };

    private Timeline timer_loop;

    public static void main(String[] args) {
        runExtensionForm(args, FastMap.class);
    }

    public String getFolderPath() {
        File Dir = null;
        try {
            Dir = new File(FastMap.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            writeToConsole(FastMap.class.getProtectionDomain().getCodeSource().getLocation().toURI().toASCIIString());
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

        if(a == 0 && b == 0){
            writeToConsole("Room Data Info: " + a + ", " + b);
            return;
        }if(a == 0 && b == 1){
            writeToConsole("Room Data Info: Clear");
            ctx.clearRect(0, 0, canvas.getWidth(), canvas.getWidth());
            current_room.clear();
            current_room.setRoomID(room_id);
            timer_loop.stop();
        }else if(a == 1 && b == 0){
            writeToConsole("Room Data Info: Ready");
            timer_loop.play();
        }else{
            writeToConsole("Room Data Info: " + a + ", " + b);
        }

    };

    @FXML
    public void game_option(javafx.event.ActionEvent actionEvent) {
        final Node source = (javafx.scene.Node) actionEvent.getSource();

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
                current_game = crazy_chairs;
            }else if(source == huye_enemigo) {
                current_game = scape_beast_game;
            }else if(source == lista_compra) {
                current_game = hand_item_game;
            }
            current_game.init();
        }
    }

    public void tick(double delta){
        current_room.update(delta);
        if(current_game != null) {
            current_game.update();
        }
        current_room.render(ctx);
    }

    @Override
    protected void initExtension() {
        // Init Elements
        ctx = canvas.getGraphicsContext2D();
        hash_manager = new HashSupport(this);

        // Canvas Manager
        events = new EventController(this);

        // Furni Data
        writeToConsole("LOAD FURNIDATA XML");
        furnis_data = new FurniDataManager(this);

        // Mini Games
        writeToConsole("INITIALIZE \"HAND ITEM\" GAME");
        hand_item_game = new HandItemGame(this);
        writeToConsole("INITIALIZE \"SCAPE FROM THE BEAST\" GAME");
        scape_beast_game = new ScapeBeastGame(this);
        writeToConsole("INITIALIZE \"SCAPE FROM THE BEAST\" GAME");
        crazy_chairs = new CrazyChairs(this);

        // A Star
        writeToConsole("INITIALIZE SEARCHER");

        hash_manager.intercept(HMessage.Direction.TOSERVER, "RequestRoomData", this::reset_room_data);

        // Create Empty Room
        current_room = new Room(this);

        writeToConsole("INITIALIZE \"TICK\" THREAD");

        float fps = 30;
        timer_loop = new Timeline(
                new KeyFrame(Duration.millis(1000 / fps),
                event -> {
                    double delta = fract(((Timeline)event.getSource()).getCurrentTime().toSeconds());
                    tick(delta);
                })
        );
        timer_loop.setCycleCount(Animation.INDEFINITE);
    }

    public void sendToServer(String hashOrName, Object... objects){ hash_manager.sendToServer(hashOrName, objects); };


    public boolean isCurrentGame(TypeMiniGame current_type_game){
        switch(current_type_game){
            case SILLAS_LOCAS: return current_game == crazy_chairs;
            case LISTA_COMPRA: return current_game == hand_item_game;
            case HUYE_ENEMIGO: return current_game == scape_beast_game;
        }
        return false;
    }

    @Override
    public ExtensionForm launchForm(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(FastMap.class.getResource("../../../resources/fastmap.fxml"));
        Parent root = loader.load();

        stage.setTitle("Habbo Fast Moving");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.getScene().getStylesheets().add(FastMap.class.getResource("styles.css").toExternalForm());

        return loader.getController();
    }

    public void canvas_clicked(MouseEvent mouseEvent) {
        events.canvas_clicked(mouseEvent);
    }

    public void canvas_mousemove(MouseEvent mouseEvent) {
        events.canvas_mousemove(mouseEvent);
    }

}
