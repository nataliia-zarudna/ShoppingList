package com.nzarudna.shoppinglist.ui.productlist.editproduct;

import android.arch.lifecycle.LiveData;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.product.CategoryProductItem;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.product.list.ProductListRepository;
import com.nzarudna.shoppinglist.model.product.list.ShoppingList;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.ui.FormatUtils;
import com.nzarudna.shoppinglist.ui.templates.editdialog.BaseEditTemplateViewModel;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by nsirobaba on 2/26/18.
 */

public class EditProductViewModel extends BaseEditTemplateViewModel<CategoryProductItem> {

    @Inject
    ProductTemplateRepository mTemplateRepository;
    @Inject
    ProductListRepository mProductListRepository;

    private Product mProduct;
    private ShoppingList mShoppingList;

    public void setListID(UUID listID) {
        mShoppingList = mProductListRepository.getShoppingList(listID);
    }

    @Override
    public void setItem(CategoryProductItem item) {
        super.setItem(item);
        mProduct = mItem.getProduct();

        if (mShoppingList == null) {
            setListID(mProduct.getListID());
        }
    }

    @Override
    protected CategoryProductItem createItemObject() {
        mProduct = new Product();
        return new CategoryProductItem(mProduct, null);
    }

    @Override
    public String getName() {
        return mProduct.getName();
    }

    @Override
    public void setName(String name) {
        mProduct.setName(name);
    }

    @Override
    protected String getUniqueNameValidationMessage() {
        return mResourceResolver.getString(R.string.product_unique_name_validation_message);
    }

    @Override
    protected void updateItem(AsyncResultListener asyncResultListener) {
        mShoppingList.updateProduct(mProduct, asyncResultListener);
    }

    @Override
    protected void createItem(AsyncResultListener asyncResultListener) {
        mShoppingList.addProduct(mProduct, asyncResultListener);
    }

    public LiveData<List<ProductTemplate>> getNameAutocompleteList(String filterValue) {
        return mTemplateRepository.getTemplatesByNameLike(filterValue, mProduct.getListID());
    }

    public String getProductCount() {
        if (mProduct != null && mProduct.getCount() > 0) {
            return FormatUtils.format(mProduct.getCount());
        } else {
            return "";
        }
    }

    public void onCountChange(CharSequence countSequence, int i, int i1, int i2) {
        String countStr = countSequence.toString().trim();
        double count = !countStr.isEmpty() ? Double.valueOf(countStr) : 0;
        mProduct.setCount(count);
    }

    public void onChooseProductTemplate(ProductTemplate template) {
        mProduct.setTemplateID(template.getTemplateID());

        mProduct.setCategoryID(template.getCategoryID());
        mProduct.setUnitID(template.getUnitID());
    }

    @Override
    public UUID getCategoryID() {
        return mProduct.getCategoryID();
    }

    @Override
    public UUID getUnitID() {
        return mProduct.getUnitID();
    }

    @Override
    public void setUnit(Unit selectedUnit) {
        mProduct.setUnitID(selectedUnit.getUnitID());
    }

    @Override
    public void setCategoryID(UUID categoryID) {
        mProduct.setCategoryID(categoryID);
    }
}
