package com.nzarudna.shoppinglist.ui.productlist.edit.template;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItemWithListStatistics;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Nataliia on 04.03.2018.
 */

public class ChooseTemplateViewModel extends ViewModel {

    @Inject
    ProductTemplateRepository mTemplateRepository;

    @Inject
    ProductListRepository mProductListRepository;

    private UUID mProductListID;
    private ShoppingList mShoppingList;

    public void setProductListID(UUID productListID) {
        this.mProductListID = productListID;
        mShoppingList = mProductListRepository.getShoppingList(productListID);
    }

    public LiveData<PagedList<CategoryTemplateItemWithListStatistics>> getTemplates(boolean isGroupedView, int pageSize) {

        DataSource.Factory<Integer, CategoryTemplateItemWithListStatistics> templatesDSFactory =
                mTemplateRepository.getTemplates(isGroupedView, mProductListID);
        return new LivePagedListBuilder<>(templatesDSFactory, pageSize).build();
    }

}
