package com.nzarudna.shoppinglist.model.product;

import android.arch.persistence.room.Embedded;
import android.support.annotation.StringDef;

import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.unit.Unit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Nataliia on 28.01.2018.
 */

public class CategoryProductItem {

    public static final String TYPE_PRODUCT = "product";
    public static final String TYPE_CATEGORY = "category";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TYPE_PRODUCT, TYPE_CATEGORY})
    @interface ItemType {
    }

    public CategoryProductItem() {
    }

    public CategoryProductItem(Product product, Unit unit) {
        this.product = product;
        this.unit = unit;
        type = TYPE_PRODUCT;
    }

    public CategoryProductItem(Category category) {
        this.category = category;
        type = TYPE_CATEGORY;
    }

    @ItemType
    private String type;

    @Embedded(prefix = "prod_")
    private Product product;

    @Embedded(prefix = "cat_")
    private Category category;

    @Embedded(prefix = "unit_")
    private Unit unit;

    @ItemType
    public String getType() {
        return type;
    }

    public void setType(@ItemType String type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryProductItem that = (CategoryProductItem) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (product != null ? !product.equals(that.product) : that.product != null) return false;
        if (category != null ? !category.equals(that.category) : that.category != null)
            return false;
        return unit != null ? unit.equals(that.unit) : that.unit == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CategoryProductItem{" +
                "type='" + type + '\'' +
                ", product=" + product +
                ", category=" + category +
                ", unit=" + unit +
                '}';
    }
}
