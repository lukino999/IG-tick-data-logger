package luca.ig_trading;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import luca.ig_trading.Logger.MyLogger;
import luca.ig_trading.streamer.Streamer;
import luca.ig_trading.streamer.data.LoginDetails;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;


public class Controller {

    @FXML
    private TextArea consoleTextArea;
    @FXML
    private MenuItem connect;
    private MyLogger logFileWriter;
    private LoginDetails loginDetails;
    private Streamer streamer = new Streamer();

    public void setBaseFileName(String baseFileName) {
        logFileWriter = new MyLogger(baseFileName);
        Configurator.currentConfig()
                .formatPattern("{date:yyyy-MM-dd HH:mm:ss.SSS}: {message}")
                .activate();
        Configurator.currentConfig()
                .writer(logFileWriter)
                .activate();
        logFileWriter.addMessageListener(renderedLogEntry -> {
            Platform.runLater(() -> consoleTextArea.appendText(renderedLogEntry));
        });
        streamer.setBaseFileName(baseFileName);
    }


    public void setLoginDetails(LoginDetails loginDetails) {
        this.loginDetails = loginDetails;
    }


    public void onMenuConnectConnect(ActionEvent actionEvent) {
        Logger.info("connect");
        startStreaming();
        connect.setDisable(true);
    }

    private void startStreaming() {
        Runnable stream = new Runnable() {
            @Override
            public void run() {
                streamer.setLoginDetails(loginDetails);
                streamer.startLiveStream();
            }
        };

        stream.run();
    }


    public void close() {
        try {
            if(streamer != null) {
                streamer.stopLiveStream();
            }
            Thread.sleep(500);
            logFileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
