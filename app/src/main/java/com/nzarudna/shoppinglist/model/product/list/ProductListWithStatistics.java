package com.nzarudna.shoppinglist.model.product.list;

import android.arch.persistence.room.ColumnInfo;

import java.util.UUID;

/**
 * Class to query product lists with statistics
 * of bought/all products count
 */

public class ProductListWithStatistics {

    @ColumnInfo(name = "list_id")
    private UUID listID;

    private String name;

    @ColumnInfo(name = "to_buy_products_count")
    private int toBuyProductsCount;

    @ColumnInfo(name = "absent_products_count")
    private int absentProductsCount;

    @ColumnInfo(name = "bought_products_count")
    private int boughtProductsCount;

    public ProductListWithStatistics(UUID listID, String name, int toBuyProductsCount, int absentProductsCount, int boughtProductsCount) {
        this.listID = listID;
        this.name = name;
        this.toBuyProductsCount = toBuyProductsCount;
        this.absentProductsCount = absentProductsCount;
        this.boughtProductsCount = boughtProductsCount;
    }

    public UUID getListID() {
        return listID;
    }

    public void setListID(UUID listID) {
        this.listID = listID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBoughtProductsCount() {
        return boughtProductsCount;
    }

    public void setBoughtProductsCount(int boughtProductsCount) {
        this.boughtProductsCount = boughtProductsCount;
    }

    public int getToBuyProductsCount() {
        return toBuyProductsCount;
    }

    public void setToBuyProductsCount(int toBuyProductsCount) {
        this.toBuyProductsCount = toBuyProductsCount;
    }

    public int getAbsentProductsCount() {
        return absentProductsCount;
    }

    public void setAbsentProductsCount(int absentProductsCount) {
        this.absentProductsCount = absentProductsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductListWithStatistics that = (ProductListWithStatistics) o;

        if (toBuyProductsCount != that.toBuyProductsCount) return false;
        if (absentProductsCount != that.absentProductsCount) return false;
        if (boughtProductsCount != that.boughtProductsCount) return false;
        if (listID != null ? !listID.equals(that.listID) : that.listID != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = listID != null ? listID.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + toBuyProductsCount;
        result = 31 * result + absentProductsCount;
        result = 31 * result + boughtProductsCount;
        return result;
    }

    @Override
    public String toString() {
        return "ProductListWithStatistics{" +
                "listID=" + listID +
                ", name='" + name + '\'' +
                ", toBuyProductsCount=" + toBuyProductsCount +
                ", absentProductsCount=" + absentProductsCount +
                ", boughtProductsCount=" + boughtProductsCount +
                '}';
    }
}
