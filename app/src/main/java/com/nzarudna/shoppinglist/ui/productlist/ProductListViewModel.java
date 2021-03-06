package com.nzarudna.shoppinglist.ui.productlist;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.databinding.Bindable;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.ResultCallback;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.ProductStatistics;
import com.nzarudna.shoppinglist.model.product.list.ProductList;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ProductListWithStatistics;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerViewModel;
import com.nzarudna.shoppinglist.utils.ShareUtils;

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
    protected ProductStatistics mProductStatistics;

    protected UUID mProductListID;
    protected ShoppingList mShoppingList;

    @ProductList.ProductSorting
    protected int mSorting;

    protected boolean mIsGroupedView;
    private boolean mUseCustomSorting;

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
        mUseCustomSorting = mProductList.isUseCustomSorting();
        mIsGroupedView = mProductList.isGroupedView();

        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public ProductList getProductList() {
        return mProductList;
    }

    public void setSorting(int sorting) {
        this.mSorting = sorting;
        this.mUseCustomSorting = false;

        mProductList.setSorting(sorting);
        mShoppingList.updateProductList(mProductList, null);
    }

    public void setUseCustomSorting(boolean useCustomSorting) {
        this.mUseCustomSorting = useCustomSorting;

        mProductList.setUseCustomSorting(useCustomSorting);
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

        DataSource.Factory<Integer, CategoryProductItem> productsFactory
                = mShoppingList.getProducts(mUseCustomSorting, mSorting, mIsGroupedView, null);
        return new LivePagedListBuilder<>(productsFactory, pageSize).build();
    }

    public void removeBoughtProducts() {
        mShoppingList.removeProductsByStatus(Product.BOUGHT, null);
    }

    public void markProductsAs(int status) {
        mShoppingList.updateProductsStatus(status, null);
    }

    public LiveData<ProductListWithStatistics> getProductStatisticsLiveData() {
        return mShoppingList.getProductListStatistics();
    }

    public ProductStatistics getProductStatistics() {
        return mProductStatistics;
    }

    public void setProductStatistics(ProductStatistics productStatistics) {
        this.mProductStatistics = productStatistics;
    }

    public void getShareProductsText(ResultCallback<String> shareTextCallback) {
        mShoppingList.getAllUnboughtProducts(categoryProductItems -> {
            shareTextCallback.onResult(ShareUtils.formatProductList(categoryProductItems));
        });
    }

    public void removeList() {
        mProductListRepository.removeList(mProductListID, null);
    }

    public void archiveList() {
        mProductListRepository.updateListStatus(mProductListID, ProductList.STATUS_ARCHIVED, null);
    }

}
