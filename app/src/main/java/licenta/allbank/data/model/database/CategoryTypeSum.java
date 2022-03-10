package licenta.allbank.data.model.database;

public class CategoryTypeSum {
    private float sum;
    private String transactionCategory;

    public CategoryTypeSum(float sum, String transactionCategory) {
        this.sum = sum;
        this.transactionCategory = transactionCategory;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }

    public String getCategoryType() {
        return transactionCategory;
    }

    public void setCategoryType(String transactionCategory) {
        this.transactionCategory = transactionCategory;
    }
}
