package com.nzarudna.shoppinglist.ui.categories;

import android.util.Log;

import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.EditDialogViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 09.03.2018.
 */

public class EditCategoryViewModel extends EditDialogViewModel<Category> {

    @Inject
    CategoryRepository mCategoryRepository;

    @Override
    protected Category createItemObject() {
        return new Category("");
    }

    @Override
    public String getName() {
        return mItem.getName();
    }

    @Override
    public void setName(String name) {
        mItem.setName(name);
    }

    @Override
    protected void updateItem() {
        Log.d("magic", "mCategoryRepository updateItem " + mCategoryRepository.toString());

        mCategoryRepository.update(mItem);
    }

    @Override
    protected void createItem() {
        mCategoryRepository.create(mItem);
    }
}
