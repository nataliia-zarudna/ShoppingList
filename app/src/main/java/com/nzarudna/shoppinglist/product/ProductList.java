package com.nzarudna.shoppinglist.product;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.nzarudna.shoppinglist.DateUtils;
import com.nzarudna.shoppinglist.user.User;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * List of products to buy
 */
@Entity(tableName = "product_lists",
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
public class ProductList {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_ACTIVE, STATUS_ARCHIVED})
    public @interface ProductListStatus {
    }

    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_ARCHIVED = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SORT_LISTS_BY_NAME, SORT_LISTS_BY_CREATED_BY, SORT_LISTS_BY_CREATED_AT,
            SORT_LISTS_BY_MODIFIED_AT, SORT_LISTS_BY_ASSIGNED, SORT_LISTS_BY_PRODUCT_ORDER})
    public @interface ProductListSorting {
    }

    public static final int SORT_LISTS_BY_NAME = 1;
    public static final int SORT_LISTS_BY_CREATED_BY = 2;
    public static final int SORT_LISTS_BY_CREATED_AT = 3;
    public static final int SORT_LISTS_BY_MODIFIED_AT = 4;
    public static final int SORT_LISTS_BY_ASSIGNED = 5;
    public static final int SORT_LISTS_BY_PRODUCT_ORDER = 6;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "list_id")
    private int listID;

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

    @ProductListSorting
    private int sorting;

    @ColumnInfo(name = "assigned_id")
    private Integer assignedID;

    public ProductList() {
        this.createdAt = new Date();
        this.setStatus(STATUS_ACTIVE);
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
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

    public void setCreatedAt(@NonNull Date createdAt) {
        this.createdAt = createdAt;
    }

    @NonNull
    public Integer getCreatedBy() {
        return createdBy;
    }

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

    @ProductListSorting
    public int getSorting() {
        return sorting;
    }

    public void setSorting(@ProductListSorting int sorting) {
        this.sorting = sorting;
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

        ProductList that = (ProductList) o;

        if (listID != that.listID) return false;
        if (status != that.status) return false;
        if (sorting != that.sorting) return false;
        if (!name.equals(that.name)) return false;
        if (DateUtils.getTimeInSeconds(createdAt) != DateUtils.getTimeInSeconds(that.createdAt)) return false;
        if (!createdBy.equals(that.createdBy)) return false;
        if (modifiedAt != null ? !modifiedAt.equals(that.modifiedAt) : that.modifiedAt != null)
            return false;
        if (modifiedBy != null ? !modifiedBy.equals(that.modifiedBy) : that.modifiedBy != null)
            return false;
        return assignedID != null ? assignedID.equals(that.assignedID) : that.assignedID == null;
    }

    @Override
    public int hashCode() {
        int result = listID;
        result = 31 * result + name.hashCode();
        result = 31 * result + createdAt.hashCode();
        result = 31 * result + createdBy.hashCode();
        result = 31 * result + (modifiedAt != null ? modifiedAt.hashCode() : 0);
        result = 31 * result + (modifiedBy != null ? modifiedBy.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + sorting;
        result = 31 * result + (assignedID != null ? assignedID.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductList{" +
                "listID=" + listID +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", modifiedAt=" + modifiedAt +
                ", modifiedBy=" + modifiedBy +
                ", status=" + status +
                ", sorting=" + sorting +
                ", assignedID=" + assignedID +
                '}';
    }
}
