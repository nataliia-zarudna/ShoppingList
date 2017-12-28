package com.nzarudna.shoppinglist.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * List of products to buy
 */
@Entity(tableName = "products_lists")
public class ProductsList {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_ACTIVE, STATUS_ARCHIVED})
    public @interface ProductListStatus {}
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_ARCHIVED = 2;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "list_id")
    private int listID;

    private String name;

    @ColumnInfo(name = "created_by")
    private int createdBy;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    private int status;

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

    public @ProductListStatus int getStatus() {
        return status;
    }

    public void setStatus(@ProductListStatus int status) {
        this.status = status;
    }

    public int getAssignedID() {
        return assignedID;
    }

    public void setAssignedID(int assignedID) {
        this.assignedID = assignedID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductsList that = (ProductsList) o;

        if (listID != that.listID) return false;
        if (createdBy != that.createdBy) return false;
        if (status != that.status) return false;
        if (assignedID != that.assignedID) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return createdAt != null ? createdAt.equals(that.createdAt) : that.createdAt == null;
    }

    @Override
    public int hashCode() {
        int result = listID;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + createdBy;
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + assignedID;
        return result;
    }
}
