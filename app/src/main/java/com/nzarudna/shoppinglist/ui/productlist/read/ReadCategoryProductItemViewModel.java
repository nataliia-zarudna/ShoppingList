package com.nzarudna.shoppinglist.ui.productlist.read;

import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.ui.productlist.CategoryProductItemViewModel;

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
        int[] productStatusChain = new int[]{Product.ACTIVE, Product.BOUGHT, Product.ABSENT};
        int newStatus = Product.ACTIVE;
        for (int i = 0; i < productStatusChain.length; i++) {
            if (productStatusChain[i] == product.getStatus()) {
                newStatus = productStatusChain[(i + 1) % productStatusChain.length];
                break;
            }
        }
        updateProductStatus(newStatus);
    }
}
