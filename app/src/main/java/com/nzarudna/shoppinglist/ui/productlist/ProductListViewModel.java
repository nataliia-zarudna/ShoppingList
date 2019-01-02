package com.nzarudna.shoppinglist.ui.productlist;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.databinding.Bindable;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by Nataliia on 28.01.2018.
 */

public abstract class ProductListViewModel extends RecyclerViewModel<CategoryProductItem> {

    @Inject
    ProductListRepository mProductListRepository;

    @Bindable
    protected ProductList mProductList;

    protected UUID mProductListID;
    protected ShoppingList mShoppingList;

    @ProductList.ProductSorting
    protected int mSorting;

    protected boolean mIsGroupedView;

    public void setProductListID(UUID productListID) {
        this.mProductListID = productListID;

        mShoppingList = mProductListRepository.getShoppingList(productListID);
    }

    public UUID getProductListID() {
        return mProductListID;
    }

    public LiveData<ProductList> getProductListLiveData() {
        return mShoppingList.getListData();
    }

    public void setProductListData(ProductList productList) {
        mProductList = productList;

        mSorting = mProductList.getSorting();
        mIsGroupedView = mProductList.isGroupedView();

        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public ProductList getProductList() {
        return mProductList;
    }

    public void setSorting(int sorting) {
        this.mSorting = sorting;

        mProductList.setSorting(sorting);
        mShoppingList.updateProductList(mProductList, null);
    }

    public void setIsGroupedView(Boolean isGroupedView) {
        this.mIsGroupedView = isGroupedView;

        mProductList.setIsGroupedView(isGroupedView);
        mShoppingList.updateProductList(mProductList, null);
    }

    public String getListName() {
        return mProductList != null ? mProductList.getName() : "";
    }

    @Override
    public LiveData<PagedList<CategoryProductItem>> getItems(int pageSize) {

        DataSource.Factory<Integer, CategoryProductItem> productsFactory = null;
        try {
            productsFactory = mShoppingList.getProducts(mSorting, mIsGroupedView, null);
        } catch (ShoppingListException e) {
            //TODO: handle error
            e.printStackTrace();
        }
        return new LivePagedListBuilder<>(productsFactory, pageSize).build();
    }

    public void removeBoughtProducts() {
        mShoppingList.removeProductsByStatus(Product.BOUGHT, null);
    }

    public void markProductsAs(int status) {
        mShoppingList.updateProductsStatus(status, null);
    }
}
