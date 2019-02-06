package com.nzarudna.shoppinglist.ui.productlist.edit.template;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.SharedPreferencesConstants;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItemWithListStatistics;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Nataliia on 04.03.2018.
 */

public class ChooseTemplateViewModel extends RecyclerViewModel<CategoryTemplateItemWithListStatistics> {

    @Inject
    ProductTemplateRepository mTemplateRepository;
    @Inject
    ProductListRepository mProductListRepository;

    private SharedPreferences mSharedPreferences;

    private UUID mProductListID;
    private ShoppingList mShoppingList;
    private boolean mIsGroupedView;

    @Inject
    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.mSharedPreferences = sharedPreferences;
        mIsGroupedView = mSharedPreferences.getBoolean(
                SharedPreferencesConstants.DEFAULT_CHOOSE_TEMPLATES_IS_GROUPED_VIEW, false);
    }

    public ChooseTemplateViewModel() {
    }

    public void setProductListID(UUID productListID) {
        this.mProductListID = productListID;
        mShoppingList = mProductListRepository.getShoppingList(productListID);
    }

    public ShoppingList getShoppingList() {
        return mShoppingList;
    }

    public void setIsGroupedView(boolean isGroupedView) {
        this.mIsGroupedView = isGroupedView;

        mSharedPreferences.edit()
                .putBoolean(SharedPreferencesConstants.DEFAULT_CHOOSE_TEMPLATES_IS_GROUPED_VIEW, isGroupedView)
                .apply();
    }

    public boolean isIsGroupedView() {
        return mIsGroupedView;
    }

    @Override
    public LiveData<PagedList<CategoryTemplateItemWithListStatistics>> getItems(int pageSize) {
        DataSource.Factory<Integer, CategoryTemplateItemWithListStatistics> templatesDSFactory =
                mTemplateRepository.getTemplates(mIsGroupedView, mProductListID);
        return new LivePagedListBuilder<>(templatesDSFactory, pageSize).build();
    }

    @Override
    public boolean canCreateNewItem() {
        return false;
    }
}
