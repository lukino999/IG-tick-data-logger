package luca.ig_trading;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import luca.ig_trading.streamer.data.LoginDetails;
import org.pmw.tinylog.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Application {

    private static String baseFileName;
    private static LoginDetails loginDetails = new LoginDetails();


    public static void main(String[] args) {
        createFileName();
        setLoginDetails(args);
        launch(args);
    }


    private static void createFileName() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        baseFileName = "./data/" + dtf.format(now);
    }


    private static void setLoginDetails(String[] args) {
        loginDetails.setUsername(args[0]);
        loginDetails.setPassword(args[1]);
        loginDetails.setApiKey(args[2]);
    }



    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("IG Data Logger");
        primaryStage.setScene(new Scene(root, 900, 500));

        Controller controller = loader.getController();
        controller.setBaseFileName(baseFileName);
        controller.setLoginDetails(loginDetails);

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
