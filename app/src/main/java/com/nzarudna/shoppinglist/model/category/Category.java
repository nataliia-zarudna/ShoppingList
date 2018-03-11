package com.nzarudna.shoppinglist.model.category;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Category of product
 */
@Entity(tableName = "categories")
public class Category implements Parcelable {

    public static final String DEFAULT_CATEGORY_ID_STRING = "ffffffff-ffff-ffff-ffff-ffffffffffff";
    public static final UUID DEFAULT_CATEGORY_ID = UUID.fromString(DEFAULT_CATEGORY_ID_STRING);

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "category_id")
    private UUID categoryID;

    @NonNull
    private String name;

    public Category(@NonNull String name) {
        categoryID = UUID.randomUUID();
        this.name = name;
    }

    protected Category(Parcel in) {
        categoryID = (UUID) in.readSerializable();
        name = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @NonNull
    public UUID getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(@NonNull UUID categoryID) {
        this.categoryID = categoryID;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (!categoryID.equals(category.categoryID)) return false;
        return name.equals(category.name);
    }

    @Override
    public int hashCode() {
        int result = categoryID.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryID=" + categoryID +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(categoryID);
        parcel.writeString(name);
    }
}
