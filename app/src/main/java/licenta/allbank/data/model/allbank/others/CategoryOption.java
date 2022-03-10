package licenta.allbank.data.model.allbank.others;

import androidx.annotation.DrawableRes;

public class CategoryOption {
    private final String categoryType;
    private final @DrawableRes
    int categoryImageViewUnselected;
    private final @DrawableRes
    int categoryImageViewSelected;
    private boolean selected;
    private Integer color;

    public CategoryOption(String categoryType, int categoryImageViewUnselected, int categoryImageViewSelected, boolean selected, Integer color) {
        this.categoryType = categoryType;
        this.categoryImageViewUnselected = categoryImageViewUnselected;
        this.categoryImageViewSelected = categoryImageViewSelected;
        this.selected = selected;
        this.color = color;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public int getCategoryImageViewUnselected() {
        return categoryImageViewUnselected;
    }

    public int getCategoryImageViewSelected() {
        return categoryImageViewSelected;
    }

    public boolean isSelected() {
        return selected;
    }

    public Integer getColor() {
        return color;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
