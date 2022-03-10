package licenta.allbank.data.model.allbank.others;

public class TransactionOption {
    private final int pastDays;
    private final String text;

    public TransactionOption(int pastDays, String text) {
        this.pastDays = pastDays;
        this.text = text;
    }

    public int getPastDays() {
        return pastDays;
    }

    public String getText() {
        return text;
    }
}
