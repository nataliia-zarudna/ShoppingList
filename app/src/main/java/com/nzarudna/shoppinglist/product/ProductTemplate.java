package com.nzarudna.shoppinglist.product;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductTemplate that = (ProductTemplate) o;

        if (templateID != that.templateID) return false;
        if (!name.equals(that.name)) return false;
        return categoryID != null ? categoryID.equals(that.categoryID) : that.categoryID == null;
    }

    @Override
    public int hashCode() {
        int result = templateID;
        result = 31 * result + name.hashCode();
        result = 31 * result + (categoryID != null ? categoryID.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductTemplate{" +
                "templateID=" + templateID +
                ", name='" + name + '\'' +
                ", categoryID=" + categoryID +
                '}';
    }
}
