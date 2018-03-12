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
import com.nzarudna.shoppinglist.ui.ObservableViewModel;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 28.01.2018.
 */

public  class CategoryProductItemViewModel extends RecyclerItemViewModel<CategoryProductItem> {

    @Inject
    ProductListRepository mProductListRepository;

    private ShoppingList mShoppingList;
    //protected CategoryProductItemViewModelObserver mObserver;
    //private int mCurrentPosition;

    //@Bindable
    //protected CategoryProductItem mCategoryProductItem;

    //public abstract void onProductClick();

    public void setShoppingList(ShoppingList shoppingList) {
        this.mShoppingList = shoppingList;
    }

    /*public void setCategoryProductItem(CategoryProductItem categoryProductItem) {
        this.mCategoryProductItem = categoryProductItem;

        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }*/

   /* public void setObserver(CategoryProductItemViewModelObserver observer) {
        this.mObserver = observer;
    }*/

    /*public void setCurrentPosition(int currentPosition) {
        this.mCurrentPosition = currentPosition;
    }*/

    /*public String getProductName() {
        if (mCategoryProductItem != null) {
            String name = mCategoryProductItem.getProduct().getName();
            if (mCategoryProductItem.getProduct().getStatus() == Product.BOUGHT) {
                return "<u>" + name + "</u>";
            }
            return name;
        } else {
            return "";
        }
    }*/

    @Override
    public String getItemName() {
        if (mItem != null) {
            if (mItem.getType().equals(CategoryProductItem.TYPE_PRODUCT)) {
                String name = mItem.getProduct().getName();
                if (mItem.getProduct().getStatus() == Product.BOUGHT) {
                    return "<u>" + name + "</u>";
                }
                return name;
            } else {
                return mItem.getCategory().getName();
            }
        } else {
            return "";
        }
    }

    @Override
    public boolean hasContextMenu() {
        return mItem.getType().equals(CategoryProductItem.TYPE_PRODUCT);
    }

    @Override
    public void removeItem() {
        //checkIsProductType();
        mShoppingList.removeProduct(mItem.getProduct());
    }

    /*public String getCategoryName() {
        return (mCategoryProductItem != null) ? mCategoryProductItem.getCategory().getName() : "";
    }*/

    public String getProductCount() {
        if (mItem != null) {
            double count = mItem.getProduct().getCount();
            return FormatUtils.format(count);
        } else {
            return "";
        }
    }

    public String getProductUnitInfo() {
        StringBuilder info = new StringBuilder();
        if (mItem != null) {

            double count = mItem.getProduct().getCount();
            if (count > 0) {
                info.append(FormatUtils.format(count));
                info.append(" ");
            }

            if (mItem.getUnit() != null) {
                info.append(mItem.getUnit().getName());
            }
        }
        return info.toString().trim();
    }

    public boolean isGreyedProductName() {
        return mItem.getProduct().getStatus() == Product.ABSENT;
    }

    /*public boolean onProductLongClick() {
        if (mObserver != null) {
            mObserver.showItemContextMenu(mItem
        }
        return true;
    }*/

    public Product getProduct() throws ShoppingListException {
        checkIsProductType();
        return mItem.getProduct();
    }

    public void markProductAs(int status) throws ShoppingListException {
        checkIsProductType();
        updateProductStatus(status);
    }

    protected void updateProductStatus(int newStatus) {
        Product product = mItem.getProduct();
        product.setStatus(newStatus);
        mShoppingList.updateProduct(product, new ShoppingList.OnSaveProductCallback() {
            @Override
            public void onSaveProduct() {
                mPropertyChangeRegistry.notifyChange(CategoryProductItemViewModel.this, BR._all);
            }
        });
    }

    /*public void removeProduct() throws ShoppingListException {
        checkIsProductType();
        mShoppingList.removeProduct(mItem.getProduct());
    }*/

    private void checkIsProductType() throws ShoppingListException {
        if (!CategoryProductItem.TYPE_PRODUCT.equals(mItem.getType())) {
            throw new ShoppingListException("CategoryProductItem has category type");
        }
    }

    /*public void onMenuButtonClick() {
        if (mObserver != null) {
            mObserver.showContextMenu(mCurrentPosition);
        }
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
    }*/

    public void onMoveItem(CategoryProductItemViewModel prevViewModel, CategoryProductItemViewModel nextViewModel) throws ShoppingListException {

        Product prevProduct = (prevViewModel != null) ? prevViewModel.getProduct() : null;
        Product nextProduct = (nextViewModel != null) ? nextViewModel.getProduct() : null;
        mShoppingList.moveProduct(mItem.getProduct(), nextProduct, prevProduct);
    }

    /*public interface CategoryProductItemViewModelObserver {
        void showContextMenu(int productPosition);

        void showSuccessSaveMessage();

        void openEditProductDialog(Product product);
    }*/
}
