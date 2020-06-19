package luca.ig_trading.streamer.data;

import com.lightstreamer.client.ItemUpdate;

public class Ticker {

    public String name;
    public double bid;
    public double ofr;
    public long utm;



    // constructor
    public Ticker(String name) {
        this.name = name;
        bid = 0.0;
        ofr = 0.0;
        utm = 0;
    }

    public void setValues(ItemUpdate update) {
        bid = Double.parseDouble(update.getValue("BID"));
        ofr = Double.parseDouble(update.getValue("OFR"));
        utm = Long.parseLong(update.getValue("UTM"));
    }
}
