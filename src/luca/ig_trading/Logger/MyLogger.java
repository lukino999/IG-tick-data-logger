package luca.ig_trading.Logger;

import org.pmw.tinylog.Configuration;
import org.pmw.tinylog.EnvironmentHelper;
import org.pmw.tinylog.LogEntry;
import org.pmw.tinylog.writers.LogEntryValue;
import org.pmw.tinylog.writers.VMShutdownHook;
import org.pmw.tinylog.writers.Writer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.*;

public class MyLogger implements Writer {

    private String baseFileName;
    private BufferedOutputStream stream;
    private final List<MessageListener> messageListeners = new ArrayList<>();

    private static final int BUFFER_SIZE = 1024;
    private Timer timer;

    public MyLogger(String baseFileName) {
        this.baseFileName = baseFileName;
    }

    @Override
    public Set<LogEntryValue> getRequiredLogEntryValues() {
        return EnumSet.of(LogEntryValue.RENDERED_LOG_ENTRY);
    }

    @Override
    public void init(Configuration configuration) throws Exception {
        File file = new File(baseFileName + ".log");
        EnvironmentHelper.makeDirectories(file);

        stream = new BufferedOutputStream(new FileOutputStream(file, false), BUFFER_SIZE);

        VMShutdownHook.register(this);

        scheduleFileFlush();
    }

    private void scheduleFileFlush() {
        TimerTask flushFile = new TimerTask() {
            @Override
            public void run() {
                System.out.println("log flush: " + LocalDateTime.now());
                try {
                    flush();
                } catch (Exception e) {
                    System.out.println("Error with scheduled file flush");
                    e.printStackTrace();
                }
            }
        };
        timer = new Timer();
        LocalDateTime now = LocalDateTime.now();

        //
        timer.scheduleAtFixedRate(flushFile, Delay.getDelayToNextSecond(), 1000);
    }

    public void addMessageListener(MessageListener messageListener) {
        messageListeners.add(messageListener);
    }

    @Override
    public void write(LogEntry logEntry) throws Exception {
        String renderedLogEntry = logEntry.getRenderedLogEntry();

        fireOnMessage(renderedLogEntry);

        byte[] data = renderedLogEntry.getBytes();
        synchronized (stream) {
            stream.write(data);
        }
    }

    private void fireOnMessage(String renderedLogEntry) {
        for (MessageListener l : messageListeners) {
            l.onMessage(renderedLogEntry);
        }
    }

    @Override
    public void flush() throws Exception {
        synchronized (stream) {
            stream.flush();
        }
    }

    @Override
    public void close() throws Exception {
        timer.cancel();
        synchronized (stream) {
            VMShutdownHook.unregister(this);
            stream.close();
        }
    }
}
