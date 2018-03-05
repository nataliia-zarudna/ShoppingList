package com.nzarudna.shoppinglist.model.template;

/**
 * Created by Nataliia on 05.03.2018.
 */

public class TemplateItemViewModel {

    private CategoryTemplateItemWithListStatistics mItem;

    public void setItem(CategoryTemplateItemWithListStatistics item) {
        mItem = item;
    }

    public String getTemplateName() {
        return mItem != null ? mItem.getTemplate().getName() : "";
    }

    public boolean isTemplateUsed() {
        return mItem != null && mItem.isIsUsedInList();
    }
}
