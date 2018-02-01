package com.nzarudna.shoppinglist.model.category;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * Category of product
 */
@Entity(tableName = "categories")
public class Category {

    public static final UUID DEFAULT_CATEGORY_ID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

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
}
