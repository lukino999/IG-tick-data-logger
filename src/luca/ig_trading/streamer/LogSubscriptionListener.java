package luca.ig_trading.streamer;


import com.lightstreamer.client.ItemUpdate;
import com.lightstreamer.client.Subscription;
import com.lightstreamer.client.SubscriptionListener;
import luca.ig_trading.streamer.data.Ticker;
import org.pmw.tinylog.Logger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

class LogSubscriptionListener implements SubscriptionListener {

    private BufferedOutputStream stream;
    private ArrayList<Ticker> tickers = new ArrayList<>();
    private TickerUpdateListener tickerUpdateListener;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

    public LogSubscriptionListener(BufferedOutputStream stream, String[] epics, TickerUpdateListener tickerUpdateListener) {
        this.stream = stream;

        for (String epic : epics) {
            tickers.add(new Ticker(epic));
        }

        this.tickerUpdateListener = tickerUpdateListener;
    }

    @Override
    public void onClearSnapshot(String itemName, int itemPos) {
        String msg = "onClearSnapshot: ";
        msg += "itemName: " + itemName + " - itemPos: " + String.valueOf(itemPos);
        Logger.info(msg);
    }

    @Override
    public void onCommandSecondLevelItemLostUpdates(int lostUpdates, String key) {
        String msg = "onCommandSecondLevelItemLostUpdates: ";
        msg += "lostUpdates: " + String.valueOf(lostUpdates) + " - key: " + key;
        Logger.info(msg);
    }

    @Override
    public void onCommandSecondLevelSubscriptionError(int code, String message, String key) {
        String msg = "onCommandSecondLevelSubscriptionError: ";
        msg += "code: " + String.valueOf(code) + " - message: " + message + " - key: " + key;
        Logger.info(msg);
    }

    @Override
    public void onEndOfSnapshot(String arg0, int arg1) {
        Logger.info("Snapshot is now fully received, from now on only real-time messages will be received");
    }

    @Override
    public void onItemLostUpdates(String itemName, int itemPos, int lostUpdates) {
        Logger.info(lostUpdates + " messages were lost");
    }

    @Override
    public void onItemUpdate(ItemUpdate update) {
        writeToFile(update);
        updateDisplay(update);
    }

    private void throwError() {
        System.out.println("before error");
        int[] nums ={0};
        int i = nums[nums.length];
        System.out.println("after error");
    }

    private void writeToFile(ItemUpdate update) {
        String bidString = update.getValue("BID");
        String ofrString = update.getValue("OFR");
        String utmString = update.getValue("UTM");

        String record = update.getItemName() + ","
                + bidString + ","
                + ofrString + ","
                + utmString + "\n";
        byte[] data = record.getBytes();
        synchronized (stream) {
            try {
                stream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM-dd HH:mm:ss.SSS");

    private void updateDisplay(ItemUpdate update) {
        StringBuilder display = new StringBuilder();
        for (Ticker ticker : tickers) {
            if (ticker.name.equals(update.getItemName().split(":")[1])) {
                ticker.bid = Double.parseDouble(update.getValue("BID"));
                ticker.ofr = Double.parseDouble(update.getValue("OFR"));
                ticker.utm = Long.parseLong(update.getValue("UTM"));
            }
            display.append(ticker.name);
            display.append("\t");

            Date date = new java.util.Date(ticker.utm);
            sdf.setTimeZone(java.util.TimeZone.getDefault());
            String formattedDate = sdf.format(date);
            display.append(formattedDate);

            display.append("\n");
            display.append(ticker.bid);
            display.append("\t\t");
            display.append(ticker.ofr);
            display.append("\t\t");
            double spread = Math.round((ticker.ofr - ticker.bid) * 10) * 0.1;
            display.append(String.format("%.1f",spread));
            display.append("\n\n");
        }

        tickerUpdateListener.onTickerUpdate(display.toString());
    }


    @Override
    public void onListenEnd(Subscription subscription) {
        Logger.info("Stop listening to subscription: " + subscription.toString());
    }

    @Override
    public void onListenStart(Subscription subscription) {
        Logger.info("Start listening to subscription: " + subscription.toString());

    }

    @Override
    public void onSubscription() {
        Logger.info("Now subscribed to the chat item, messages will now start coming in");
    }

    @Override
    public void onSubscriptionError(int code, String message) {
        Logger.info("Cannot subscribe because of error " + code + ": " + message);
    }

    @Override
    public void onUnsubscription() {
        Logger.info("Now unsubscribed from chat item, no more messages will be received");
    }

}