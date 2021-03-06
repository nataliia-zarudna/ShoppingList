package com.nzarudna.shoppinglist.ui.categories;

import android.util.Log;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
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
        return new Category();
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
    protected String getUniqueNameValidationMessage() {
        return mResourceResolver.getString(R.string.category_unique_name_validation_message);
    }

    @Override
    protected void updateItem(AsyncResultListener<Category> asyncResultListener) {
        Log.d("magic", "mCategoryRepository updateItem " + mCategoryRepository.toString());

        mCategoryRepository.updateAsync(mItem, asyncResultListener);
    }

    @Override
    protected void createItem(AsyncResultListener asyncResultListener) {
        mCategoryRepository.createAsync(mItem, asyncResultListener);
    }
}
