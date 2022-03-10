package licenta.allbank.data.model.allbank.others;

public class IbanNameSearchItem {
    private final String iban;
    private final String name;

    public IbanNameSearchItem(String iban, String name) {
        this.iban = iban;
        this.name = name;
    }

    public String getIban() {
        return iban;
    }

    public String getName() {
        return name;
    }
}
