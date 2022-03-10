package licenta.allbank.data.model.bcr.accounts;

import licenta.allbank.data.model.bcr.transactions.TransactionsObjectBcr;

public class AccountDetailsBcr {
    private String iban;
    private String name;
    private String resourceId;
    private String details;
    private String currency;
    private float balance;
    private TransactionsObjectBcr transactions;

    public String getIban() {
        return iban;
    }

    public String getName() {
        return name;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getDetails() {
        return details;
    }

    public String getCurrency() {
        return currency;
    }

    public float getBalance() {
        return balance;
    }

    public TransactionsObjectBcr getTransactions() {
        return transactions;
    }
}
