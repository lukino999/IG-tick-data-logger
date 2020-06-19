package luca.ig_trading.streamer.data;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountInfo {

    @SerializedName("balance")
    @Expose
    private Double balance;
    @SerializedName("deposit")
    @Expose
    private Double deposit;
    @SerializedName("profitLoss")
    @Expose
    private Double profitLoss;
    @SerializedName("available")
    @Expose
    private Double available;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getDeposit() {
        return deposit;
    }

    public Double getProfitLoss() {
        return profitLoss;
    }

    public Double getAvailable() {
        return available;
    }

}