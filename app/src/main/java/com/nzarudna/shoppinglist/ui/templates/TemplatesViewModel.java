package com.nzarudna.shoppinglist.ui.templates;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;

import java.util.UUID;

import javax.inject.Inject;

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

    private TemplatesViewModelObserver mTemplateViewObserver;

    public void setTemplateViewObserver(TemplatesViewModelObserver templateViewObserver) {
        this.mTemplateViewObserver = templateViewObserver;
    }

    @Override
    public LiveData<PagedList<CategoryTemplateItem>> getItems() {
        boolean isGroupedView = mSharedPreferences.getBoolean(SHARED_PREFERENCE_TEMPLATES_IS_GROUP_VIEW, false);
        return getItems(isGroupedView);
    }

    public LiveData<PagedList<CategoryTemplateItem>> getItems(boolean isGroupedView) {
        DataSource.Factory<Integer, CategoryTemplateItem> factory =
                mTemplateRepository.getTemplates(isGroupedView);
        return new LivePagedListBuilder<>(factory, DEFAULT_PAGE_SIZE).build();
    }

    public void createProductList() {
        mProductListRepository.createNewList(new ProductListRepository.OnProductListCreateListener() {
            @Override
            public void onCreateNewList(UUID productListID) {

                ShoppingList shoppingList = mProductListRepository.getShoppingList(productListID);
                for (CategoryTemplateItem item : mSelectedItems) {
                    shoppingList.addProductFromTemplate(item.getTemplate(), null);
                }

                if (mTemplateViewObserver != null) {
                    mTemplateViewObserver.onCreateProductList(productListID);
                }
            }
        });
    }

    public interface TemplatesViewModelObserver {
        void onCreateProductList(UUID listID);
    }
}
