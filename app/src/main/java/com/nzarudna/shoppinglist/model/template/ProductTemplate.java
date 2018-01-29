package com.nzarudna.shoppinglist.model.template;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.nzarudna.shoppinglist.model.category.Category;

/**
 * Product item user can add to his shopping card
 */
@Entity(tableName = "product_templates",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "category_id",
                childColumns = "category_id"))
public class ProductTemplate {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "template_id")
    private int templateID;

    @NonNull
    private String name;

    @ColumnInfo(name = "category_id")
    private Integer categoryID;

    @ColumnInfo(name = "unit_id")
    private Integer unitID;

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public Integer getUnitID() {
        return unitID;
    }

    public void setUnitID(Integer unitID) {
        this.unitID = unitID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductTemplate template = (ProductTemplate) o;

        if (templateID != template.templateID) return false;
        if (!name.equals(template.name)) return false;
        if (categoryID != null ? !categoryID.equals(template.categoryID) : template.categoryID != null)
            return false;
        return unitID != null ? unitID.equals(template.unitID) : template.unitID == null;
    }

    @Override
    public int hashCode() {
        int result = templateID;
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
