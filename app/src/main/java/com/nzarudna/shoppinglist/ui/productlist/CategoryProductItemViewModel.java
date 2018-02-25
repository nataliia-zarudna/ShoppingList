package com.nzarudna.shoppinglist.ui.productlist;

import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;

import javax.inject.Inject;

/**
 * Created by Nataliia on 28.01.2018.
 */

public class CategoryProductItemViewModel extends ViewModel implements Observable {

    @Inject
    ProductListRepository mProductListRepository;
    private ShoppingList mShoppingList;

    @Bindable
    private CategoryProductItem mCategoryProductItem;

    private PropertyChangeRegistry mPropertyChangeRegistry = new PropertyChangeRegistry();

    public void setShoppingList(ShoppingList shoppingList) {
        this.mShoppingList = shoppingList;
    }

    public void setCategoryProductItem(CategoryProductItem categoryProductItem) {
        this.mCategoryProductItem = categoryProductItem;

        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public String getName() {
        if (mCategoryProductItem != null) {
            if (CategoryProductItem.TYPE_PRODUCT.equals(mCategoryProductItem.getType())) {
                String name = mCategoryProductItem.getProduct().getName();
                if (mCategoryProductItem.getProduct().getStatus() == Product.BOUGHT) {
                    return "<u>" + name + "</u>";
                }
                return name;

            } else {
                return mCategoryProductItem.getCategory().getName();
            }
        } else {
            return "";
        }
    }

    public int getProductNameColor() {
        return mCategoryProductItem.getProduct().getStatus() == Product.ABSENT
                ? android.R.color.darker_gray : android.R.color.black;
    }

    public boolean isGreyedProductName() {
        return mCategoryProductItem.getProduct().getStatus() == Product.ABSENT;
    }

    public void onProductClick() {
        if (!CategoryProductItem.TYPE_PRODUCT.equals(mCategoryProductItem.getType())) {
            return;
        }

        Product product = mCategoryProductItem.getProduct();
        int[] productStatusChain = new int[] {Product.TO_BUY, Product.BOUGHT, Product.ABSENT};
        int newStatus = Product.TO_BUY;
        for (int i = 0; i < productStatusChain.length; i++) {
            if (productStatusChain[i] == product.getStatus()) {
                newStatus = productStatusChain[(i + 1) % productStatusChain.length];
                break;
            }
        }
        product.setStatus(newStatus);
        mShoppingList.updateProduct(product, new ShoppingList.onUpdateProductCallback() {
            @Override
            public void onUpdateProduct() {
                mPropertyChangeRegistry.notifyChange(CategoryProductItemViewModel.this, BR._all);
            }
        });
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mPropertyChangeRegistry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mPropertyChangeRegistry.remove(onPropertyChangedCallback);
    }
}
