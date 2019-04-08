package com.nzarudna.shoppinglist.model.template;

import android.os.Parcel;
import android.os.Parcelable;

import com.nzarudna.shoppinglist.model.category.Category;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;

/**
 * Item in grouped by categories template list
 */

public class CategoryTemplateItem implements Parcelable {

    public static final String TYPE_CATEGORY = "category";
    public static final String TYPE_TEMPLATE = "template";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TYPE_CATEGORY, TYPE_TEMPLATE})
    @interface ItemType {
    }

    @ItemType
    @ColumnInfo(name = "type")
    protected String mType;

    @Embedded(prefix = "cat_")
    protected Category mCategory;

    @Embedded(prefix = "temp_")
    protected ProductTemplate mTemplate;

    public CategoryTemplateItem() {
    }

    public CategoryTemplateItem(Category category) {
        mCategory = category;
        mType = TYPE_CATEGORY;
    }

    public CategoryTemplateItem(ProductTemplate template) {
        mTemplate = template;
        mType = TYPE_TEMPLATE;
    }

    public CategoryTemplateItem(Parcel parcel) {
        mType = parcel.readString();
        mCategory = parcel.readParcelable(Category.class.getClassLoader());
        mTemplate = parcel.readParcelable(ProductTemplate.class.getClassLoader());
    }

    public static final Creator<CategoryTemplateItem> CREATOR = new Creator<CategoryTemplateItem>() {
        @Override
        public CategoryTemplateItem createFromParcel(Parcel parcel) {
            return new CategoryTemplateItem(parcel);
        }

        @Override
        public CategoryTemplateItem[] newArray(int i) {
            return new CategoryTemplateItem[0];
        }
    };

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        this.mCategory = category;
    }

    public ProductTemplate getTemplate() {
        return mTemplate;
    }

    public void setTemplate(ProductTemplate template) {
        this.mTemplate = template;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryTemplateItem that = (CategoryTemplateItem) o;

        if (mType != null ? !mType.equals(that.mType) : that.mType != null) return false;
        if (mCategory != null ? !mCategory.equals(that.mCategory) : that.mCategory != null)
            return false;
        return mTemplate != null ? mTemplate.equals(that.mTemplate) : that.mTemplate == null;
    }

    @Override
    public int hashCode() {
        int result = mType != null ? mType.hashCode() : 0;
        result = 31 * result + (mCategory != null ? mCategory.hashCode() : 0);
        result = 31 * result + (mTemplate != null ? mTemplate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CategoryTemplateItem{" +
                "type='" + mType + '\'' +
                ", mCategory=" + mCategory +
                ", mTemplate=" + mTemplate +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mType);
        parcel.writeParcelable(mCategory, i);
        parcel.writeParcelable(mTemplate, i);
    }
}
