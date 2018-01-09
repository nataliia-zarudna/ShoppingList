package com.nzarudna.shoppinglist.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Product item user can add to his shopping list
 */
@Entity(tableName = "products",
        foreignKeys = {@ForeignKey(entity = Category.class,
                parentColumns = "category_id",
                childColumns = "category_id"),
                @ForeignKey(entity = ProductsList.class,
                        parentColumns = "list_id", childColumns = "list_id")})
public class Product {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TO_BUY, ABSENT, BOUGHT})
    public @interface ProductStatus {}

    public static final int TO_BUY = 1;
    public static final int ABSENT = 2;
    public static final int BOUGHT = 3;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    private int productID;

    @NonNull
    private String name;

    @ColumnInfo(name = "category_id")
    private Integer categoryID;

    @ColumnInfo(name = "list_id")
    private int listID;

    @ColumnInfo(name = "unit_id")
    private int unitID;

    @ColumnInfo(name = "count")
    private double count;

    @ProductStatus
    private int status;

    private String comment;

    private double order;

    public Product() {
        this.status = TO_BUY;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public int getUnitID() {
        return unitID;
    }

    public void setUnitID(int unitID) {
        this.unitID = unitID;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    @NonNull
    public int getStatus() {
        return status;
    }

    public void setStatus(@NonNull int status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getOrder() {
        return order;
    }

    public void setOrder(double order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (productID != product.productID) return false;
        if (listID != product.listID) return false;
        if (unitID != product.unitID) return false;
        if (Double.compare(product.count, count) != 0) return false;
        if (status != product.status) return false;
        if (Double.compare(product.order, order) != 0) return false;
        if (!name.equals(product.name)) return false;
        if (categoryID != null ? !categoryID.equals(product.categoryID) : product.categoryID != null)
            return false;
        return comment != null ? comment.equals(product.comment) : product.comment == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = productID;
        result = 31 * result + name.hashCode();
        result = 31 * result + (categoryID != null ? categoryID.hashCode() : 0);
        result = 31 * result + listID;
        result = 31 * result + unitID;
        temp = Double.doubleToLongBits(count);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + status;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        temp = Double.doubleToLongBits(order);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productID=" + productID +
                ", name='" + name + '\'' +
                ", categoryID=" + categoryID +
                ", listID=" + listID +
                ", unitID=" + unitID +
                ", count=" + count +
                ", status=" + status +
                ", comment='" + comment + '\'' +
                ", order=" + order +
                '}';
    }
}
