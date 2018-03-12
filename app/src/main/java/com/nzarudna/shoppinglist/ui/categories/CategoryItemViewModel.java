package com.nzarudna.shoppinglist.ui.categories;

import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 09.03.2018.
 */

public class CategoryItemViewModel extends RecyclerItemViewModel<Category> {

    @Inject
    CategoryRepository mCategoryRepository;

    @Override
    public String getItemName() {
        return mItem.getName();
    }

    @Override
    public void removeItem() {
        mCategoryRepository.remove(mItem);
    }
}
