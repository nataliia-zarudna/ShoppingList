package com.nzarudna.shoppinglist.ui.productlists;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.model.ShoppingListException;

import javax.inject.Inject;

import static com.nzarudna.shoppinglist.model.product.list.ProductListRepository.SORT_LISTS_BY_NAME;

/**
 * Created by Nataliia on 19.01.2018.
 */

public class ProductListsViewModel extends ViewModel {

    @Inject
    ProductListRepository mProductListRepository;

    private ProductListViewModelObserver mObserver;

    public void setObserver(ProductListViewModelObserver observer) {
        this.mObserver = observer;
    }

    public LiveData<PagedList<ProductListWithStatistics>> getList(int pageSize) {
        try {
            DataSource.Factory<Integer, ProductListWithStatistics> listFactory
                    = mProductListRepository.getLists(ProductList.STATUS_ACTIVE, SORT_LISTS_BY_NAME);

            return new LivePagedListBuilder<>(listFactory, pageSize).build();

        } catch (ShoppingListException e) {
            //TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public void removeList(ProductListWithStatistics productList) {
        mProductListRepository.removeList(productList.getListID());
    }

    public void createList() {

        mProductListRepository.createList(new ProductListRepository.OnProductListCreateListener() {
            @Override
            public void onCreate(int productListID) {

                if (mObserver != null) {
                    mObserver.startEditProductListActivity(productListID);
                }
            }
        });
    }

    public interface ProductListViewModelObserver {

        void startEditProductListActivity(int productListID);
    }
}