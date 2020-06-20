package luca.ig_trading.streamer;

import com.lightstreamer.client.LightstreamerClient;
import com.lightstreamer.client.Subscription;
import luca.ig_trading.streamer.data.LoginDetails;
import luca.ig_trading.streamer.data.LoginResponse;
import org.pmw.tinylog.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Streamer {

    private static final String[] fields = {"BID", "OFR", "UTM"};

    private static String[] epics = {
            "CS.D.GBPUSD.TODAY.IP",
            "CS.D.EURGBP.TODAY.IP",
            "CS.D.EURUSD.TODAY.IP",
            "CS.D.USDJPY.TODAY.IP",
            "IX.D.FTSE.DAILY.IP",
            "CS.D.BITCOIN.TODAY.IP",
            "CS.D.ETHUSD.TODAY.IP",
            "CS.D.CRYPTOB10.TODAY.IP",
            "CS.D.EOSUSD.TODAY.IP",
            "CS.D.NEOUSD.TODAY.IP",
            "CS.D.BCHXBT.TODAY.IP",
            "CS.D.ETHXBT.TODAY.IP",
            "CS.D.BCHUSD.TODAY.IP",
            "CS.D.XRPUSD.TODAY.IP",
            "CS.D.LTCUSD.TODAY.IP"
    };
    private static final ArrayList<String> items = new ArrayList<>();

    private LightstreamerClient lsClient;
    private Subscription subscription;
    private HTTPClient httpClient;
    private FileWriter fileWriter;
    private String fileName;
    private static LoginDetails loginDetails;

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

    public void startLiveStream() {

        Logger.info(" - Logging in");

        httpClient = new HTTPClient();

        LoginResponse loginResponse = login();

        if (loginResponse == null) {
            return;
        } else {

            // open file and schedule flush in between every second

            //
            String serverAddress = loginResponse.getLightstreamerEndpoint();

            lsClient = new LightstreamerClient(serverAddress, null);
            Logger.info(" - current account id: " + loginResponse.getCurrentAccountId());
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

            com.lightstreamer.client.ClientListener clientListener = new LogClientListener();
            lsClient.addListener(clientListener);

            for (String epic : epics) {
                items.add("CHART:" + epic + ":TICK");
            }

            String[] itemsArray = new String[items.size()];
            itemsArray = items.toArray(itemsArray);

            subscription = new Subscription("DISTINCT", itemsArray, fields);
            subscription.setRequestedSnapshot("yes");

            com.lightstreamer.client.SubscriptionListener subListener = new LogSubscriptionListener();
            subscription.addListener(subListener);

            lsClient.subscribe(subscription);
            lsClient.connect();
        }
    }

    public void stopLiveStream() {
        Logger.info(" stopLiveStream");
        if (lsClient != null) {
            lsClient.unsubscribe(subscription);
            lsClient.disconnect();
        }

        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //
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

    public void setBaseFileName(String baseFileName) {
        this.fileName = baseFileName + ".csv";
    }
}
