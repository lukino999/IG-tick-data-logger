package luca.ig_trading;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.pmw.tinylog.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Application {


    private static String baseFileName;

    public static void main(String[] args) {
        createFileName();
        launch(args);
    }


    private static void createFileName() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        baseFileName = "./data/" + dtf.format(now);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("IG Data Logger");
        primaryStage.setScene(new Scene(root, 900, 500));

        Controller controller = loader.getController();
        controller.setBaseFileName(baseFileName);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                controller.close();
                primaryStage.close();
            }
        });

        primaryStage.show();
        Logger.info("launch");
    }

}
