package licenta.allbank.data.model.database;

public class TransactionTypeAverageBank {
    private float transactionAvg;
    private String transactionType;
    private String bank;

    public TransactionTypeAverageBank(float transactionAvg, String transactionType, String bank) {
        this.transactionAvg = transactionAvg;
        this.transactionType = transactionType;
        this.bank = bank;
    }

    public float getTransactionAvg() {
        return transactionAvg;
    }

    public void setTransactionAvg(float transactionAvg) {
        this.transactionAvg = transactionAvg;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
