package com.nzarudna.shoppinglist.model.product;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.unit.Unit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.UUID;

/**
 * Product item user can add to his shopping list
 */
@Entity(tableName = "products",
        foreignKeys = {
                @ForeignKey(entity = Category.class,
                        parentColumns = "category_id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = ProductList.class,
                        parentColumns = "list_id",
                        childColumns = "list_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = ProductTemplate.class,
                        parentColumns = "template_id",
                        childColumns = "template_id",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Unit.class,
                        parentColumns = "unit_id",
                        childColumns = "unit_id",
                        onDelete = ForeignKey.SET_NULL)},
        indices = {
                @Index(value = {"list_id", "name"}),
                @Index(value = {"list_id", "product_id"}),
                @Index(value = "category_id"),
                @Index(value = {"template_id", "list_id"})
        })
public class Product implements Cloneable, Parcelable {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ACTIVE, ABSENT, BOUGHT})
    public @interface ProductStatus {
    }

    public static final int ACTIVE = 1;
    public static final int ABSENT = 2;
    public static final int BOUGHT = 3;

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "product_id")
    private UUID productID;

    private String name;

    @ColumnInfo(name = "category_id")
    private UUID categoryID;

    @ColumnInfo(name = "list_id")
    private UUID listID;

    @ColumnInfo(name = "unit_id")
    private UUID unitID;

    @ColumnInfo(name = "count")
    private double count;

    @ProductStatus
    private int status;

    private String comment;

    @ColumnInfo(name = "template_id")
    private UUID templateID;

    private double order;

    public Product() {
        this.productID = UUID.randomUUID();
        this.status = ACTIVE;
        this.categoryID = Category.DEFAULT_CATEGORY_ID;
    }

    @NonNull
    public UUID getProductID() {
        return productID;
    }

    public void setProductID(@NonNull UUID productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name != null) ? name.trim() : null;
    }

    public UUID getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(UUID categoryID) {
        this.categoryID = categoryID;
    }

    public UUID getListID() {
        return listID;
    }

    public void setListID(UUID listID) {
        this.listID = listID;
    }

    public UUID getUnitID() {
        return unitID;
    }

    public void setUnitID(UUID unitID) {
        this.unitID = unitID;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UUID getTemplateID() {
        return templateID;
    }

    public void setTemplateID(UUID templateID) {
        this.templateID = templateID;
    }

    public double getOrder() {
        return order;
    }

    public void setOrder(double order) {
        this.order = order;
    }

    @Override
    public Product clone() throws CloneNotSupportedException {
        Product clone = (Product) super.clone();
        clone.setProductID(UUID.randomUUID());

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (Double.compare(product.count, count) != 0) return false;
        if (status != product.status) return false;
        if (Double.compare(product.order, order) != 0) return false;
        if (!productID.equals(product.productID)) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        if (categoryID != null ? !categoryID.equals(product.categoryID) : product.categoryID != null)
            return false;
        if (listID != null ? !listID.equals(product.listID) : product.listID != null) return false;
        if (unitID != null ? !unitID.equals(product.unitID) : product.unitID != null) return false;
        if (comment != null ? !comment.equals(product.comment) : product.comment != null)
            return false;
        return templateID != null ? templateID.equals(product.templateID) : product.templateID == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = productID.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (categoryID != null ? categoryID.hashCode() : 0);
        result = 31 * result + (listID != null ? listID.hashCode() : 0);
        result = 31 * result + (unitID != null ? unitID.hashCode() : 0);
        temp = Double.doubleToLongBits(count);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + status;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (templateID != null ? templateID.hashCode() : 0);
        temp = Double.doubleToLongBits(order);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productID=" + productID +
                ", name='" + name + '\'' +
                ", categoryID=" + categoryID +
                ", listID=" + listID +
                ", unitID=" + unitID +
                ", templateID=" + templateID +
                ", count=" + count +
                ", status=" + status +
                ", comment='" + comment + '\'' +
                ", order=" + order +
                '}';
    }

    protected Product(Parcel in) {
        productID = (UUID) in.readSerializable();
        name = in.readString();
        categoryID = (UUID) in.readSerializable();
        listID = (UUID) in.readSerializable();
        unitID = (UUID) in.readSerializable();
        templateID = (UUID) in.readSerializable();
        count = in.readDouble();
        status = in.readInt();
        comment = in.readString();
        order = in.readDouble();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeSerializable(productID);
        parcel.writeString(name);
        parcel.writeSerializable(categoryID);
        parcel.writeSerializable(listID);
        parcel.writeSerializable(unitID);
        parcel.writeSerializable(templateID);
        parcel.writeDouble(count);
        parcel.writeInt(status);
        parcel.writeString(comment);
        parcel.writeDouble(order);
    }
}
