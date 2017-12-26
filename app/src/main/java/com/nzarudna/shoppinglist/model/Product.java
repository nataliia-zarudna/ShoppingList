package com.nzarudna.shoppinglist.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Product item user can add to his shopping card
 */
@Entity(tableName = "products",
        foreignKeys = {@ForeignKey(entity = Category.class,
                parentColumns = "category_id",
                childColumns = "category_id"),
                @ForeignKey(entity = ShoppingList.class,
                        parentColumns = "list_id", childColumns = "list_id")})
public class Product {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    private int productID;

    private String name;

    @ColumnInfo(name = "category_id")
    private int categoryID;

    @ColumnInfo(name = "list_id")
    @NonNull
    private int listID;

    @ColumnInfo(name = "unit_id")
    private int unitID;

    @ColumnInfo(name = "count")
    private int count;

    private String comment;

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

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
