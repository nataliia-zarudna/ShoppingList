package com.nzarudna.shoppinglist.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * List of products to buy
 */
@Entity(tableName = "products_lists",
        foreignKeys = {@ForeignKey(entity = User.class,
                parentColumns = "user_id",
                childColumns = "created_by"),
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "modified_by"),
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "assigned_id")},
        indices = @Index("created_by"))
public class ProductsList {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_ACTIVE, STATUS_ARCHIVED})
    public @interface ProductListStatus {
    }

    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_ARCHIVED = 2;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "list_id")
    private long listID;

    @NonNull
    private String name;

    @ColumnInfo(name = "created_at")
    @NonNull
    private Date createdAt;

    @ColumnInfo(name = "created_by")
    @NonNull
    private Integer createdBy;

    @ColumnInfo(name = "modified_at")
    private Date modifiedAt;

    @ColumnInfo(name = "modified_by")
    private Integer modifiedBy;

    private int status;

    @ColumnInfo(name = "assigned_id")
    private Integer assignedID;

    public ProductsList() {
        this.createdAt = new Date();
        this.setStatus(STATUS_ACTIVE);
    }

    public long getListID() {
        return listID;
    }

    public void setListID(long listID) {
        this.listID = listID;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public Date getCreatedAt() {
        return createdAt;
    }

    @NonNull
    public void setCreatedAt(@NonNull Date createdAt) {
        this.createdAt = createdAt;
    }

    @NonNull
    public Integer getCreatedBy() {
        return createdBy;
    }

    @NonNull
    public void setCreatedBy(@NonNull Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public @ProductListStatus
    int getStatus() {
        return status;
    }

    public void setStatus(@ProductListStatus int status) {
        this.status = status;
    }

    public Integer getAssignedID() {
        return assignedID;
    }

    public void setAssignedID(Integer assignedID) {
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
        int result = (int) listID;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + createdBy;
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + assignedID;
        return result;
    }

    @Override
    public String toString() {
        return "ProductsList{" +
                "listID=" + listID +
                ", name='" + name + '\'' +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                ", status=" + status +
                ", assignedID=" + assignedID +
                '}';
    }
}
