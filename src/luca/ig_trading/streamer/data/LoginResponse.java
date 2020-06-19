package luca.ig_trading.streamer.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import okhttp3.Headers;


import java.util.List;

public class LoginResponse {


    private Headers headers;

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }



    @SerializedName("accountType")
    @Expose
    private String accountType;
    @SerializedName("accountInfo")
    @Expose
    private AccountInfo accountInfo;
    @SerializedName("currencyIsoCode")
    @Expose
    private String currencyIsoCode;
    @SerializedName("currencySymbol")
    @Expose
    private String currencySymbol;
    @SerializedName("currentAccountId")
    @Expose
    private String currentAccountId;
    @SerializedName("lightstreamerEndpoint")
    @Expose
    private String lightstreamerEndpoint;
    @SerializedName("accounts")
    @Expose
    private List<Account> accounts = null;
    @SerializedName("clientId")
    @Expose
    private String clientId;
    @SerializedName("timezoneOffset")
    @Expose
    private Integer timezoneOffset;
    @SerializedName("hasActiveDemoAccounts")
    @Expose
    private Boolean hasActiveDemoAccounts;
    @SerializedName("hasActiveLiveAccounts")
    @Expose
    private Boolean hasActiveLiveAccounts;
    @SerializedName("trailingStopsEnabled")
    @Expose
    private Boolean trailingStopsEnabled;
    @SerializedName("reroutingEnvironment")
    @Expose
    private Object reroutingEnvironment;
    @SerializedName("dealingEnabled")
    @Expose
    private Boolean dealingEnabled;

    public String getAccountType() {
        return accountType;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public String getCurrencyIsoCode() {
        return currencyIsoCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getCurrentAccountId() {
        return currentAccountId;
    }

    public String getLightstreamerEndpoint() {
        return lightstreamerEndpoint;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public String getClientId() {
        return clientId;
    }

    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    public Boolean getHasActiveDemoAccounts() {
        return hasActiveDemoAccounts;
    }

    public Boolean getHasActiveLiveAccounts() {
        return hasActiveLiveAccounts;
    }

    public Boolean getTrailingStopsEnabled() {
        return trailingStopsEnabled;
    }

    public Object getReroutingEnvironment() {
        return reroutingEnvironment;
    }

    public Boolean getDealingEnabled() {
        return dealingEnabled;
    }


}