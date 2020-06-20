package luca.ig_trading;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import luca.ig_trading.Logger.MyLogger;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;


public class Controller {

    private MyLogger logFileWriter;
    public TextArea consoleTextArea;


    public void onMenuConnectConnect(ActionEvent actionEvent) {
        Logger.info("onMenuConnectConnect");
    }

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
    }

    public void close() {
        try {
            logFileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
