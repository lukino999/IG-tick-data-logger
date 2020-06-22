package luca.ig_trading;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import luca.ig_trading.Logger.MyLogger;
import luca.ig_trading.streamer.Streamer;
import luca.ig_trading.streamer.TickerUpdateListener;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;

import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    private TextArea consoleTextArea;
    @FXML
    private MenuItem connect;
    @FXML
    private TextArea tickerListTextArea;
    private MyLogger logFileWriter;
    private Streamer streamer = new Streamer();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setBaseFileName(Main.getBaseFileName());
    }

    private void setBaseFileName(String baseFileName) {
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


    public void onMenuConnectConnect() {
        Logger.info("connect");
        startStreaming();
        connect.setDisable(true);
    }

    private void startStreaming() {
        Runnable stream = () -> {
            streamer.setLoginDetails(Main.getLoginDetails());
            TickerUpdateListener tickerUpdateListener = display -> {
                Platform.runLater(() -> tickerListTextArea.setText(display));
            };
            streamer.setTickerUpdateListener(tickerUpdateListener);
            streamer.startLiveStream();
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
