package com.nzarudna.shoppinglist.model.template;

import android.arch.persistence.room.Embedded;
import android.support.annotation.StringDef;

import com.nzarudna.shoppinglist.model.category.Category;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Item in grouped by categories template list
 */

public class CategoryTemplateItem {

    public static final String TYPE_CATEGORY = "category";
    public static final String TYPE_TEMPLATE = "template";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TYPE_CATEGORY, TYPE_TEMPLATE})
    @interface ItemType {
    }

    public CategoryTemplateItem() {}

    public CategoryTemplateItem(Category category) {
        mCategory = category;
        type = TYPE_CATEGORY;
    }

    public CategoryTemplateItem(ProductTemplate template) {
        mTemplate = template;
        type = TYPE_TEMPLATE;
    }

    @ItemType
    private String type;

    @Embedded(prefix = "cat_")
    private Category mCategory;

    @Embedded(prefix = "temp_")
    private ProductTemplate mTemplate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        this.mCategory = category;
    }

    public ProductTemplate getTemplate() {
        return mTemplate;
    }

    public void setTemplate(ProductTemplate template) {
        this.mTemplate = template;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryTemplateItem that = (CategoryTemplateItem) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (mCategory != null ? !mCategory.equals(that.mCategory) : that.mCategory != null)
            return false;
        return mTemplate != null ? mTemplate.equals(that.mTemplate) : that.mTemplate == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (mCategory != null ? mCategory.hashCode() : 0);
        result = 31 * result + (mTemplate != null ? mTemplate.hashCode() : 0);
        return result;
    }
}
