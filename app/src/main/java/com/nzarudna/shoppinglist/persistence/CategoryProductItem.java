package com.nzarudna.shoppinglist.persistence;

import android.arch.persistence.room.Embedded;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import com.nzarudna.shoppinglist.product.Category;
import com.nzarudna.shoppinglist.product.Product;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Nataliia on 28.01.2018.
 */

public class CategoryProductItem {

    public static final String ITEM_PRODUCT_TYPE = "product";
    public static final String ITEM_CATEGORY_TYPE = "category";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ITEM_PRODUCT_TYPE, ITEM_CATEGORY_TYPE})
    @interface ItemType {}

    public CategoryProductItem() {}

    public CategoryProductItem(Product product) {
        this.product = product;
        type = ITEM_PRODUCT_TYPE;
    }

    public CategoryProductItem(Category category) {
        this.category = category;
        type = ITEM_CATEGORY_TYPE;
    }

    @ItemType
    private String type;

    @Embedded(prefix = "prod_")
    private Product product;

    @Embedded(prefix = "cat_")
    private Category category;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryProductItem that = (CategoryProductItem) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (product != null ? !product.equals(that.product) : that.product != null) return false;
        return category != null ? category.equals(that.category) : that.category == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CategoryProductItem{" +
                "type='" + type + '\'' +
                ", product=" + product +
                ", category=" + category +
                '}';
    }
}