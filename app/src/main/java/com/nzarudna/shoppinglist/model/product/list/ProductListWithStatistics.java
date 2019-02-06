package com.nzarudna.shoppinglist.model.product.list;

import android.arch.persistence.room.Embedded;
import android.os.Parcel;
import android.os.Parcelable;

import com.nzarudna.shoppinglist.model.product.ProductStatistics;

import java.util.UUID;

/**
 * Class to query product lists with statistics
 * of bought/all products count
 */

public class ProductListWithStatistics implements Parcelable {

    @Embedded
    private ProductList productList;

    @Embedded
    private ProductStatistics productStatistics;

    public ProductListWithStatistics() {

    }

    public ProductList getProductList() {
        return productList;
    }

    public void setProductList(ProductList productList) {
        this.productList = productList;
    }

    public ProductStatistics getProductStatistics() {
        return productStatistics;
    }

    public void setProductStatistics(ProductStatistics productStatistics) {
        this.productStatistics = productStatistics;
    }

    public UUID getListID() {
        return productList.getListID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductListWithStatistics that = (ProductListWithStatistics) o;

        if (productList != null ? !productList.equals(that.productList) : that.productList != null)
            return false;
        return productStatistics != null ? productStatistics.equals(that.productStatistics) : that.productStatistics == null;
    }

    @Override
    public int hashCode() {
        int result = productList != null ? productList.hashCode() : 0;
        result = 31 * result + (productStatistics != null ? productStatistics.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductListWithStatistics{" +
                "productList=" + productList +
                ", productStatistics=" + productStatistics +
                '}';
    }

    protected ProductListWithStatistics(Parcel in) {
        productList = in.readParcelable(ProductList.class.getClassLoader());
        productStatistics = in.readParcelable(ProductStatistics.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(productList, flags);
        dest.writeParcelable(productStatistics, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductListWithStatistics> CREATOR = new Creator<ProductListWithStatistics>() {
        @Override
        public ProductListWithStatistics createFromParcel(Parcel in) {
            return new ProductListWithStatistics(in);
        }

        @Override
        public ProductListWithStatistics[] newArray(int size) {
            return new ProductListWithStatistics[size];
        }
    };
}
