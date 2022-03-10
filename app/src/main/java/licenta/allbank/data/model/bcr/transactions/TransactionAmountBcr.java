package licenta.allbank.data.model.bcr.transactions;

public class TransactionAmountBcr {
    private String currency;
    private float amount;

    public TransactionAmountBcr(String currency, float amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public float getAmount() {
        return amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
