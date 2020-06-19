package luca.ig_trading.streamer.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Account {

    @SerializedName("accountId")
    @Expose
    private String accountId;
    @SerializedName("accountName")
    @Expose
    private String accountName;
    @SerializedName("preferred")
    @Expose
    private Boolean preferred;
    @SerializedName("accountType")
    @Expose
    private String accountType;

    public String getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public Boolean getPreferred() {
        return preferred;
    }

    public String getAccountType() {
        return accountType;
    }

}