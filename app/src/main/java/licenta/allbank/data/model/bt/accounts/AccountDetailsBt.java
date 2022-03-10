package licenta.allbank.data.model.bt.accounts;

import licenta.allbank.data.model.bt.transactions.TransactionsObjectBt;

public class AccountDetailsBt {
    private String iban;
    private String resourceId;
    private String product;
    private String name;
    private String cashAccountType;
    private String currency;
    private float balance;
    private TransactionsObjectBt transactions;

    public String getIban() {
        return iban;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getProduct() {
        return product;
    }

    public String getName() {
        return name;
    }

    public String getCashAccountType() {
        return cashAccountType;
    }

    public String getCurrency() {
        return currency;
    }

    public float getBalance() {
        return balance;
    }

    public TransactionsObjectBt getTransactions() {
        return transactions;
    }
}
