package luca.ig_trading.streamer;


import com.lightstreamer.client.ItemUpdate;
import com.lightstreamer.client.Subscription;
import com.lightstreamer.client.SubscriptionListener;

import java.io.FileWriter;
import java.io.IOException;

class LogSubscriptionListener implements SubscriptionListener {

    FileWriter fileWriter;


    public LogSubscriptionListener() {
    }

    @Override
    public void onClearSnapshot(String itemName, int itemPos) {
        String msg = "onClearSnapshot: ";
        msg += "itemName: " + itemName + " - itemPos: " + String.valueOf(itemPos);
        System.out.println(msg);
    }

    @Override
    public void onCommandSecondLevelItemLostUpdates(int lostUpdates, String key) {
        String msg = "onCommandSecondLevelItemLostUpdates: ";
        msg += "lostUpdates: " + String.valueOf(lostUpdates) + " - key: " + key;
        System.out.println(msg);
    }

    @Override
    public void onCommandSecondLevelSubscriptionError(int code, String message, String key) {
        String msg = "onCommandSecondLevelSubscriptionError: ";
        msg += "code: " + String.valueOf(code) + " - message: " + message + " - key: " + key;
        System.out.println(msg);
    }

    @Override
    public void onEndOfSnapshot(String arg0, int arg1) {
        System.out.println("Snapshot is now fully received, from now on only real-time messages will be received");
    }

    @Override
    public void onItemLostUpdates(String itemName, int itemPos, int lostUpdates) {
        System.out.println(lostUpdates + " messages were lost");
    }

    @Override
    public void onItemUpdate(ItemUpdate update) {
        String itemName = update.getItemName();

        System.out.println("onItemUpdate: " + itemName);
        /*
        String bidString = update.getValue("BID");
        String ofrString = update.getValue("OFR");
        String utmString = update.getValue("UTM");

        String record = itemName + "," + bidString + "," + ofrString + "," + utmString + "\n";
        try {
            System.out.println("onItemUpdate: adding record to file");
            fileWriter.write(record);
            fileWriter.flush();
        } catch (IOException e) {
            System.out.println("Error adding record to file");
            e.printStackTrace();
        }

         */
    }

    @Override
    public void onListenEnd(Subscription subscription) {
        System.out.println("Stop listening to subscription: " + subscription.toString());
    }

    @Override
    public void onListenStart(Subscription subscription) {
        System.out.println("Start listening to subscription: " + subscription.toString());

    }

    @Override
    public void onSubscription() {
        System.out.println("Now subscribed to the chat item, messages will now start coming in");
    }

    @Override
    public void onSubscriptionError(int code, String message) {
        System.out.println("Cannot subscribe because of error " + code + ": " + message);
    }

    @Override
    public void onUnsubscription() {
        System.out.println("Now unsubscribed from chat item, no more messages will be received");
    }

}