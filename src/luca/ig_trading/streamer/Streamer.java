package luca.ig_trading.streamer;

import com.lightstreamer.client.LightstreamerClient;
import com.lightstreamer.client.Subscription;
import luca.ig_trading.Logger.Delay;
import luca.ig_trading.Logger.ClientErrorListener;
import luca.ig_trading.streamer.data.LoginDetails;
import luca.ig_trading.streamer.data.LoginResponse;
import org.pmw.tinylog.EnvironmentHelper;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Streamer {

    private static final String[] fields = {"BID", "OFR", "UTM"};
//    private static final ArrayList<String> items = new ArrayList<>();
    private static String[] epics = {
            "CS.D.GBPUSD.TODAY.IP",
            "CS.D.EURGBP.TODAY.IP",
            "CS.D.EURUSD.TODAY.IP",
            "CS.D.USDJPY.TODAY.IP",
            "IX.D.FTSE.DAILY.IP"
//            "CS.D.BITCOIN.TODAY.IP",
//            "CS.D.ETHUSD.TODAY.IP",
//            "CS.D.CRYPTOB10.TODAY.IP",
//            "CS.D.EOSUSD.TODAY.IP",
//            "CS.D.NEOUSD.TODAY.IP",
//            "CS.D.BCHXBT.TODAY.IP",
//            "CS.D.ETHXBT.TODAY.IP",
//            "CS.D.BCHUSD.TODAY.IP",
//            "CS.D.XRPUSD.TODAY.IP",
//            "CS.D.LTCUSD.TODAY.IP"
    };
    private static LoginDetails loginDetails;
    private LightstreamerClient lsClient;
    private Subscription subscription;
    private HTTPClient httpClient;
    private String baseFileName;
    private Timer timer;
    private BufferedOutputStream stream;
    private final int BUFFER_SIZE = 1024;
    private TickerUpdateListener tickerUpdateListener;
    private ClientErrorListener clientErrorListener;

    public Streamer() {
        setupErrorListener();
    }

    private void setupErrorListener() {

        this.clientErrorListener = code -> {
            if (code == 1) {
                // User/password check failed
                startLiveStream();
            }
        };

    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("args required: username password apiKey");
            return;
        }

        loginDetails.setUsername(args[0]);
        loginDetails.setPassword(args[1]);
        loginDetails.setApiKey(args[2]);

        Streamer streamer = new Streamer();
        streamer.startLiveStream();
    }


    public static void setLoginDetails(LoginDetails loginDetails) {
        Streamer.loginDetails = loginDetails;
    }


    public void setBaseFileName(String baseFileName) {
        this.baseFileName = baseFileName;
    }


    public void setTickerUpdateListener(TickerUpdateListener tickerUpdateListener) {
        this.tickerUpdateListener = tickerUpdateListener;
    }


    public void startLiveStream() {

        Logger.info(" - Logging in");

        if(httpClient == null) {
            httpClient = new HTTPClient();
        }

        LoginResponse loginResponse = login();

        if (loginResponse == null) {
            return;
        } else {

            // open file and schedule flush in between every second
            openFileStream();

            //
            String serverAddress = loginResponse.getLightstreamerEndpoint();
            lsClient = new LightstreamerClient(serverAddress, null);

            lsClient.connectionDetails.setUser(loginResponse.getCurrentAccountId());

            String password = "";
            String client_token = loginResponse.getHeaders().get("CST");
            String account_token = loginResponse.getHeaders().get("X-SECURITY-TOKEN");
            if (client_token != null) {
                password = "CST-" + client_token;
            }
            if ((client_token != null) && (account_token != null)) {
                password = password + "|";
            }
            if (account_token != null) {
                password = password + "XST-" + account_token;
            }
            lsClient.connectionDetails.setPassword(password);

            lsClient.addListener(new LogClientListener(clientErrorListener));

            ArrayList<String> items = new ArrayList<>();
            for (String epic : epics) {
                items.add("CHART:" + epic + ":TICK");
            }
            String[] itemsArray = new String[items.size()];
            itemsArray = items.toArray(itemsArray);

            subscription = new Subscription("DISTINCT", itemsArray, fields);
//            subscription.setDataAdapter();//?
            subscription.addListener(new LogSubscriptionListener(stream, epics, tickerUpdateListener));

            lsClient.subscribe(subscription);
            lsClient.connect();
        }
    }


    private LoginResponse login() {
        try {
            return httpClient.login(
                    loginDetails.getUsername(),
                    loginDetails.getPassword(),
                    loginDetails.getApiKey()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void openFileStream() {

        if(stream != null) {
            return;
        }

        File file = new File(baseFileName + ".csv");
        EnvironmentHelper.makeDirectories(file);

        try {
            stream = new BufferedOutputStream(new FileOutputStream(file, true), BUFFER_SIZE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        scheduleFileFlush();
    }


    private void scheduleFileFlush() {
        TimerTask flushFile = new TimerTask() {
            @Override
            public void run() {
                // System.out.println("csv flush: " + LocalDateTime.now());
                try {
                    synchronized (stream) {
                        stream.flush();
                    }
                } catch (Exception e) {
                    System.out.println("Error with scheduled file flush");
                    e.printStackTrace();
                }
            }
        };
        timer = new Timer();
        LocalDateTime now = LocalDateTime.now();

        //
        timer.scheduleAtFixedRate(flushFile, Delay.getDelayToNextSecond() + 500, 1000);
    }


    public void stopLiveStream() {
        Logger.info(" stopLiveStream");
        if (lsClient != null) {
            lsClient.unsubscribe(subscription);
            lsClient.disconnect();
        }

        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(timer != null) {
            timer.cancel();
        }
    }

}
