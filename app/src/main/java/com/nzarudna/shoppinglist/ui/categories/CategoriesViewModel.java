package com.nzarudna.shoppinglist.ui.categories;

import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

/**
 * Created by Nataliia on 09.03.2018.
 */

public class CategoriesViewModel extends RecyclerViewModel<Category> {

    @Inject
    CategoryRepository mCategoryRepository;

    @Override
    public LiveData<PagedList<Category>> getItems(int pageSize) {
        DataSource.Factory<Integer, Category> categoryFactory = mCategoryRepository.getAllCategories();
        return new LivePagedListBuilder<>(categoryFactory, pageSize).build();
    }
}
