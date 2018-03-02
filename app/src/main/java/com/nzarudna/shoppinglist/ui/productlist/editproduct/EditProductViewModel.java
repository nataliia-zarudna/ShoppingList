package com.nzarudna.shoppinglist.ui.productlist.editproduct;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.nzarudna.shoppinglist.BR;
import com.nzarudna.shoppinglist.model.category.Category;
import com.nzarudna.shoppinglist.model.category.CategoryRepository;
import com.nzarudna.shoppinglist.model.product.Product;
import com.nzarudna.shoppinglist.model.template.ProductTemplate;
import com.nzarudna.shoppinglist.model.template.ProductTemplateRepository;
import com.nzarudna.shoppinglist.model.unit.Unit;
import com.nzarudna.shoppinglist.model.unit.UnitRepository;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by nsirobaba on 2/26/18.
 */

public class EditProductViewModel extends ViewModel implements Observable {

    @Inject
    ProductTemplateRepository mTemplateRepository;
    @Inject
    UnitRepository mUnitRepository;
    @Inject
    CategoryRepository mCategoryRepository;

    @Bindable
    private Product mProduct;

    private PropertyChangeRegistry mRegistry = new PropertyChangeRegistry();

    public void setProductInfo(Product product, UUID listID) {
        if (product == null) {
            product = new Product(null);
            product.setListID(listID);
        }
        this.mProduct = product;

        mRegistry.notifyChange(this, BR._all);
    }

    public Product getProduct() {
        return mProduct;
    }

    public LiveData<List<ProductTemplate>> getNameAutocompleteList(String filterValue) {
        return mTemplateRepository.getTemplatesByNameLike(filterValue, mProduct.getListID());
    }

    public String getProductName() {
        return mProduct != null ? mProduct.getName() : "";
    }

    public void setProductName(String productName) {
        mProduct.setName(productName);
    }

    public String getProductCount() {
        if (mProduct != null && mProduct.getCount() > 0) {
            DecimalFormat formatter = new DecimalFormat("#.###");
            return formatter.format(mProduct.getCount());
        } else {
            return "";
        }
    }

    public void onCountChange(CharSequence countStr, int i, int i1, int i2) {
        double count = Double.valueOf(countStr.toString());
        mProduct.setCount(count);
    }

    public UUID getProductCategoryID() {
        return mProduct != null ? mProduct.getCategoryID() : null;
    }

    public UUID getProductUnitID() {
        return mProduct != null ? mProduct.getUnitID() : null;
    }

    public String getDialogTitle() {
        return getProductName();
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mRegistry.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        mRegistry.remove(onPropertyChangedCallback);
    }

    public LiveData<List<Unit>> getUnitList() {
        return mUnitRepository.getAvailableUnits();
    }

    public LiveData<List<Category>> getCategoryList() {
        return mCategoryRepository.getAvailableCategories();
    }

    public void onUnitSelect(Unit unit) {
        if (mProduct != null) {
            mProduct.setUnitID(unit.getUnitID());
        }
    }

    public void onCategorySelect(Category category) {
        if (mProduct != null) {
            mProduct.setCategoryID(category.getCategoryID());
        }
    }
}
