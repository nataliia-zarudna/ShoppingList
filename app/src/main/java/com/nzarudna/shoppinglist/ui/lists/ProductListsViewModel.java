package com.nzarudna.shoppinglist.ui.lists;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nzarudna.shoppinglist.product.ProductList;
import com.nzarudna.shoppinglist.product.ProductListWithStatistics;
import com.nzarudna.shoppinglist.product.ShoppingList;
import com.nzarudna.shoppinglist.product.ShoppingListException;
import com.nzarudna.shoppinglist.product.ShoppingListRepository;

import javax.inject.Inject;

/**
 * Created by Nataliia on 19.01.2018.
 */

public class ProductListsViewModel extends ViewModel {

    @Inject
    ShoppingListRepository mShoppingListRepository;

    private ProductListViewModelObserver mObserver;

    public void setObserver(ProductListViewModelObserver observer) {
        this.mObserver = observer;
    }

    public LiveData<PagedList<ProductListWithStatistics>> getList(int pageSize) {
        try {
            DataSource.Factory<Integer, ProductListWithStatistics> listFactory
                    = mShoppingListRepository.getLists(ProductList.STATUS_ACTIVE, ProductList.SORT_LISTS_BY_NAME);

            return new LivePagedListBuilder<Integer, ProductListWithStatistics>(listFactory, pageSize).build();

        } catch (ShoppingListException e) {
            //TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public void removeList(ProductListWithStatistics productList) {
        mShoppingListRepository.removeList(productList.getListID());
    }

    public void createList() {

        mShoppingListRepository.createList(new ShoppingListRepository.OnProductListCreateListener() {
            @Override
            public void onCreate(int productListID) {

                if (mObserver != null) {
                    mObserver.startEditProductsListActivity(productListID);
                }
            }
        });
    }

    public interface ProductListViewModelObserver {

        void startEditProductsListActivity(int productsListID);
    }
}
