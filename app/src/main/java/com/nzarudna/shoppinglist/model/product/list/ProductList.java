package com.nzarudna.shoppinglist.model.product.list;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.nzarudna.shoppinglist.model.user.User;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.UUID;

/**
 * List of products to buy
 */
@Entity(tableName = "product_lists",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "created_by",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "modified_by",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "assigned_id",
                        onDelete = ForeignKey.SET_NULL)
        },
        indices = {@Index(value = "status")})
public class ProductList implements Parcelable {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_ACTIVE, STATUS_ARCHIVED})
    public @interface ProductListStatus {
    }

    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_ARCHIVED = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SORT_PRODUCTS_BY_NAME, SORT_PRODUCTS_BY_STATUS, SORT_PRODUCTS_BY_ORDER})
    public @interface ProductSorting {
    }

    public static final int SORT_PRODUCTS_BY_NAME = 1;
    public static final int SORT_PRODUCTS_BY_STATUS = 2;
    public static final int SORT_PRODUCTS_BY_ORDER = 3;

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "list_id")
    private UUID listID;

    @NonNull
    private String name;

    @ColumnInfo(name = "created_at")
    @NonNull
    private Date createdAt;

    @ColumnInfo(name = "created_by")
    private UUID createdBy;

    @ColumnInfo(name = "modified_at")
    private Date modifiedAt;

    @ColumnInfo(name = "modified_by")
    private UUID modifiedBy;

    @ProductListStatus
    private int status;

    @ProductSorting
    private int sorting;

    @ColumnInfo(name = "is_grouped_view")
    private boolean isGroupedView;

    @ColumnInfo(name = "assigned_id")
    private UUID assignedID;

    public ProductList(@NonNull String name, @NonNull UUID createdBy) {
        this.listID = UUID.randomUUID();
        this.name = name;
        this.createdBy = createdBy;
        this.createdAt = new Date();
        this.setStatus(STATUS_ACTIVE);
    }

    protected ProductList(Parcel in) {
        listID = (UUID) in.readSerializable();
        name = in.readString();
        createdAt = new Date(in.readLong());
        createdBy = (UUID) in.readSerializable();
        modifiedAt = new Date(in.readLong());
        modifiedBy = (UUID) in.readSerializable();
        status = in.readInt();
        sorting = in.readInt();
        isGroupedView = in.readByte() != 0;
        assignedID = (UUID) in.readSerializable();
    }

    public static final Creator<ProductList> CREATOR = new Creator<ProductList>() {
        @Override
        public ProductList createFromParcel(Parcel in) {
            return new ProductList(in);
        }

        @Override
        public ProductList[] newArray(int size) {
            return new ProductList[size];
        }
    };

    @NonNull
    public UUID getListID() {
        return listID;
    }

    public void setListID(@NonNull UUID listID) {
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

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(@NonNull UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public UUID getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(UUID modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public @ProductListStatus
    int getStatus() {
        return status;
    }

    public void setStatus(@ProductListStatus int status) {
        this.status = status;
    }

    @ProductSorting
    public int getSorting() {
        return sorting;
    }

    public void setSorting(@ProductSorting int sorting) {
        this.sorting = sorting;
    }

    public boolean isGroupedView() {
        return isGroupedView;
    }

    public void setIsGroupedView(boolean isGroupedView) {
        this.isGroupedView = isGroupedView;
    }

    public UUID getAssignedID() {
        return assignedID;
    }

    public void setAssignedID(UUID assignedID) {
        this.assignedID = assignedID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductList that = (ProductList) o;

        if (status != that.status) return false;
        if (sorting != that.sorting) return false;
        if (isGroupedView != that.isGroupedView) return false;
        if (!listID.equals(that.listID)) return false;
        if (!name.equals(that.name)) return false;
        if (!createdAt.equals(that.createdAt)) return false;
        if (!createdBy.equals(that.createdBy)) return false;
        if (modifiedAt != null ? !modifiedAt.equals(that.modifiedAt) : that.modifiedAt != null)
            return false;
        if (modifiedBy != null ? !modifiedBy.equals(that.modifiedBy) : that.modifiedBy != null)
            return false;
        return assignedID != null ? assignedID.equals(that.assignedID) : that.assignedID == null;
    }

    @Override
    public int hashCode() {
        int result = listID.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + createdAt.hashCode();
        result = 31 * result + createdBy.hashCode();
        result = 31 * result + (modifiedAt != null ? modifiedAt.hashCode() : 0);
        result = 31 * result + (modifiedBy != null ? modifiedBy.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + sorting;
        result = 31 * result + (isGroupedView ? 1 : 0);
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
                ", isGroupedView=" + isGroupedView +
                ", assignedID=" + assignedID +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(listID);
        parcel.writeString(name);
        parcel.writeLong(createdAt.getTime());
        parcel.writeSerializable(createdBy);
        parcel.writeLong(modifiedAt.getTime());
        parcel.writeSerializable(modifiedBy);
        parcel.writeInt(status);
        parcel.writeInt(sorting);
        parcel.writeByte((byte) (isGroupedView ? 1 : 0));
        parcel.writeSerializable(assignedID);
    }
}
