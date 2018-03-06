package com.nzarudna.shoppinglist.ui.productlist.edit.template;

import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItemWithListStatistics;

/**
 * Created by Nataliia on 05.03.2018.
 */

public class CategoryTemplateItemViewModel {

    private CategoryTemplateItemWithListStatistics mItem;
    private ShoppingList mShoppingList;

    public void setItem(CategoryTemplateItemWithListStatistics item) {
        mItem = item;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        mShoppingList = shoppingList;
    }

    public String getTemplateName() {
        return mItem != null ? mItem.getTemplate().getName() : "";
    }

    public boolean isTemplateUsed() {
        return mItem != null && mItem.isIsUsedInList();
    }

    public String getCategoryName() {
        return mItem != null ? mItem.getCategory().getName() : "";
    }

    public void setTemplateUsed(boolean isUsed) {
        if (isUsed) {
            mShoppingList.addProductFromTemplate(mItem.getTemplate(), null);
        } else {
            mShoppingList.removeProductsWithTemplate(mItem.getTemplate().getTemplateID());
        }
    }
}
