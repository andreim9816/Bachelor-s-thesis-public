package licenta.allbank.data.model.bcr.payments;

public class PaymentResponseBcr {
    private String paymentId;
    private String transactionStatus;
    private String scaRedirect;

    public String getPaymentId() {
        return paymentId;
    }

    public String getScaRedirect() {
        return scaRedirect;
    }
}
