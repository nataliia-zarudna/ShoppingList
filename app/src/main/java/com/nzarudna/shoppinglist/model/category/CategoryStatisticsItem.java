package com.nzarudna.shoppinglist.model.category;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;

/**
 * Category with statistics if there is any product/template
 * references to it
 */

public class CategoryStatisticsItem {

    @Embedded
    private Category category;

    @ColumnInfo(name = "is_used")
    boolean isUsed;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryStatisticsItem that = (CategoryStatisticsItem) o;

        if (isUsed != that.isUsed) return false;
        return category != null ? category.equals(that.category) : that.category == null;
    }

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (isUsed ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CategoryStatisticsItem{" +
                "category=" + category +
                ", isUsed=" + isUsed +
                '}';
    }
}
