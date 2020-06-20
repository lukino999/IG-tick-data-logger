package luca.ig_trading.Logger;


public class Delay {
    public static long getDelayToNextSecond() {
        return 1000 - (System.currentTimeMillis() % 1000);
    }
}
