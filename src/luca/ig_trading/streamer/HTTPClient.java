package luca.ig_trading.streamer;

import com.google.gson.Gson;
import luca.ig_trading.streamer.data.LoginDetails;
import luca.ig_trading.streamer.data.LoginResponse;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.pmw.tinylog.Logger;

import java.io.IOException;

public class HTTPClient {

    // one instance, reuse
    private final OkHttpClient httpClient = new OkHttpClient();


    public LoginResponse login(LoginDetails loginDetails) throws Exception {

        // form parameters
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("identifier", loginDetails.getUsername());
        jsonBody.put("password", loginDetails.getPassword());
        // TODO: 2020-02-16 password should be encrypted


        RequestBody requestBody = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("https://api.ig.com/gateway/deal/session")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("Accept:", "application/json; charset=UTF-8")
                .addHeader("X-IG-API-KEY", loginDetails.getApiKey())
                .addHeader("Version", "2")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                Logger.error("login error, code: " + response);
                throw new IOException("Unexpected code " + response);
            }

            // Get response body
            String responseBodyString = response.body().string();
            Logger.info("\n" + responseBodyString);

            Gson gson = new Gson();
            LoginResponse loginResponse = gson.fromJson(responseBodyString, LoginResponse.class);

            loginResponse.setHeaders(response.headers());

            Logger.info(" - endpoint: " + loginResponse.getLightstreamerEndpoint());

            Logger.info(" - headers: ->\n" + loginResponse.getHeaders().toString());

            return loginResponse;

        }

    }


}


