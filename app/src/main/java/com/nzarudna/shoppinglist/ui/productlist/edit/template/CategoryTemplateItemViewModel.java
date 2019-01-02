package com.nzarudna.shoppinglist.ui.productlist.edit.template;

import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItemWithListStatistics;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewModel;

/**
 * Created by Nataliia on 05.03.2018.
 */

public class CategoryTemplateItemViewModel extends RecyclerItemViewModel<CategoryTemplateItemWithListStatistics> {

    private CategoryTemplateItemWithListStatistics mItem;
    private ShoppingList mShoppingList;

    public void setItem(CategoryTemplateItemWithListStatistics item) {
        mItem = item;
    }

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

    public void setShoppingList(ShoppingList shoppingList) {
        mShoppingList = shoppingList;
    }

    public boolean isTemplateUsed() {
        return mItem != null && mItem.isIsUsedInList();
    }

    public void setTemplateUsed(boolean isUsed) {
        if (isUsed) {
            mShoppingList.addProductFromTemplate(mItem.getTemplate(), null);
        } else {
            mShoppingList.removeProductsWithTemplate(mItem.getTemplate().getTemplateID(), null);
        }
    }

    @Override
    public void removeItem(AsyncListener listener) {

    }
}
