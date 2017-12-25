package com.nzarudna.shoppinglist;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by nsirobaba on 12/25/17.
 */
@Entity(tableName = "categories")
public class Category {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    private int categoryID;
}
