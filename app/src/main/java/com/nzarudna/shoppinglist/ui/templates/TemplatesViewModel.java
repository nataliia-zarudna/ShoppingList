package com.nzarudna.shoppinglist.ui.templates;

import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;

import java.util.UUID;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class TemplatesViewModel extends RecyclerViewModel<CategoryTemplateItem> {

    private static final String SHARED_PREFERENCE_TEMPLATES_IS_GROUP_VIEW = "templates_is_group_view";

    @Inject
    ProductTemplateRepository mTemplateRepository;
    @Inject
    ProductListRepository mProductListRepository;
    @Inject
    SharedPreferences mSharedPreferences;

    private Boolean mIsGroupedView;
    private TemplatesViewModelObserver mTemplateViewObserver;

    public void setTemplateViewObserver(TemplatesViewModelObserver templateViewObserver) {
        this.mTemplateViewObserver = templateViewObserver;
    }

    public void setIsGroupedView(boolean isGroupedView) {
        this.mIsGroupedView = isGroupedView;

        mSharedPreferences.edit()
                .putBoolean(SHARED_PREFERENCE_TEMPLATES_IS_GROUP_VIEW, mIsGroupedView)
                .apply();
    }

    @Override
    public LiveData<PagedList<CategoryTemplateItem>> getItems(int pageSize) {
        if (mIsGroupedView == null) {
            mIsGroupedView = mSharedPreferences.getBoolean(SHARED_PREFERENCE_TEMPLATES_IS_GROUP_VIEW, false);
        }

        DataSource.Factory<Integer, CategoryTemplateItem> factory =
                mTemplateRepository.getTemplates(mIsGroupedView);
        return new LivePagedListBuilder<>(factory, pageSize).build();
    }

    public void createProductList() {
        mProductListRepository.createNewList(new AsyncResultListener<ProductList>() {

            @Override
            public void onAsyncSuccess(ProductList productList) {

                ShoppingList shoppingList = mProductListRepository.getShoppingList(productList.getListID());
                for (CategoryTemplateItem item : mSelectedItems) {
                    shoppingList.addProductFromTemplate(item.getTemplate(), null);
                }

                if (mTemplateViewObserver != null) {
                    mTemplateViewObserver.onCreateProductList(productList.getListID());
                }
            }

            @Override
            public void onAsyncError(Exception e) {

            }
        });
    }

    public interface TemplatesViewModelObserver {
        void onCreateProductList(UUID listID);
    }
}
