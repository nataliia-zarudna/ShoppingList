package com.nzarudna.shoppinglist.model.template;

import android.arch.persistence.room.ColumnInfo;

import com.nzarudna.shoppinglist.model.category.Category;

/**
 * Item in grouped by categories template list
 * with info about it's using in particular list
 */

public class CategoryTemplateItemWithListStatistics extends CategoryTemplateItem {

    @ColumnInfo(name = "is_used_in_list")
    private boolean mIsUsedInList;

    public CategoryTemplateItemWithListStatistics() {
    }

    public CategoryTemplateItemWithListStatistics(Category category) {
        super(category);
    }

    public CategoryTemplateItemWithListStatistics(ProductTemplate template, boolean isUsedInList) {
        super(template);
        mIsUsedInList = isUsedInList;
    }

    public boolean isIsUsedInList() {
        return mIsUsedInList;
    }

    public void setIsUsedInList(boolean isUsedInList) {
        this.mIsUsedInList = isUsedInList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CategoryTemplateItemWithListStatistics that = (CategoryTemplateItemWithListStatistics) o;

        return mIsUsedInList == that.mIsUsedInList;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mIsUsedInList ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CategoryTemplateItemWithListStatistics{" +
                "mIsUsedInList=" + mIsUsedInList +
                ", type='" + type + '\'' +
                ", mCategory=" + mCategory +
                ", mTemplate=" + mTemplate +
                '}';
    }
}
