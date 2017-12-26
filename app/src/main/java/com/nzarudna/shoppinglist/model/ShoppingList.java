package com.nzarudna.shoppinglist.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * List of products to buy
 */
@Entity(tableName = "shopping_lists")
public class ShoppingList {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "list_id")
    private int listID;

    private String name;

    @ColumnInfo(name = "created_by")
    private int createdBy;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "is_done")
    private boolean isDone;

    @ColumnInfo(name = "assigned_id")
    private int assignedID;

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

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public int getAssignedID() {
        return assignedID;
    }

    public void setAssignedID(int assignedID) {
        this.assignedID = assignedID;
    }
}
