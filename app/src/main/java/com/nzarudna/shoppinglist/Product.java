package com.nzarudna.shoppinglist;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Product item user can add to his shopping card
 */
@Entity(tableName = "products",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "category_id",
                childColumns = "category_id"))
public class Product {

    @PrimaryKey(autoGenerate = true)
    private int productID;

    private String name;

    @ColumnInfo(name = "category_id")
    private int categoryID;

}
