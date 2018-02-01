package com.nzarudna.shoppinglist.model.template;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.unit.Unit;

import java.util.UUID;

/**
 * Product item user can add to his shopping card
 */
@Entity(tableName = "product_templates",
        foreignKeys = {
                @ForeignKey(entity = Category.class,
                        parentColumns = "category_id",
                        childColumns = "category_id"),
                @ForeignKey(entity = Unit.class,
                        parentColumns = "unit_id",
                        childColumns = "unit_id")
        })
public class ProductTemplate {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "template_id")
    private UUID templateID;

    @NonNull
    private String name;

    @ColumnInfo(name = "category_id")
    private UUID categoryID;

    @ColumnInfo(name = "unit_id")
    private UUID unitID;

    public ProductTemplate(@NonNull String name) {
        this.templateID = UUID.randomUUID();
        this.name = name;
        categoryID = Category.DEFAULT_CATEGORY_ID;
    }

    @NonNull
    public UUID getTemplateID() {
        return templateID;
    }

    public void setTemplateID(@NonNull UUID templateID) {
        this.templateID = templateID;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
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
        if (!name.equals(template.name)) return false;
        if (categoryID != null ? !categoryID.equals(template.categoryID) : template.categoryID != null)
            return false;
        return unitID != null ? unitID.equals(template.unitID) : template.unitID == null;
    }

    @Override
    public int hashCode() {
        int result = templateID.hashCode();
        result = 31 * result + name.hashCode();
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
}
