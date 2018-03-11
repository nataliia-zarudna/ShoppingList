package com.nzarudna.shoppinglist.ui.categories;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;

import com.nzarudna.shoppinglist.ShoppingListApplication;
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseEditItemDialogFragment;
import com.nzarudna.shoppinglist.ui.recyclerui.BaseRecyclerViewFragment;

/**
 * Created by Nataliia on 09.03.2018.
 */

public class CategoriesFragment extends BaseRecyclerViewFragment<Category, CategoriesViewModel, CategoryItemViewModel, EditCategoryViewModel> {

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    protected CategoriesViewModel getFragmentViewModel() {
        CategoriesViewModel viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        ShoppingListApplication.getAppComponent().inject(viewModel);
        return viewModel;
    }

    @Override
    protected CategoryItemViewModel getListItemViewModel() {
        CategoryItemViewModel itemViewModel = new CategoryItemViewModel();
        ShoppingListApplication.getAppComponent().inject(itemViewModel);
        return itemViewModel;
    }

    @Override
    protected EditCategoryViewModel getEditDialogViewModel() {
        EditCategoryViewModel viewModel = new EditCategoryViewModel();
        ShoppingListApplication.getAppComponent().inject(viewModel);
        return viewModel;
    }

    @Override
    protected DiffCallback<Category> getDiffCallback() {
        return new DiffCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.getCategoryID().equals(newItem.getCategoryID());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.equals(newItem);
            }
        };
    }
}
