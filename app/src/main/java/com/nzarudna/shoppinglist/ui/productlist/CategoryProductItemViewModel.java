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

public abstract class CategoryProductItemViewModel extends ViewModel implements Observable {

    @Inject
    ProductListRepository mProductListRepository;

    private ShoppingList mShoppingList;
    private PropertyChangeRegistry mPropertyChangeRegistry = new PropertyChangeRegistry();
    protected CategoryProductItemViewModelObserver mObserver;
    private int mCurrentPosition;

    @Bindable
    protected CategoryProductItem mCategoryProductItem;

    public abstract void onProductClick();

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

    public boolean onProductLongClick() {
        if (mObserver != null) {
            mObserver.showContextMenu(mCurrentPosition);
        }
        return true;
    }

    public Product getProduct() throws ShoppingListException {
        checkIsProductType();
        return mCategoryProductItem.getProduct();
    }

    public void markProductAs(int status) throws ShoppingListException {
        checkIsProductType();
        updateProductStatus(status);
    }

    protected void updateProductStatus(int newStatus) {
        Product product = mCategoryProductItem.getProduct();
        product.setStatus(newStatus);
        mShoppingList.updateProduct(product, new ShoppingList.OnSaveProductCallback() {
            @Override
            public void onSaveProduct() {
                mPropertyChangeRegistry.notifyChange(CategoryProductItemViewModel.this, BR._all);
            }
        });
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
        mShoppingList.updateProduct(updatedProduct, new ShoppingList.OnSaveProductCallback() {
            @Override
            public void onSaveProduct() {
                if (mObserver != null) {
                    mObserver.showSuccessSaveMessage();
                }
            }
        });
    }

    public void onMoveItem(CategoryProductItemViewModel prevViewModel, CategoryProductItemViewModel nextViewModel) throws ShoppingListException {

        Product prevProduct = (prevViewModel != null) ? prevViewModel.getProduct() : null;
        Product nextProduct = (nextViewModel != null) ? nextViewModel.getProduct() : null;
        mShoppingList.moveProduct(mCategoryProductItem.getProduct(), nextProduct, prevProduct);
    }

    public interface CategoryProductItemViewModelObserver {
        void showContextMenu(int productPosition);

        void showSuccessSaveMessage();

        void openEditProductDialog(Product product);
    }
}
