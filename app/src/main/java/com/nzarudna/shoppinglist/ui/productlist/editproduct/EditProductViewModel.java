package com.nzarudna.shoppinglist.ui.productlist.editproduct;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
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
import com.nzarudna.shoppinglist.ui.FormatUtils;
import com.nzarudna.shoppinglist.ui.ObservableViewModel;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by nsirobaba on 2/26/18.
 */

public class EditProductViewModel extends ObservableViewModel {

    @Inject
    ProductTemplateRepository mTemplateRepository;
    @Inject
    UnitRepository mUnitRepository;
    @Inject
    CategoryRepository mCategoryRepository;

    @Bindable
    private MutableLiveData<Product> mProduct;

    public void setProductInfo(Product product, UUID listID) {
        if (product == null) {
            product = new Product(null);
            product.setListID(listID);
        }
        this.mProduct = new MutableLiveData<>();
        mProduct.setValue(product);

        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }

    public LiveData<Product> getProduct() {
        return mProduct;
    }

    public LiveData<List<ProductTemplate>> getNameAutocompleteList(String filterValue) {
        return mTemplateRepository.getTemplatesByNameLike(filterValue, mProduct.getValue().getListID());
    }

    public String getProductName() {
        return mProduct != null ? mProduct.getValue().getName() : "";
    }

    public void setProductName(String productName) {
        mProduct.getValue().setName(productName);
    }

    public String getProductCount() {
        if (mProduct != null && mProduct.getValue().getCount() > 0) {
            return FormatUtils.format(mProduct.getValue().getCount());
        } else {
            return "";
        }
    }

    public void onCountChange(CharSequence countStr, int i, int i1, int i2) {
        double count = Double.valueOf(countStr.toString());
        mProduct.getValue().setCount(count);
    }

    public UUID getProductCategoryID() {
        return mProduct != null ? mProduct.getValue().getCategoryID() : null;
    }

    public UUID getProductUnitID() {
        return mProduct != null ? mProduct.getValue().getUnitID() : null;
    }

    public String getDialogTitle() {
        return getProductName();
    }

    public LiveData<List<Unit>> getUnitList() {
        return mUnitRepository.getAvailableUnits();
    }

    public LiveData<List<Category>> getCategoryList() {
        return mCategoryRepository.getAvailableCategories();
    }

    public void onUnitSelect(Unit unit) {
        if (mProduct.getValue() != null) {
            mProduct.getValue().setUnitID(unit.getUnitID());
        }
    }

    public void onCategorySelect(Category category) {
        if (mProduct.getValue() != null) {
            mProduct.getValue().setCategoryID(category.getCategoryID());
        }
    }

    public void onChooseProductTemplate(ProductTemplate template) {
        Product product = mProduct.getValue();
        product.setTemplateID(template.getTemplateID());

        product.setCategoryID(template.getCategoryID());
        product.setUnitID(template.getUnitID());

        mProduct.postValue(product);
        mPropertyChangeRegistry.notifyChange(this, BR._all);
    }
}
