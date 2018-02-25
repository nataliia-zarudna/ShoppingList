package com.nzarudna.shoppinglist.ui.productlist;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.ShoppingListException;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Nataliia on 28.01.2018.
 */

public class ProductListViewModel extends ViewModel implements Observable {

    @Inject
    ProductListRepository mProductListRepository;
    private PropertyChangeRegistry mRegistry = new PropertyChangeRegistry();

    @Bindable
    protected ProductList mProductList;

    protected UUID mProductListID;
    protected ShoppingList mShoppingList;

    public void setProductListID(UUID productListID) {
        this.mProductListID = productListID;

        mShoppingList = mProductListRepository.getShoppingList(productListID);
    }

    public LiveData<PagedList<CategoryProductItem>> getProducts(int pageSize) {
        return getProducts(mProductList.getSorting(), mProductList.isGroupedView(), pageSize);
    }

    public LiveData<PagedList<CategoryProductItem>> getProducts(int productSort, int pageSize) {
        return getProducts(productSort, mProductList.isGroupedView(), pageSize);
    }

    public LiveData<PagedList<CategoryProductItem>> getProducts(boolean isGroupedView, int pageSize) {
        return getProducts(mProductList.getSorting(), isGroupedView, pageSize);
    }

    private LiveData<PagedList<CategoryProductItem>> getProducts(int productSort,
                                                                boolean isGroupedView, int pageSize) {

        DataSource.Factory<Integer, CategoryProductItem> productsFactory = null;
        try {
            productsFactory = mShoppingList.getProducts(productSort, isGroupedView);
        } catch (ShoppingListException e) {
            //TODO: handle error
            e.printStackTrace();
        }
        return new LivePagedListBuilder<>(productsFactory, pageSize).build();
    }

    public LiveData<ProductList> getProductListData() {
        return mShoppingList.getListData();
    }

    public void setProductListData(ProductList productList) {
        mProductList = productList;
        mRegistry.notifyChange(this, BR._all);
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mRegistry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mRegistry.remove(onPropertyChangedCallback);
    }

    public void onDeleteMenuItemSelected() {
        mProductListRepository.archiveList(mProductListID);
    }

    public void onArchiveMenuItemSelected() {
        mProductListRepository.removeList(mProductListID);
    }

    public void removeBoughtProducts() {
        mShoppingList.removeProductsByStatus(Product.BOUGHT);
    }

    public void markProductsAs(int status) {
        mShoppingList.updateProductsStatus(status);
    }
}
