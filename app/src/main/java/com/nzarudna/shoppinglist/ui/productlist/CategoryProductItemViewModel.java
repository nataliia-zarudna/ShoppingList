package com.nzarudna.shoppinglist.ui.productlist;

import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.ShoppingListException;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.ui.FormatUtils;

import javax.inject.Inject;

/**
 * Created by Nataliia on 28.01.2018.
 */

public class CategoryProductItemViewModel extends ViewModel implements Observable {

    @Inject
    ProductListRepository mProductListRepository;

    private ShoppingList mShoppingList;
    private PropertyChangeRegistry mPropertyChangeRegistry = new PropertyChangeRegistry();
    private CategoryProductItemViewModelObserver mObserver;
    private int mCurrentPosition;

    @Bindable
    private CategoryProductItem mCategoryProductItem;

    public void setShoppingList(ShoppingList shoppingList) {
        this.mShoppingList = shoppingList;
    }

    public void setCategoryProductItem(CategoryProductItem categoryProductItem) {
        this.mCategoryProductItem = categoryProductItem;

        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public void setObserver(CategoryProductItemViewModelObserver observer) {
        this.mObserver = observer;
    }

    public void setCurrentPosition(int currentPosition) {
        this.mCurrentPosition = currentPosition;
    }

    public String getProductName() {
        if (mCategoryProductItem != null) {
            String name = mCategoryProductItem.getProduct().getName();
            if (mCategoryProductItem.getProduct().getStatus() == Product.BOUGHT) {
                return "<u>" + name + "</u>";
            }
            return name;
        } else {
            return "";
        }
    }

    public String getCategoryName() {
        return (mCategoryProductItem != null) ? mCategoryProductItem.getCategory().getName() : "";
    }

    public String getProductCount() {
        if (mCategoryProductItem != null) {
            double count = mCategoryProductItem.getProduct().getCount();
            return FormatUtils.format(count);
        } else {
            return "";
        }
    }

    public String getProductUnitInfo() {
        StringBuilder info = new StringBuilder();
        if (mCategoryProductItem != null) {

            double count = mCategoryProductItem.getProduct().getCount();
            if (count > 0) {
                info.append(FormatUtils.format(count));
                info.append(" ");
            }

            if (mCategoryProductItem.getUnit() != null) {
                info.append(mCategoryProductItem.getUnit().getName());
            }
        }
        return info.toString().trim();
    }

    public boolean isGreyedProductName() {
        return mCategoryProductItem.getProduct().getStatus() == Product.ABSENT;
    }

    public void onProductClick() {
        if (!CategoryProductItem.TYPE_PRODUCT.equals(mCategoryProductItem.getType())) {
            return;
        }

        Product product = mCategoryProductItem.getProduct();
        int[] productStatusChain = new int[]{Product.TO_BUY, Product.BOUGHT, Product.ABSENT};
        int newStatus = Product.TO_BUY;
        for (int i = 0; i < productStatusChain.length; i++) {
            if (productStatusChain[i] == product.getStatus()) {
                newStatus = productStatusChain[(i + 1) % productStatusChain.length];
                break;
            }
        }
        updateProductStatus(newStatus);
    }

    public boolean onProductLongClick() {
        if (mObserver != null) {
            mObserver.showContextMenu(mCurrentPosition);
        }
        return true;
    }

    private void updateProductStatus(int newStatus) {
        Product product = mCategoryProductItem.getProduct();
        product.setStatus(newStatus);
        mShoppingList.updateProduct(product, new ShoppingList.onUpdateProductCallback() {
            @Override
            public void onUpdateProduct() {
                mPropertyChangeRegistry.notifyChange(CategoryProductItemViewModel.this, BR._all);
            }
        });
    }

    public Product getProduct() throws ShoppingListException {
        checkIsProductType();
        return mCategoryProductItem.getProduct();
    }

    public void markProductAs(int status) throws ShoppingListException {
        checkIsProductType();
        updateProductStatus(status);
    }

    public void removeProduct() throws ShoppingListException {
        checkIsProductType();
        mShoppingList.removeProduct(mCategoryProductItem.getProduct());
    }

    private void checkIsProductType() throws ShoppingListException {
        if (!CategoryProductItem.TYPE_PRODUCT.equals(mCategoryProductItem.getType())) {
            throw new ShoppingListException("CategoryProductItem has category type");
        }
    }

    public void onMenuButtonClick() {
        if (mObserver != null) {
            mObserver.showContextMenu(mCurrentPosition);
        }
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mPropertyChangeRegistry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mPropertyChangeRegistry.remove(onPropertyChangedCallback);
    }

    public void onUpdateProductDone(Product updatedProduct) {
        mShoppingList.updateProduct(updatedProduct, new ShoppingList.onUpdateProductCallback() {
            @Override
            public void onUpdateProduct() {
                if (mObserver != null) {
                    mObserver.showSuccessSaveMessage();
                }
            }
        });
    }

    public interface CategoryProductItemViewModelObserver {
        void showContextMenu(int productPosition);

        void showSuccessSaveMessage();
    }
}
