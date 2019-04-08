package com.nzarudna.shoppinglist.model.category;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Category of product
 */
@Entity(tableName = "categories",
        indices = {
                @Index(value = "name")
        })
public class Category implements Parcelable, Cloneable {

    public static final String DEFAULT_CATEGORY_ID_STRING = "ffffffff-ffff-ffff-ffff-ffffffffffff";
    public static final UUID DEFAULT_CATEGORY_ID = UUID.fromString(DEFAULT_CATEGORY_ID_STRING);

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "category_id")
    private UUID categoryID;

    private String name;

    public Category() {
        categoryID = UUID.randomUUID();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name != null) ? name.trim() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (!categoryID.equals(category.categoryID)) return false;
        return name != null ? name.equals(category.name) : category.name == null;
    }

    @Override
    public int hashCode() {
        int result = categoryID.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
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

    @Override
    public Category clone() throws CloneNotSupportedException {
        return (Category) super.clone();
    }
}
