package licenta.allbank.data.model.allbank.request;

import licenta.allbank.data.model.allbank.others.NewTransaction;

public class ConfirmTransactionRequest {
    private String username;
    private String allbankPassword;
    private NewTransaction transactionData;

    public ConfirmTransactionRequest(String username, String allbankPassword, NewTransaction transactionData) {
        this.username = username;
        this.allbankPassword = allbankPassword;
        this.transactionData = transactionData;
    }
}
