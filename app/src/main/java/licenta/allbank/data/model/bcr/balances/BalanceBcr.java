package licenta.allbank.data.model.bcr.balances;

public class BalanceBcr {
    private String balanceType;
    private String referenceDate;
    private BalanceAmountBcr balanceAmount;

    public String getBalanceType() {
        return balanceType;
    }

    public String getReferenceDate() {
        return referenceDate;
    }

    public BalanceAmountBcr getBalanceAmount() {
        return balanceAmount;
    }
}
