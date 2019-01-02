package com.nzarudna.shoppinglist.ui.productlist;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.exception.ShoppingListException;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.ui.FormatUtils;
import com.nzarudna.shoppinglist.ui.recyclerui.RecyclerItemViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 28.01.2018.
 */

public  class CategoryProductItemViewModel extends RecyclerItemViewModel<CategoryProductItem> {

    @Inject
    ProductListRepository mProductListRepository;

    private ShoppingList mShoppingList;

    public void setShoppingList(ShoppingList shoppingList) {
        this.mShoppingList = shoppingList;
    }

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
    public void removeItem(AsyncListener asyncListener) {
        mShoppingList.removeProduct(mItem.getProduct(), asyncListener);
    }

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
        mShoppingList.updateProduct(product, new AsyncResultListener<Product>() {
            @Override
            public void onAsyncSuccess(Product resultProduct) {
                mPropertyChangeRegistry.notifyChange(CategoryProductItemViewModel.this, BR._all);
            }

            @Override
            public void onAsyncError(Exception e) {

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

    public void onMoveItem(CategoryProductItemViewModel prevViewModel, CategoryProductItemViewModel nextViewModel) throws ShoppingListException {

        Product prevProduct = (prevViewModel != null) ? prevViewModel.getProduct() : null;
        Product nextProduct = (nextViewModel != null) ? nextViewModel.getProduct() : null;
        mShoppingList.moveProduct(mItem.getProduct(), prevProduct, nextProduct, null);
    }
}
