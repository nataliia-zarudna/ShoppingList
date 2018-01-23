package com.nzarudna.shoppinglist.product;

import android.arch.persistence.room.ColumnInfo;

/**
 * Class to query product lists with statistics
 * of bought/all products count
 */

public class ProductListWithStatistics {

    @ColumnInfo(name = "list_id")
    private int listID;

    private String name;

    @ColumnInfo(name = "all_products_count")
    private int allProductsCount;

    @ColumnInfo(name = "bought_products_count")
    private int boughtProductsCount;

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAllProductsCount() {
        return allProductsCount;
    }

    public void setAllProductsCount(int allProductsCount) {
        this.allProductsCount = allProductsCount;
    }

    public int getBoughtProductsCount() {
        return boughtProductsCount;
    }

    public void setBoughtProductsCount(int boughtProductsCount) {
        this.boughtProductsCount = boughtProductsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductListWithStatistics that = (ProductListWithStatistics) o;

        if (listID != that.listID) return false;
        if (allProductsCount != that.allProductsCount) return false;
        if (boughtProductsCount != that.boughtProductsCount) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = listID;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + allProductsCount;
        result = 31 * result + boughtProductsCount;
        return result;
    }

    @Override
    public String toString() {
        return "ProductListWithStatistics{" +
                "listID=" + listID +
                ", name='" + name + '\'' +
                ", allProductsCount=" + allProductsCount +
                ", boughtProductsCount=" + boughtProductsCount +
                '}';
    }
}
