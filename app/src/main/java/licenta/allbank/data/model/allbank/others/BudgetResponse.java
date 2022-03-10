package licenta.allbank.data.model.allbank.others;

import com.google.gson.annotations.SerializedName;

public class BudgetResponse {
    @SerializedName("id")
    private final int budgetId;
    private String category;
    private String startDate;
    private String endDate;
    private float budget;

    public BudgetResponse(int budgetId, String category, String startDate, String endDate, float budget) {
        this.budgetId = budgetId;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
    }

    public int getBudgetId() {
        return budgetId;
    }

    public String getCategory() {
        return category;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public float getBudget() {
        return budget;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }
}
