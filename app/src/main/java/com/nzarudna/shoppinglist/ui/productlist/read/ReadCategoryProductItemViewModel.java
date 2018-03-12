package com.nzarudna.shoppinglist.ui.productlist.read;

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
import com.nzarudna.shoppinglist.ui.productlist.CategoryProductItemViewModel;

import javax.inject.Inject;

/**
 * Created by Nataliia on 28.01.2018.
 */

public class ReadCategoryProductItemViewModel extends CategoryProductItemViewModel {

    @Override
    public void onItemClick() {
        if (!CategoryProductItem.TYPE_PRODUCT.equals(mItem.getType())) {
            return;
        }

        Product product = mItem.getProduct();
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
}
