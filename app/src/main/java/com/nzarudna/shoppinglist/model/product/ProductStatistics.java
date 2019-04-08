package com.nzarudna.shoppinglist.model.product;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;

/**
 * Class to query product lists with statistics
 * of bought/all products count
 */

public class ProductStatistics implements Parcelable {

    @ColumnInfo(name = "active_products_count")
    private int activeProductsCount;

    @ColumnInfo(name = "absent_products_count")
    private int absentProductsCount;

    @ColumnInfo(name = "bought_products_count")
    private int boughtProductsCount;


    public ProductStatistics() {
    }

    protected ProductStatistics(Parcel in) {
        activeProductsCount = in.readInt();
        absentProductsCount = in.readInt();
        boughtProductsCount = in.readInt();
    }

    public static final Creator<ProductStatistics> CREATOR = new Creator<ProductStatistics>() {
        @Override
        public ProductStatistics createFromParcel(Parcel in) {
            return new ProductStatistics(in);
        }

        @Override
        public ProductStatistics[] newArray(int size) {
            return new ProductStatistics[size];
        }
    };

    public int getActiveProductsCount() {
        return activeProductsCount;
    }

    public void setActiveProductsCount(int activeProductsCount) {
        this.activeProductsCount = activeProductsCount;
    }

    public int getBoughtProductsCount() {
        return boughtProductsCount;
    }

    public void setBoughtProductsCount(int boughtProductsCount) {
        this.boughtProductsCount = boughtProductsCount;
    }

    public int getAbsentProductsCount() {
        return absentProductsCount;
    }

    public void setAbsentProductsCount(int absentProductsCount) {
        this.absentProductsCount = absentProductsCount;
    }

    public int getAllProductsCount() {
        return activeProductsCount + boughtProductsCount + absentProductsCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductStatistics that = (ProductStatistics) o;

        if (activeProductsCount != that.activeProductsCount) return false;
        if (absentProductsCount != that.absentProductsCount) return false;
        return boughtProductsCount == that.boughtProductsCount;
    }

    @Override
    public int hashCode() {
        int result = activeProductsCount;
        result = 31 * result + absentProductsCount;
        result = 31 * result + boughtProductsCount;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "ProductStatistics{" +
                "activeProductsCount=" + activeProductsCount +
                ", absentProductsCount=" + absentProductsCount +
                ", boughtProductsCount=" + boughtProductsCount +
                '}';
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(activeProductsCount);
        parcel.writeInt(absentProductsCount);
        parcel.writeInt(boughtProductsCount);
    }
}
