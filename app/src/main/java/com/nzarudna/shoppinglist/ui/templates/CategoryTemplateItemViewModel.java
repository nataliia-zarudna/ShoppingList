package com.nzarudna.shoppinglist.ui.templates;

import android.databinding.Bindable;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.ui.ArrayItemViewModel;
import com.nzarudna.shoppinglist.ui.ObservableViewModel;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class CategoryTemplateItemViewModel extends ArrayItemViewModel<CategoryTemplateItem> {

    @Override
    public String getItemName() {
        if (mItem != null) {
            return mItem.getType().equals(CategoryTemplateItem.TYPE_TEMPLATE)
                    ? mItem.getTemplate().getName()
                    : mItem.getCategory().getName();
        } else {
            return "";
        }
    }

    /*@Bindable
    private CategoryTemplateItem mItem;

    public void setCategoryTemplateItem(CategoryTemplateItem item) {
        mItem = item;
        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public String getCategoryName() {
        return mItem != null ? mItem.getCategory().getName() : "";
    }

    public String getTemplateName() {
        return mItem != null ? mItem.getTemplate().getName() : "";
    }*/


}
