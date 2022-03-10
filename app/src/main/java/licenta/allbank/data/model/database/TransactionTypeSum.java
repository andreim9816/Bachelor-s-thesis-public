package licenta.allbank.data.model.database;

public class TransactionTypeSum {
    private String transactionType;
    private float sum;

    public TransactionTypeSum(String transactionType, float sum) {
        this.transactionType = transactionType;
        this.sum = sum;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }
}
