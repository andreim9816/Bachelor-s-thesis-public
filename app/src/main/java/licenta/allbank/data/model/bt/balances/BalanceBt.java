package licenta.allbank.data.model.bt.balances;

public class BalanceBt {
    private String balanceType;
    private String creditLimitIncluded;
    private String referenceDate;
    private BalanceAmountBt balanceAmount;

    public String getBalanceType() {
        return balanceType;
    }

    public String getCreditLimitIncluded() {
        return creditLimitIncluded;
    }

    public String getReferenceDate() {
        return referenceDate;
    }

    public BalanceAmountBt getBalanceAmount() {
        return balanceAmount;
    }
}
