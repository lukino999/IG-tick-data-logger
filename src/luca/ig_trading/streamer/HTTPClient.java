package luca.ig_trading.streamer;

import com.google.gson.Gson;
import okhttp3.*;
import org.json.simple.JSONObject;
import luca.ig_trading.streamer.data.LoginResponse;

import java.io.IOException;

public class HTTPClient {

    public HTTPClient() {
    }

    // one instance, reuse
    private final OkHttpClient httpClient = new OkHttpClient();


    public LoginResponse login(String username, String password, String APIKey) throws Exception {

        // form parameters

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("identifier", username);
        jsonBody.put("password", password);
        // TODO: 2020-02-16 password should be encrypted


        RequestBody requestBody = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("https://api.ig.com/gateway/deal/session")
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("Accept:", "application/json; charset=UTF-8")
                .addHeader("X-IG-API-KEY", APIKey)
                .addHeader("Version", "2")
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            // Get response body
            String responseBodyString = response.body().string();
            System.out.println(responseBodyString);

            Gson gson = new Gson();
            LoginResponse loginResponse = gson.fromJson(responseBodyString, LoginResponse.class);

            loginResponse.setHeaders(response.headers());

            System.out.println(" - endpoint: " + loginResponse.getLightstreamerEndpoint());

            System.out.println(" - headers: ->\n" + loginResponse.getHeaders().toString());

            return loginResponse;

        }

    }


}

