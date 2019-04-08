package com.nzarudna.shoppinglist.model.template;

import android.os.Parcel;
import android.os.Parcelable;

import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.unit.Unit;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Product item user can add to his shopping card
 */
@Entity(tableName = "product_templates",
        foreignKeys = {
                @ForeignKey(entity = Category.class,
                        parentColumns = "category_id",
                        childColumns = "category_id",
                        onDelete = ForeignKey.SET_NULL),
                @ForeignKey(entity = Unit.class,
                        parentColumns = "unit_id",
                        childColumns = "unit_id",
                        onUpdate = ForeignKey.SET_NULL)
        },
        indices = {
                @Index(value = "category_id"),
                @Index(value = "name")
        })
public class ProductTemplate implements Parcelable {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "template_id")
    private UUID templateID;

    private String name;

    @ColumnInfo(name = "category_id")
    private UUID categoryID;

    @ColumnInfo(name = "unit_id")
    private UUID unitID;

    public ProductTemplate() {
        this.templateID = UUID.randomUUID();
        categoryID = Category.DEFAULT_CATEGORY_ID;
    }

    protected ProductTemplate(Parcel in) {
        templateID = (UUID) in.readSerializable();
        name = in.readString();
        categoryID = (UUID) in.readSerializable();
        unitID = (UUID) in.readSerializable();
    }

    public static final Creator<ProductTemplate> CREATOR = new Creator<ProductTemplate>() {
        @Override
        public ProductTemplate createFromParcel(Parcel in) {
            return new ProductTemplate(in);
        }

        @Override
        public ProductTemplate[] newArray(int size) {
            return new ProductTemplate[size];
        }
    };

    @NonNull
    public UUID getTemplateID() {
        return templateID;
    }

    public void setTemplateID(@NonNull UUID templateID) {
        this.templateID = templateID;
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

    public UUID getUnitID() {
        return unitID;
    }

    public void setUnitID(UUID unitID) {
        this.unitID = unitID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductTemplate template = (ProductTemplate) o;

        if (!templateID.equals(template.templateID)) return false;
        if (name != null ? !name.equals(template.name) : template.name != null) return false;
        if (categoryID != null ? !categoryID.equals(template.categoryID) : template.categoryID != null)
            return false;
        return unitID != null ? unitID.equals(template.unitID) : template.unitID == null;
    }

    @Override
    public int hashCode() {
        int result = templateID.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (categoryID != null ? categoryID.hashCode() : 0);
        result = 31 * result + (unitID != null ? unitID.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductTemplate{" +
                "templateID=" + templateID +
                ", name='" + name + '\'' +
                ", categoryID=" + categoryID +
                ", unitID=" + unitID +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(templateID);
        parcel.writeString(name);
        parcel.writeSerializable(categoryID);
        parcel.writeSerializable(unitID);
    }
}
