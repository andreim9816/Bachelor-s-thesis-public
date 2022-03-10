package licenta.allbank.data.model.ing.accounts;

public class AccountDetailsIng {
    private String resourceId;
    private String iban;
    private String name;
    private String product;
    private String currency;

    public String getResourceId() {
        return resourceId;
    }

    public String getIban() {
        return iban;
    }

    public String getName() {
        return name;
    }

    public String getProduct() {
        return product;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "AccountDetailsIng{" +
                "resourceId='" + resourceId + '\'' +
                ", iban='" + iban + '\'' +
                ", name='" + name + '\'' +
                ", product='" + product + '\'' +
                ", currency='" + currency + '\'' +
                '}';
    }
}
